package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record AnimationSyncC2SPacket(int entityId, String animationState, boolean loopState, boolean holdState) implements CustomPacketPayload {

    public static final Type<AnimationSyncC2SPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "sync_override_animation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnimationSyncC2SPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, AnimationSyncC2SPacket::entityId,
                    ByteBufCodecs.STRING_UTF8, AnimationSyncC2SPacket::animationState,
                    ByteBufCodecs.BOOL, AnimationSyncC2SPacket::loopState,
                    ByteBufCodecs.BOOL, AnimationSyncC2SPacket::holdState,
                    AnimationSyncC2SPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
