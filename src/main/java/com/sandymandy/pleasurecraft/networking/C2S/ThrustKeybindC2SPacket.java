package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ThrustKeybindC2SPacket(Boolean held) implements CustomPacketPayload {

    public static final Type<ThrustKeybindC2SPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "thrust_keybind_held"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ThrustKeybindC2SPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, ThrustKeybindC2SPacket::held,
                    ThrustKeybindC2SPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}