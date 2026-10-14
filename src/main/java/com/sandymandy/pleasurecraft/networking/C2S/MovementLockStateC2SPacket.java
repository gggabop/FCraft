package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record MovementLockStateC2SPacket(int entityId, boolean data ) implements CustomPacketPayload {
    public static final Type<MovementLockStateC2SPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "scene_options"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MovementLockStateC2SPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, MovementLockStateC2SPacket::entityId,
                    ByteBufCodecs.BOOL, MovementLockStateC2SPacket::data,
                    MovementLockStateC2SPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
