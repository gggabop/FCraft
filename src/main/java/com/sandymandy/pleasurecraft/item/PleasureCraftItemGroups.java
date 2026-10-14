package com.sandymandy.pleasurecraft.item;

import com.sandymandy.pleasurecraft.PleasureCraft;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class PleasureCraftItemGroups {
    public static final CreativeModeTab PLEASURE_CRAFT_ITEM_GROUP = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "pleasure_craft_items"),
            FabricCreativeModeTab.builder().icon(() -> new ItemStack(PleasureCraftItems.LUCY_SPAWN_EGG))
                    .title(Component.translatable("itemgroup.pleasurecraft.pleasure_craft_items"))
                    .displayItems((displayContext, entries) -> {
                      entries.accept(PleasureCraftItems.LUCY_SPAWN_EGG);
                      entries.accept(PleasureCraftItems.BIA_SPAWN_EGG);
                    }).build());

    public static void registerItemGroups(){
        PleasureCraft.LOGGER.info("Registering Item Groups for " + PleasureCraft.MOD_ID);
    }
}