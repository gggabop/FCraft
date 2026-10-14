package com.sandymandy.pleasurecraft.client.models;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import com.sandymandy.pleasurecraft.entity.girls.AbstractGirlEntity;
import net.minecraft.resources.Identifier;

/**
 * Base de los modelos. En GeckoLib 5 el modelo solo dice dónde están sus archivos; los ajustes de huesos que hacía
 * (cabeza que sigue la mirada, ocultar huesos) están ahora en AbstractGirlRenderer.adjustModelBonesForRender.
 */
public abstract class AbstractGirlModel<T extends AbstractGirlEntity> extends GeoModel<T> {

    @Override
    public abstract Identifier getModelResource(GeoRenderState renderState);

    @Override
    public abstract Identifier getTextureResource(GeoRenderState renderState);

    @Override
    public abstract Identifier getAnimationResource(T animatable);
}