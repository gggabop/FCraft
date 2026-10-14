package com.sandymandy.pleasurecraft.util.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface GirlInventory extends Container {
    // Slot indices
    int MAIN_HAND_SLOT = 0;

    // Armor slots mapped to vanilla slots
    int ARMOR_FEET_SLOT = 1;
    int ARMOR_LEGS_SLOT = 2;
    int ARMOR_CHEST_SLOT = 3;
    int ARMOR_HEAD_SLOT = 4;

    int ARMOR_START = ARMOR_FEET_SLOT;
    int ARMOR_END = ARMOR_HEAD_SLOT;

    int BACKPACK_START = 5;
    int BACKPACK_SIZE = 12;  // slots 5..16 inclusive
    int BACKPACK_END = 16;
    int TOTAL_SLOTS = BACKPACK_START + BACKPACK_SIZE;  // 17 total slots

    // Must always return the same instance backing the inventory
    NonNullList<ItemStack> getItems();

    // Factory to create a GirlInventory backed by the given list
    static GirlInventory of(NonNullList<ItemStack> items) {
        return () -> items;
    }

    // Factory to create a new GirlInventory with empty slots
    static GirlInventory ofSize() {
        return of(NonNullList.withSize(TOTAL_SLOTS, ItemStack.EMPTY));
    }


    // === Armor access helpers ===

    default ItemStack getArmorStack(EquipmentSlot slot) {
        return switch (slot) {
            case FEET -> getItem(ARMOR_FEET_SLOT);
            case LEGS -> getItem(ARMOR_LEGS_SLOT);
            case CHEST -> getItem(ARMOR_CHEST_SLOT);
            case HEAD -> getItem(ARMOR_HEAD_SLOT);
            default -> ItemStack.EMPTY; // mainhand/offhand not handled here
        };
    }

    default void setArmorStack(EquipmentSlot slot, ItemStack stack) {
        switch (slot) {
            case FEET -> setItem(ARMOR_FEET_SLOT, stack);
            case LEGS -> setItem(ARMOR_LEGS_SLOT, stack);
            case CHEST -> setItem(ARMOR_CHEST_SLOT, stack);
            case HEAD -> setItem(ARMOR_HEAD_SLOT, stack);
            default -> {}
        }
    }


    // === Container defaults ===

    @Override
    default int getContainerSize() {
        return getItems().size();
    }

    @Override
    default boolean isEmpty() {
        for (ItemStack stack : getItems()) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    default ItemStack getItem(int slot) {
        return getItems().get(slot);
    }

    @Override
    default ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(getItems(), slot, count);
        if (!result.isEmpty()) setChanged();
        return result;
    }

    @Override
    default ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(getItems(), slot);
    }

    @Override
    default void setItem(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > stack.getMaxStackSize()) {
            stack.setCount(stack.getMaxStackSize());
        }
        setChanged();
    }

    // New method: set the entire inventory at once
    default void setItems(List<ItemStack> items) {
        NonNullList<ItemStack> inventory = getItems();

        // Copy provided items into inventory
        for (int i = 0; i < items.size() && i < inventory.size(); i++) {
            inventory.set(i, items.get(i));
        }
    }


    @Override
    default void setChanged() {
        // Optional: could sync inventory here, if needed
    }

    @Override
    default boolean stillValid(Player player) {
        return true;
    }

    @Override
    default void clearContent() {
        getItems().clear();
    }
}