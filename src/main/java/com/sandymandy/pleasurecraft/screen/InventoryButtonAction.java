package com.sandymandy.pleasurecraft.screen;

import com.sandymandy.pleasurecraft.entity.girls.AbstractGirlEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiConsumer;

public record InventoryButtonAction(Component label, BiConsumer<AbstractGirlEntity, Player> action) {}