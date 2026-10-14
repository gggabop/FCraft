package com.sandymandy.pleasurecraft.item;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.PleasureCraftEntities;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Function;


public class PleasureCraftItems {


    public static final Item LUCY_SPAWN_EGG = registerItem("lucy_spawn_egg",
            properties -> new SpawnEggItem(properties.spawnEgg(PleasureCraftEntities.LUCY)));

    public static final Item BIA_SPAWN_EGG = registerItem("bia_spawn_egg",
            properties -> new SpawnEggItem(properties.spawnEgg(PleasureCraftEntities.BIA)));


    private static Item registerItem(String name, Function<Item.Properties, Item> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, name);
        return Registry.register(BuiltInRegistries.ITEM, id,
                factory.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
    }


    public static void registerModItems(){
        PleasureCraft.LOGGER.info("Registering Mod Items for " + PleasureCraft.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
            entries.accept(LUCY_SPAWN_EGG);
            entries.accept(BIA_SPAWN_EGG);
        });
    }
}