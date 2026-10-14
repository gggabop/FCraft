package com.sandymandy.pleasurecraft.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class Messages {
    public void GlobleMessage(Level world, String messageContent) {
        Component message = Component.literal(messageContent);
        if (world.isClientSide()) return; // Don't run on client


        Objects.requireNonNull(world.getServer())
                .getPlayerList()
                .broadcastSystemMessage(message, false);
    }

    public void PlayerSpecificMessage(Player playerEntity, String messageContent){
        Component message = Component.literal(messageContent);

        playerEntity.sendSystemMessage(message);
    }

}