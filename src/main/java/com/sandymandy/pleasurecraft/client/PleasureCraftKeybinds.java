package com.sandymandy.pleasurecraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.sandymandy.pleasurecraft.PleasureCraft;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class PleasureCraftKeybinds {
    public static KeyMapping thrustKey;
    public static KeyMapping cumKey;

    // Categoría propia en Controles (en 26.x es un objeto, ya no un texto)
    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "main"));

    public static void register() {
        thrustKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.pleasurecraft.thrust", // translation key
                InputConstants.KEY_Z, // default key
                CATEGORY
        ));

        cumKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.pleasurecraft.cum",
                InputConstants.KEY_V,
                CATEGORY
        ));

  }


}