package com.sandymandy.pleasurecraft.networking.S2C;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.scene.SceneOption;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public record SceneOptionsS2CPacket(int entityId, List<SceneOption> options) implements CustomPacketPayload {
    public static final Type<SceneOptionsS2CPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "scene_options"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SceneOptionsS2CPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, SceneOptionsS2CPacket::entityId,
                    ByteBufCodecs.collection(ArrayList::new, SceneOption.CODEC), SceneOptionsS2CPacket::options,
                    SceneOptionsS2CPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
