package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record InventoryButtonC2SPacket(int entityId, String actionId) implements CustomPacketPayload {

    public static final Type<InventoryButtonC2SPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "girl_inventory_button"));

    public static final StreamCodec<RegistryFriendlyByteBuf, InventoryButtonC2SPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, InventoryButtonC2SPacket::entityId,
                    ByteBufCodecs.STRING_UTF8, InventoryButtonC2SPacket::actionId,
                    InventoryButtonC2SPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
