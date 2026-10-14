package com.sandymandy.pleasurecraft.entity.girls;

import com.sandymandy.pleasurecraft.scene.SceneOption;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class LucyEntity extends AbstractGirlEntity{

    public LucyEntity(EntityType<? extends AbstractGirlEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected Item getTameItem() {
        return Items.ALLIUM;
    }

    @Override
    protected String getGirlDisplayName() {
        return "Lucy";
    }

    @Override
    protected String getGirlID() {
        return "lucy";
    }

    @Override
    public int getSizeGUI(){return 29;}

    @Override
    public float getYAxisGUI(){return 0.0525F;}

    @Override
    public List<SceneOption> getSceneOptions() {
        return List.of(
                new SceneOption("Paizuri",
                        "paizuri_start", List.of("paizuri_slow"),List.of("paizuri_fast"),"paizuri_cum"),
                new SceneOption("Blow Job",
                        "blowjob_start",List.of("blowjob_slow"),List.of("blowjob_fast"),"blowjob_cum")
        );
    }

    @Override
    protected Map<EquipmentSlot, List<String>> getClothingBones() {
        return super.getClothingBones();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, .20)
                .add(Attributes.TEMPT_RANGE, 5)
                .add(Attributes.ATTACK_DAMAGE, 2);


    }


}