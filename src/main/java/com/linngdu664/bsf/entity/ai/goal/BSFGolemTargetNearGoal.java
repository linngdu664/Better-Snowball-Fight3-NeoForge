package com.linngdu664.bsf.entity.ai.goal;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;

public class BSFGolemTargetNearGoal extends RegionControllerGolemTargetNearGoal {
    private final BSFSnowGolemEntity golem;

    public BSFGolemTargetNearGoal(BSFSnowGolemEntity golem) {
        super(golem);
        this.golem = golem;
    }

    @Override
    public boolean canUse() {
        if (golem.getStatus() == 2 || golem.getStatus() == 3) {
            return super.canUse();
        }
        return false;
    }
}
