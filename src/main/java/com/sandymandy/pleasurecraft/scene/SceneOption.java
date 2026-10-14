package com.sandymandy.pleasurecraft.scene;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.ArrayList;
import java.util.List;

public record SceneOption(
        String name,
        String introAnim,
        List<String> slowAnim,
        List<String> fastAnim,
        String cumAnim
) {
    public static final StreamCodec<RegistryFriendlyByteBuf, SceneOption> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SceneOption::name,
            ByteBufCodecs.STRING_UTF8, SceneOption::introAnim,
            ByteBufCodecs.collection(ArrayList::new , ByteBufCodecs.STRING_UTF8), SceneOption::slowAnim,
            ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8), SceneOption::fastAnim,
            ByteBufCodecs.STRING_UTF8, SceneOption::cumAnim,
            SceneOption::new
    );

}