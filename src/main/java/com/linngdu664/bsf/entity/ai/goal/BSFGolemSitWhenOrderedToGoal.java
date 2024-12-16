package com.linngdu664.bsf.entity.ai.goal;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BSFGolemSitWhenOrderedToGoal extends Goal {
    private final BSFSnowGolemEntity golem;

    public BSFGolemSitWhenOrderedToGoal(BSFSnowGolemEntity mob) {
        this.golem = mob;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canContinueToUse() {
        return golem.isOrderedToSit();
    }

    @Override
    public boolean canUse() {
        if (golem.isInWaterOrBubble()) {
            return false;
        } else if (!golem.onGround()) {
            return false;
        } else {
            LivingEntity livingentity = golem.getOwner();
            if (livingentity == null) {
                return true;
            } else {
                return (!(golem.distanceToSqr(livingentity) < 144.0) || livingentity.getLastHurtByMob() == null) && golem.isOrderedToSit();
            }
        }
    }

    @Override
    public void start() {
        golem.getNavigation().stop();
    }
}
