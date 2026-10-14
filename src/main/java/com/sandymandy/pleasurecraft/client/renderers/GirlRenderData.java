package com.sandymandy.pleasurecraft.client.renderers;

import com.geckolib.cache.model.GeoBone;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.google.common.reflect.TypeToken;
import com.sandymandy.pleasurecraft.entity.girls.AbstractGirlEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;

import java.util.Map;

/**
 * Datos que el renderizador copia de la entidad al estado de render de cada fotograma
 * (GeckoLib 5 descarta la entidad antes de dibujar, así que todo lo que haga falta debe viajar aquí).
 */
public final class GirlRenderData {
    public static final DataTicket<AbstractGirlEntity> GIRL = DataTicket.create("pleasurecraft_girl", AbstractGirlEntity.class);
    public static final DataTicket<Map<String, Boolean>> BONE_VISIBILITY = DataTicket.create("pleasurecraft_bone_visibility", new TypeToken<Map<String, Boolean>>() {});
    public static final DataTicket<Map<String, Vec2>> UV_OFFSETS = DataTicket.create("pleasurecraft_uv_offsets", new TypeToken<Map<String, Vec2>>() {});
    public static final DataTicket<Identifier> RIDER_SKIN = DataTicket.create("pleasurecraft_rider_skin", Identifier.class);
    public static final DataTicket<Boolean> HIDE_HEAD = DataTicket.create("pleasurecraft_hide_head", Boolean.class);

    private GirlRenderData() {}

    /**
     * Si el hueso se debe dibujar: ni él ni ninguno de sus padres están ocultos en el mapa de visibilidad.
     * (Ocultar un hueso oculta también a sus hijos, y una capa que redibuja un hueso por su cuenta debe respetarlo.)
     */
    public static boolean isBoneVisible(RenderPassInfo<? extends GeoRenderState> renderPassInfo, GeoBone bone) {
        Map<String, Boolean> visibility = renderPassInfo.getGeckolibData(BONE_VISIBILITY);
        if (visibility == null || visibility.isEmpty()) return true;

        for (GeoBone b = bone; b != null; b = b.parent()) {
            if (Boolean.FALSE.equals(visibility.get(b.name()))) return false;
        }
        return true;
    }
}
