package com.sandymandy.pleasurecraft.client.models;

import com.geckolib.renderer.base.GeoRenderState;
import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.girls.LucyEntity;
import net.minecraft.resources.Identifier;

public class LucyModel extends AbstractGirlModel<LucyEntity> {

    // Modelo y animaciones: assets/pleasurecraft/geckolib/models/lucy.geo.json y geckolib/animations/lucy.animation.json
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "lucy");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "textures/entities/lucy.png");
    }

    @Override
    public Identifier getAnimationResource(LucyEntity entity) {
        return Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "lucy");
    }



}