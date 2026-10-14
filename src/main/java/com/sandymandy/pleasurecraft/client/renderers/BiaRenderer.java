package com.sandymandy.pleasurecraft.client.renderers;

import com.sandymandy.pleasurecraft.client.models.BiaModel;
import com.sandymandy.pleasurecraft.entity.girls.BiaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class BiaRenderer extends AbstractGirlRenderer<BiaEntity> {
    public BiaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new BiaModel());
    }


}
