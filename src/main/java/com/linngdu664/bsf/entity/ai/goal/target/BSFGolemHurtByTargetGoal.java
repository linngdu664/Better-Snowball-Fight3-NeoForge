package com.linngdu664.bsf.entity.ai.goal.target;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.item.component.RegionData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Enemy;

public class BSFGolemHurtByTargetGoal extends HurtByTargetGoal {
    private final BSFSnowGolemEntity snowGolem;

    public BSFGolemHurtByTargetGoal(BSFSnowGolemEntity snowGolem, Class<?>... pToIgnoreDamage) {
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
        if (lastHurtByMob == null) {
            return false;
        }
        if (aliveRange != null && !aliveRange.inRegion(lastHurtByMob.position())) {
            return false;
        }
        return switch (snowGolem.getLocator()) {
            case 0 -> {
                if (lastHurtByMob instanceof Enemy) yield super.canUse();
                yield false;
            }
            case 2 -> {
                if (snowGolem.canPassiveAttackInAttackEnemyTeamMode(lastHurtByMob)) yield super.canUse();
                yield false;
            }
            case 3 -> !lastHurtByMob.equals(snowGolem.getOwner()) && !snowGolem.isEntityHasSameOwner(lastHurtByMob) && super.canUse();
            default -> false;
        };
    }
}
