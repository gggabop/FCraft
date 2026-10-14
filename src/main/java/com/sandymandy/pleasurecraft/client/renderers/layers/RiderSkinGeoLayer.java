package com.sandymandy.pleasurecraft.client.renderers.layers;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.cache.model.GeoQuad;
import com.geckolib.cache.model.cuboid.CuboidGeoBone;
import com.geckolib.cache.model.cuboid.GeoCube;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.PerBoneRender;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.CustomBoneTextureGeoLayer;
import com.geckolib.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.sandymandy.pleasurecraft.client.renderers.GirlRenderData;
import com.sandymandy.pleasurecraft.entity.girls.AbstractGirlEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Dibuja un hueso del modelo (el del jugador) y todos sus hijos con la skin de quien monta a la entidad,
 * o con una textura de reserva si no la monta nadie.
 * <p>
 * El hueso es la raíz de todo el cuerpo del jugador, así que no basta con redibujar sus cubos: hay que recorrer también
 * los huesos hijos (con su animación) y esconderlos del dibujo normal, que los pintaría con la textura de la entidad.
 */
public class RiderSkinGeoLayer<T extends AbstractGirlEntity> extends CustomBoneTextureGeoLayer<T, Void, LivingEntityRenderState> {
    private final Identifier fallbackTexture;

    public RiderSkinGeoLayer(GeoRenderer<T, Void, LivingEntityRenderState> renderer, String boneName, Identifier fallbackTexture) {
        super(renderer, boneName, fallbackTexture);
        this.fallbackTexture = fallbackTexture;
    }

    @Override
    protected Identifier getTextureResource(LivingEntityRenderState renderState) {
        Identifier skin = renderState.getGeckolibData(GirlRenderData.RIDER_SKIN);
        return skin != null ? skin : this.fallbackTexture;
    }

    /** Esconde del dibujo normal el hueso y todo lo que cuelga de él: lo dibuja esta capa. */
    @Override
    public void preRender(RenderPassInfo<LivingEntityRenderState> renderPassInfo, SubmitNodeCollector renderTasks) {
        if (!renderPassInfo.willRender()) return;

        renderPassInfo.addBoneUpdater((_, snapshots) -> snapshots.get(this.boneName)
                .filter(snapshot -> snapshot.getBone() instanceof CuboidGeoBone && shouldRenderBone(renderPassInfo.renderState()))
                .ifPresent(snapshot -> {
                    snapshot.skipRender(true);
                    snapshot.skipChildrenRender(true);
                }));
    }

    @Override
    public void addPerBoneRender(RenderPassInfo<LivingEntityRenderState> renderPassInfo, BiConsumer<GeoBone, PerBoneRender<LivingEntityRenderState>> consumer) {
        // Los modelos que no tienen este hueso simplemente no lo usan (sin avisos en el registro en cada fotograma)
        renderPassInfo.model().getBone(this.boneName).ifPresent(bone -> {
            if (GirlRenderData.isBoneVisible(renderPassInfo, bone)) super.addPerBoneRender(renderPassInfo, consumer);
        });
    }

    /** Un hueso con cubos y su posición relativa a la raíz, calculada mientras el modelo está animado. */
    private record PosedBone(CuboidGeoBone bone, PoseStack.Pose pose) {}

    @Override
    protected void renderBone(RenderPassInfo<LivingEntityRenderState> renderPassInfo, GeoBone bone, SubmitNodeCollector renderTasks) {
        if (!(bone instanceof CuboidGeoBone root)) return;

        LivingEntityRenderState renderState = renderPassInfo.renderState();
        RenderType renderType = getRenderType(renderState, getTextureResource(renderState));
        if (renderType == null) return;

        // Ahora (con el modelo animado) se calcula dónde queda cada hueso; el dibujo en sí ocurre más tarde, con el modelo ya suelto
        List<PosedBone> posedBones = new ArrayList<>();
        PoseStack local = new PoseStack();

        root.translateAwayFromPivotPoint(local);
        posedBones.add(new PosedBone(root, copyOf(local)));
        collectChildren(root, local, posedBones);

        int packedLight = renderPassInfo.packedLight();
        int packedOverlay = renderPassInfo.packedOverlay();
        int renderColor = renderPassInfo.renderColor();

        renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), renderType, (pose, buffer) -> {
            PoseStack poseStack = renderPassInfo.poseStack();

            for (PosedBone posed : posedBones) {
                poseStack.pushPose();
                poseStack.last().set(pose);
                poseStack.last().pose().mul(posed.pose().pose());
                poseStack.last().normal().mul(posed.pose().normal());

                for (GeoCube cube : posed.bone().cubes) {
                    poseStack.pushPose();
                    renderCube(cube, poseStack, buffer, packedLight, packedOverlay, renderColor, 1f, 1f);
                    poseStack.popPose();
                }

                poseStack.popPose();
            }
        });
    }

    /** Recorre los hijos como lo hace GeckoLib: respetando los huesos y ramas que estén ocultos. */
    private void collectChildren(GeoBone parent, PoseStack local, List<PosedBone> out) {
        // El hueso raíz lleva marcados como ocultos a sus hijos solo para el dibujo normal, no para esta capa
        if (!isRoot(parent) && parent.frameSnapshot != null && parent.frameSnapshot.areChildrenHidden()) return;

        for (GeoBone child : parent.children()) {
            local.pushPose();
            RenderUtil.prepMatrixForBone(local, child);

            if (child instanceof CuboidGeoBone cuboid && (child.frameSnapshot == null || !child.frameSnapshot.isHidden()))
                out.add(new PosedBone(cuboid, copyOf(local)));

            collectChildren(child, local, out);
            local.popPose();
        }
    }

    private boolean isRoot(GeoBone bone) {
        return bone.name().equals(this.boneName);
    }

    private static PoseStack.Pose copyOf(PoseStack stack) {
        PoseStack.Pose copy = new PoseStack.Pose();
        copy.set(stack.last());
        return copy;
    }

    /**
     * El modelo declara una textura de 64x64 (el formato de las skins) aunque la del cuerpo sea mucho mayor, así que sus
     * coordenadas ya valen directamente para la skin. GeckoLib escala por el cociente de tamaños y las sacaría de la skin.
     */
    @Override
    protected void renderQuad(GeoQuad quad, Matrix4f pose, Vector3f normal, VertexConsumer vertexConsumer, int packedLight,
                              int packedOverlay, int renderColor, float widthRatio, float heightRatio) {
        super.renderQuad(quad, pose, normal, vertexConsumer, packedLight, packedOverlay, renderColor, 1f, 1f);
    }
}
