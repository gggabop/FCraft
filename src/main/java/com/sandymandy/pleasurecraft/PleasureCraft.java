package com.sandymandy.pleasurecraft;

import com.sandymandy.pleasurecraft.entity.PleasureCraftEntities;
import com.sandymandy.pleasurecraft.item.PleasureCraftItemGroups;
import com.sandymandy.pleasurecraft.item.PleasureCraftItems;
import com.sandymandy.pleasurecraft.networking.PleasureCraftPackets;
import com.sandymandy.pleasurecraft.screen.GirlInventoryScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PleasureCraft implements ModInitializer {
	public static final String MOD_ID = "pleasurecraft";
	public static final Logger LOGGER = LoggerFactory.getLogger("PleasureCraft");

	public static final ExtendedMenuType<GirlInventoryScreenHandler, GirlScreenData> GIRL_INVENTORY_SCREEN_HANDLER =
			Registry.register(
					BuiltInRegistries.MENU,
					Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "girl_inventory_screen"),
					new ExtendedMenuType<>(GirlInventoryScreenHandler::new, GirlScreenData.PACKET_CODEC)
			);

	public static final ExtendedMenuType<GirlInventoryScreenHandler, GirlScreenData> GIRL_TALK_SCREEN_HANDLER =
			Registry.register(
					BuiltInRegistries.MENU,
					Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "girl_talk_screen"),
					new ExtendedMenuType<>(GirlInventoryScreenHandler::new, GirlScreenData.PACKET_CODEC)
			);


	@Override
	public void onInitialize() {
		PleasureCraftEntities.register();
		PleasureCraftItemGroups.registerItemGroups();
		PleasureCraftItems.registerModItems();
		PleasureCraftPackets.registerPackets();
		PleasureCraftPackets.registerC2SPackets();
	}

	public record GirlScreenData(int entityId) {
		public static final StreamCodec<RegistryFriendlyByteBuf, GirlScreenData> PACKET_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT,
				GirlScreenData::entityId,
				GirlScreenData::new
		);
	}
}