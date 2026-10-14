package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record CumKeybindC2SPacket(Boolean pressed) implements CustomPacketPayload {

    public static final Type<CumKeybindC2SPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "cum_keybind_pressed"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CumKeybindC2SPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, CumKeybindC2SPacket::pressed,
                    CumKeybindC2SPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
