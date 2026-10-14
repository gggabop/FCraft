package com.sandymandy.pleasurecraft.screen;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.girls.AbstractGirlEntity;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class GirlInventoryScreenHandlerFactory implements ExtendedMenuProvider<PleasureCraft.GirlScreenData> {
    private final AbstractGirlEntity girl;

    public GirlInventoryScreenHandlerFactory(AbstractGirlEntity girl) {
        this.girl = girl;
    }

    // Called on the server -> sends data to client
    @Override
    public PleasureCraft.GirlScreenData getScreenOpeningData(ServerPlayer player) {
        return new PleasureCraft.GirlScreenData(girl.getId());
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("");
    }

    // Called on the client
    @Override
    public @Nullable AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new GirlInventoryScreenHandler(syncId, playerInventory, girl.getId());
    }
}