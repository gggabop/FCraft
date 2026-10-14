package com.sandymandy.pleasurecraft.networking.C2S;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public record StartSceneC2SPacket(int entityId, String introAnim, List<String> slowAnim, List<String> fastAnim, String cumAnim) implements CustomPacketPayload {

    public static final Type<StartSceneC2SPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "start_scene_from_client"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StartSceneC2SPacket> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, StartSceneC2SPacket::entityId,
                    ByteBufCodecs.STRING_UTF8, StartSceneC2SPacket::introAnim,
                    ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8), StartSceneC2SPacket::slowAnim,
                    ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8), StartSceneC2SPacket::fastAnim,
                    ByteBufCodecs.STRING_UTF8, StartSceneC2SPacket::cumAnim,
                    StartSceneC2SPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
