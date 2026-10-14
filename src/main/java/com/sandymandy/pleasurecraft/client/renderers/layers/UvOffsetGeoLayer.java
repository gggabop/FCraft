package com.sandymandy.pleasurecraft.client.renderers.layers;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.cache.model.GeoQuad;
import com.geckolib.cache.model.GeoVertex;
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
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Redibuja un hueso con la misma textura del modelo pero desplazada (u, v): así una sola textura puede traer varios
 * materiales de armadura en tiras y cada pieza elige la suya. El desplazamiento sale del estado de render.
 * Sustituye al antiguo envoltorio de VertexConsumer, que GeckoLib 5 ya no permite.
 */
public class UvOffsetGeoLayer<T extends AbstractGirlEntity> extends CustomBoneTextureGeoLayer<T, Void, LivingEntityRenderState> {

    public UvOffsetGeoLayer(GeoRenderer<T, Void, LivingEntityRenderState> renderer, String boneName) {
        super(renderer, boneName, MissingTextureAtlasSprite.getLocation());
    }

    private Vec2 offsetFor(LivingEntityRenderState renderState) {
        Map<String, Vec2> offsets = renderState.getGeckolibData(GirlRenderData.UV_OFFSETS);
        return offsets == null ? null : offsets.get(this.boneName);
    }

    /** La textura es la propia del modelo. */
    @Override
    protected Identifier getTextureResource(LivingEntityRenderState renderState) {
        return this.renderer.getTextureLocation(renderState);
    }

    /** Solo hay que redibujar el hueso si tiene un desplazamiento asignado. */
    @Override
    public boolean shouldRenderBone(LivingEntityRenderState renderState) {
        return offsetFor(renderState) != null;
    }

    @Override
    public void addPerBoneRender(RenderPassInfo<LivingEntityRenderState> renderPassInfo, BiConsumer<GeoBone, PerBoneRender<LivingEntityRenderState>> consumer) {
        renderPassInfo.model().getBone(this.boneName).ifPresent(bone -> {
            if (GirlRenderData.isBoneVisible(renderPassInfo, bone)) super.addPerBoneRender(renderPassInfo, consumer);
        });
    }

    /** Igual que el de GeckoLib, pero pasando el desplazamiento a cada vértice. */
    @Override
    protected void renderBone(RenderPassInfo<LivingEntityRenderState> renderPassInfo, GeoBone bone, SubmitNodeCollector renderTasks) {
        LivingEntityRenderState renderState = renderPassInfo.renderState();
        Vec2 offset = offsetFor(renderState);
        if (offset == null || !(bone instanceof CuboidGeoBone cuboidBone)) return;

        Identifier texture = getTextureResource(renderState);
        int packedLight = renderPassInfo.packedLight();
        int packedOverlay = renderPassInfo.packedOverlay();
        int renderColor = renderPassInfo.renderColor();
        RenderType renderType = getRenderType(renderState, texture);
        if (renderType == null) return;

        final float uOffset = offset.x;
        final float vOffset = offset.y;

        renderTasks.submitCustomGeometry(renderPassInfo.poseStack(), renderType, (pose, buffer) -> {
            PoseStack poseStack = renderPassInfo.poseStack();

            poseStack.pushPose();
            poseStack.last().set(pose);
            bone.translateAwayFromPivotPoint(poseStack);

            for (GeoCube cube : cuboidBone.cubes) {
                poseStack.pushPose();
                renderCubeOffset(cube, poseStack, buffer, packedLight, packedOverlay, renderColor, uOffset, vOffset);
                poseStack.popPose();
            }

            poseStack.popPose();
        });
    }

    private void renderCubeOffset(GeoCube cube, PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                                  int renderColor, float uOffset, float vOffset) {
        cube.translateToPivotPoint(poseStack);
        cube.rotate(poseStack);
        cube.translateAwayFromPivotPoint(poseStack);

        Matrix3f normalisedPoseState = poseStack.last().normal();
        Matrix4f poseState = poseStack.last().pose();

        for (GeoQuad quad : cube.quads()) {
            if (quad == null)
                continue;

            Vector3f normal = normalisedPoseState.transform(quad.normalVec());

            RenderUtil.fixInvertedFlatCube(cube, normal);

            for (GeoVertex vertex : quad.vertices()) {
                Vector4f position = poseState.transform(new Vector4f(vertex.posX(), vertex.posY(), vertex.posZ(), 1));

                vertexConsumer.addVertex(position.x(), position.y(), position.z(), renderColor, vertex.texU() + uOffset,
                                         vertex.texV() + vOffset, packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
            }
        }
    }
}
