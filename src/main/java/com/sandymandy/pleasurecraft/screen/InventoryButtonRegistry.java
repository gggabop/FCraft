package com.sandymandy.pleasurecraft.screen;

import com.sandymandy.pleasurecraft.networking.C2S.InventoryButtonC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.chat.Component;

import java.util.List;

public class InventoryButtonRegistry {
    public static final List<InventoryButtonAction> BUTTONS_LEFT = List.of(
            new InventoryButtonAction(Component.literal("Strip"), (girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "stripOrDressup"));
            }),

            new InventoryButtonAction(Component.literal("Break Up"), (girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "breakUp"));
            }),

            new InventoryButtonAction(Component.literal("Set Base Here"), (girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "setBase"));
            }),

            new InventoryButtonAction(Component.literal("Go To Base"), (girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "goToBase"));
            })
    );

    public static final List<InventoryButtonAction> BUTTONS_RIGHT = List.of(
            new InventoryButtonAction(Component.literal("Talk"), (girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "talk"));
            }),

            new InventoryButtonAction(Component.literal("Sit"), (girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "sit"));
            }),

            new InventoryButtonAction(Component.literal("Follow Me"), (girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "follow"));
            }),

            new InventoryButtonAction(Component.literal("Test Anim 1"), (girl, player) -> {
                ClientPlayNetworking.send(new InventoryButtonC2SPacket(girl.getId(), "testAnim1"));
            })
    );
}