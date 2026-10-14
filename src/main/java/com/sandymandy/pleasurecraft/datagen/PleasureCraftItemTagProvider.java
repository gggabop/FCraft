package com.sandymandy.pleasurecraft.datagen;

import com.sandymandy.pleasurecraft.item.PleasureCraftItems;
import com.sandymandy.pleasurecraft.util.PleasureCraftTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class PleasureCraftItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public PleasureCraftItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        builder(PleasureCraftTags.Items.TRANSFORMABLE_ITEMS)
                .add(key(PleasureCraftItems.LUCY_SPAWN_EGG))
                .add(key(PleasureCraftItems.BIA_SPAWN_EGG));
    }

    private static ResourceKey<Item> key(Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow();
    }
}