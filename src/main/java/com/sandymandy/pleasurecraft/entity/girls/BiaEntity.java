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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiaEntity extends AbstractGirlEntity{

    public BiaEntity(EntityType<? extends AbstractGirlEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected Item getTameItem() {
        return Items.OXEYE_DAISY;
    }

    @Override
    protected String getGirlDisplayName() {
        return "Bia";
    }

    @Override
    protected String getGirlID() {
        return "bia";
    }

    @Override
    public int getSizeGUI(){return 35;}

    @Override
    public List<SceneOption> getSceneOptions() {
        return List.of(
                new SceneOption("Doggy",
                        "prone_doggy_intro", List.of("prone_doggy_soft"),List.of("prone_doggy_hard1","prone_doggy_hard2","prone_doggy_hard3"),"prone_doggy_cum")
        );

    }


    @Override
    protected Map<EquipmentSlot, List<String>> getClothingBones() {
        Map<EquipmentSlot, List<String>> clothing = super.getClothingBones();

        clothing.put(EquipmentSlot.HEAD, List.of("flower"));

        clothing.put(EquipmentSlot.LEGS, List.of("slip"));

        return clothing;
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, .20)
                .add(Attributes.TEMPT_RANGE, 5)
                .add(Attributes.ATTACK_DAMAGE, 2);

    }



}