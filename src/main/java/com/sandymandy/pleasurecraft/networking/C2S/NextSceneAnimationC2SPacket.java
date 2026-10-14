package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record NextSceneAnimationC2SPacket(int entityId, String finishedAnimation) implements CustomPacketPayload {
    public static final Type<NextSceneAnimationC2SPacket> ID =
            new Type<>(Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "sync_scene_progress"));

    public static final StreamCodec<RegistryFriendlyByteBuf, NextSceneAnimationC2SPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, NextSceneAnimationC2SPacket::entityId,
                    ByteBufCodecs.STRING_UTF8, NextSceneAnimationC2SPacket::finishedAnimation,
                    NextSceneAnimationC2SPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}