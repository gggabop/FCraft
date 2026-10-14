package com.sandymandy.pleasurecraft.client.renderers;

import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.RenderPassInfo;
import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.client.renderers.layers.RiderSkinGeoLayer;
import com.sandymandy.pleasurecraft.client.renderers.layers.UvOffsetGeoLayer;
import com.sandymandy.pleasurecraft.entity.girls.AbstractGirlEntity;
import com.sandymandy.pleasurecraft.networking.C2S.BonePosSyncC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractGirlRenderer<T extends AbstractGirlEntity> extends GeoEntityRenderer<T, LivingEntityRenderState> {
    /** Hueso con forma de jugador: lleva la skin de quien monta (o esta textura si no monta nadie). */
    private static final String RIDER_BONE = "steve";
    private static final Identifier STEVE = Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "textures/player/steve.png");
    /** Huesos de armadura, que cambian de material desplazando la textura (ver AbstractGirlEntity#getArmorBones). */
    private static final List<String> ARMOR_BONES = List.of(
            "armorHelmet",
            "armorBoobs", "armorChest", "armorShoulderL", "armorShoulderR",
            "armorHip", "armorPantsLowL", "armorPantsUpL", "armorPantsLowR", "armorPantsUpR", "armorBootyL", "armorBootyR",
            "armorShoesL", "armorShoesR"
    );

    public AbstractGirlRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);

        withRenderLayer(new RiderSkinGeoLayer<>(this, RIDER_BONE, STEVE));
        for (String bone : ARMOR_BONES) {
            withRenderLayer(new UvOffsetGeoLayer<>(this, bone));
        }
    }

    /** Copia al estado de render lo que hace falta de la entidad: GeckoLib 5 la descarta antes de dibujar. */
    @Override
    public void addRenderData(T animatable, @Nullable Void relatedObject, LivingEntityRenderState renderState, float partialTick) {
        renderState.addGeckolibData(GirlRenderData.GIRL, animatable);
        renderState.addGeckolibData(GirlRenderData.BONE_VISIBILITY, new HashMap<>(animatable.boneVisibility));
        renderState.addGeckolibData(GirlRenderData.UV_OFFSETS, new HashMap<>(animatable.boneUVOffsets));

        Entity rider = animatable.getFirstPassenger();
        if (rider instanceof AbstractClientPlayer clientPlayer) {
            renderState.addGeckolibData(GirlRenderData.RIDER_SKIN, clientPlayer.getSkin().body().texturePath());
        }

        // En primera persona, quien monta no debe ver la cabeza por dentro
        Minecraft client = Minecraft.getInstance();
        boolean isFirstPerson = client.options.getCameraType().isFirstPerson();
        boolean isPlayerRider = rider != null && client.getCameraEntity() == rider;
        renderState.addGeckolibData(GirlRenderData.HIDE_HEAD, isFirstPerson && isPlayerRider);
    }

    /** Ajustes de huesos justo antes de dibujar: la cabeza sigue la mirada y se ocultan los huesos que toquen. */
    @Override
    public void adjustModelBonesForRender(RenderPassInfo<LivingEntityRenderState> renderPassInfo, BoneSnapshots snapshots) {
        // Cabeza. GeckoLib 4 daba el cabeceo y la guiñada con el signo cambiado, y aquí se mantiene el mismo comportamiento.
        float pitch = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
        float headYaw = renderPassInfo.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);
        snapshots.ifPresent("head", head -> {
            head.setRotX(-pitch * Mth.DEG_TO_RAD);
            head.setRotY(-headYaw * Mth.DEG_TO_RAD);
        });

        if (Boolean.TRUE.equals(renderPassInfo.getGeckolibData(GirlRenderData.HIDE_HEAD))) {
            snapshots.ifPresent("Head2", head -> head.skipRender(true));
        }

        // Visibilidad de huesos (ropa, armadura, etc.): ocultar un hueso oculta también a sus hijos
        Map<String, Boolean> visibility = renderPassInfo.getGeckolibData(GirlRenderData.BONE_VISIBILITY);
        if (visibility != null) {
            for (Map.Entry<String, Boolean> entry : visibility.entrySet()) {
                boolean visible = entry.getValue();
                snapshots.ifPresent(entry.getKey(), bone -> {
                    bone.skipRender(!visible);
                    bone.skipChildrenRender(!visible);
                });
            }
        }
    }

    /** Pide a GeckoLib la posición del hueso del pasajero en este fotograma. */
    @Override
    public void preRenderPass(RenderPassInfo<LivingEntityRenderState> renderPassInfo, SubmitNodeCollector renderTasks) {
        AbstractGirlEntity girl = renderPassInfo.getGeckolibData(GirlRenderData.GIRL);
        if (girl == null) return;

        renderPassInfo.addBonePositionListener(girl.getSceneManager().passengerBoneName, (worldPos, modelPos, localPos) -> {
            if (worldPos == null) return;
            girl.handlePassengerBone(worldPos);

            // Solo quien monta avisa al servidor (antes lo hacían todos los clientes que veían a la entidad, en cada fotograma)
            Minecraft client = Minecraft.getInstance();
            if (client.player != null && girl.getFirstPassenger() == client.player) {
                ClientPlayNetworking.send(new BonePosSyncC2SPacket(girl.getId(), worldPos));
            }
        });
    }
}