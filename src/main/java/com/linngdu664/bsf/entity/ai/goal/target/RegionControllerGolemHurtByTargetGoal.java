package com.linngdu664.bsf.entity.ai.goal.target;

import com.linngdu664.bsf.entity.RegionControllerSnowGolemEntity;
import com.linngdu664.bsf.item.component.RegionData;
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
        RegionData aliveRange = snowGolem.getAliveRange();
        if (aliveRange != null && lastHurtByMob != null && !aliveRange.inRegion(lastHurtByMob.position())) {
            return false;
        }
        if (snowGolem.canPassiveAttackInAttackEnemyTeamMode(lastHurtByMob)) {
            return super.canUse();
        }
        return false;
    }
}
