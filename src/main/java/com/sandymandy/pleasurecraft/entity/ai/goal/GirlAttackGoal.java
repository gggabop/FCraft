package com.sandymandy.pleasurecraft.entity.ai.goal;

import com.sandymandy.pleasurecraft.entity.girls.AbstractGirlEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class GirlAttackGoal extends MeleeAttackGoal {
    private final AbstractGirlEntity girl;
    private int ticks;

    public GirlAttackGoal(AbstractGirlEntity girl, double speed, boolean pauseWhenMobIdle) {
        super(girl, speed, pauseWhenMobIdle);
        this.girl = girl;
    }

    @Override
    public void start() {
        super.start();
        this.ticks = 0;
    }

    @Override
    public void stop() {
        super.stop();
        this.girl.setAggressive(false);
    }

    @Override
    public void tick() {
        super.tick();
        this.ticks++;
        if (this.ticks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2) {
            this.girl.setAggressive(true);
        } else {
            this.girl.setAggressive(false);
        }
    }
}
