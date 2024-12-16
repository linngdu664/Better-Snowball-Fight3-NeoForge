package com.linngdu664.bsf.entity.ai.goal.target;

import com.linngdu664.bsf.entity.RegionControllerSnowGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

public class RegionControllerGolemHurtByTargetGoal extends HurtByTargetGoal {
    private final RegionControllerSnowGolemEntity snowGolem;

    public RegionControllerGolemHurtByTargetGoal(RegionControllerSnowGolemEntity snowGolem, Class<?>... pToIgnoreDamage) {
        super(snowGolem, pToIgnoreDamage);
        this.snowGolem = snowGolem;
    }

//     0: monster
//     1: target locator
//     2: enemy player
//     3: all
    @Override
    public boolean canUse() {
        LivingEntity lastHurtByMob = snowGolem.getLastHurtByMob();
        if (snowGolem.canPassiveAttackInAttackEnemyTeamMode(lastHurtByMob)) {
            return super.canUse();
        }
        return false;
    }
}
