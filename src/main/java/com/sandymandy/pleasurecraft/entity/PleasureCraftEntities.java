package com.sandymandy.pleasurecraft.entity;

import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.girls.BiaEntity;
import com.sandymandy.pleasurecraft.entity.girls.LucyEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class PleasureCraftEntities {
    public static final Identifier LUCY_ID = Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "lucy");
    public static final Identifier BIA_ID = Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "bia");

    // (la clave de Lucy usaba el id "bia" por error; en 26.x la clave debe coincidir con el id con que se registra)
    private static final ResourceKey<EntityType<?>> LUCY_KEY = ResourceKey.create(Registries.ENTITY_TYPE, LUCY_ID);

    private static final ResourceKey<EntityType<?>> BIA_KEY = ResourceKey.create(Registries.ENTITY_TYPE, BIA_ID);

    public static final EntityType<LucyEntity> LUCY = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            LUCY_ID,
            EntityType.Builder.of(LucyEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.95f)
                    .build(LUCY_KEY)
    );

    public static final EntityType<BiaEntity> BIA = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            BIA_ID,
            EntityType.Builder.of(BiaEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.65f)
                    .build(BIA_KEY)
    );

    public static void register() {
        // Attributes
        FabricDefaultAttributeRegistry.register(LUCY, LucyEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(BIA, BiaEntity.createAttributes());
    }
}