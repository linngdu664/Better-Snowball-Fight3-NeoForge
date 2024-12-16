package com.linngdu664.bsf.entity.ai.goal;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

public class BSFGolemRandomStrollGoal extends WaterAvoidingRandomStrollGoal {
    private final BSFSnowGolemEntity golem;

    public BSFGolemRandomStrollGoal(BSFSnowGolemEntity golem, double pSpeedModifier, float pProbability) {
        super(golem, pSpeedModifier, pProbability);
        this.golem = golem;
    }

    @Override
    public boolean canUse() {
        if (golem.getStatus() == 3) {
            return super.canUse();
        }
        return false;
    }
}
