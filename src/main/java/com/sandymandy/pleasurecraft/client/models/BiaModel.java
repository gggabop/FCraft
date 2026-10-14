package com.sandymandy.pleasurecraft.client.models;

import com.geckolib.renderer.base.GeoRenderState;
import com.sandymandy.pleasurecraft.PleasureCraft;
import com.sandymandy.pleasurecraft.entity.girls.BiaEntity;
import net.minecraft.resources.Identifier;

public class BiaModel extends AbstractGirlModel<BiaEntity> {

    // Modelo y animaciones: assets/pleasurecraft/geckolib/models/bia.geo.json y geckolib/animations/bia.animation.json
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "bia");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "textures/entities/bia.png");
    }

    @Override
    public Identifier getAnimationResource(BiaEntity entity) {
        return Identifier.fromNamespaceAndPath(PleasureCraft.MOD_ID, "bia");
    }

}