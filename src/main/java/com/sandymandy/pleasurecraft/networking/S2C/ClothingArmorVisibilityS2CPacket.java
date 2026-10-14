package com.sandymandy.pleasurecraft.networking.S2C;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public record ClothingArmorVisibilityS2CPacket(
        int entityId,
        List<Boolean> clothing,
        List<Boolean> armor
) implements CustomPacketPayload {

    public static final Type<ClothingArmorVisibilityS2CPacket> ID =
            new Type<>(Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "sync_clothing_armor"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClothingArmorVisibilityS2CPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ClothingArmorVisibilityS2CPacket::entityId,
                    ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.BOOL), ClothingArmorVisibilityS2CPacket::clothing,
                    ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.BOOL), ClothingArmorVisibilityS2CPacket::armor,
                    ClothingArmorVisibilityS2CPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
