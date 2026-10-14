package com.sandymandy.pleasurecraft.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.MoveControl;

import java.util.EnumSet;

public class StopMovementGoal extends Goal {
    private final PathfinderMob entity;

    public StopMovementGoal(PathfinderMob entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return true; // Always active while added
    }

    @Override
    public void start() {
        haltMovement();
    }

    @Override
    public void tick() {
        haltMovement();
    }

    private void haltMovement() {
        entity.getNavigation().stop();
        entity.setDeltaMovement(0, entity.getDeltaMovement().y > 0 ? 0 : entity.getDeltaMovement().y, 0); // stops lateral motion
        entity.setJumping(false);

        MoveControl control = entity.getMoveControl();
        if (control != null) {
            control.setWantedPosition(entity.getX(), entity.getY(), entity.getZ(), 0); // keep position
        }
    }

    @Override
    public boolean canContinueToUse() {
        return true; // Continuous block
    }
}
