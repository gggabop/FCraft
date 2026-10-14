package com.sandymandy.pleasurecraft.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.function.BooleanSupplier;

public class ConditionalGoal extends Goal {
    private final Goal wrappedGoal;
    private final BooleanSupplier condition;

    public ConditionalGoal(Goal wrappedGoal, BooleanSupplier condition) {
        this.wrappedGoal = wrappedGoal;
        this.condition = condition;
    }

    @Override
    public boolean canUse() {
        return condition.getAsBoolean() && wrappedGoal.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return condition.getAsBoolean() && wrappedGoal.canContinueToUse();
    }

    @Override
    public void start() {
        wrappedGoal.start();
    }

    @Override
    public void stop() {
        wrappedGoal.stop();
    }

    @Override
    public void tick() {
        wrappedGoal.tick();
    }

    @Override
    public boolean isInterruptable() {
        return wrappedGoal.isInterruptable();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return wrappedGoal.requiresUpdateEveryTick();
    }

    @Override
    public EnumSet<Flag> getFlags() {
        return wrappedGoal.getFlags();
    }
}
