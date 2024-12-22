package com.linngdu664.bsf.entity.ai.goal;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class BSFGolemSitWhenOrderedToGoal extends Goal {
    private final BSFSnowGolemEntity golem;

    public BSFGolemSitWhenOrderedToGoal(BSFSnowGolemEntity golem) {
        this.golem = golem;
        this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canContinueToUse() {
        return golem.getStatus() == 0;
    }

    @Override
    public boolean canUse() {
        if (golem.isInWaterOrBubble()) {
            return false;
        } else if (!golem.onGround()) {
            return false;
        } else {
            LivingEntity livingentity = golem.getOwner();   // 主人下线也为null
            if (livingentity == null) {
                return !golem.isSpecialMode() || golem.getStatus() == 0;    // 特殊模式无视主人
            }
            return golem.getStatus() == 0;
        }
    }

    @Override
    public void start() {
        golem.getNavigation().stop();
    }
}
