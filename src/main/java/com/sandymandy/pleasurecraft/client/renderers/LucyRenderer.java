package com.sandymandy.pleasurecraft.client.renderers;

import com.sandymandy.pleasurecraft.client.models.LucyModel;
import com.sandymandy.pleasurecraft.entity.girls.LucyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class LucyRenderer extends AbstractGirlRenderer<LucyEntity> {
    public LucyRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new LucyModel());
    }


}