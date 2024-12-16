package com.linngdu664.bsf.entity.ai.goal.target;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Enemy;

import java.util.EnumSet;

public class BSFGolemOwnerHurtByTargetGoal extends TargetGoal {
    private final BSFSnowGolemEntity snowGolem;
    private LivingEntity ownerLastHurtBy;
    private int timestamp;

    public BSFGolemOwnerHurtByTargetGoal(BSFSnowGolemEntity snowGolem) {
        super(snowGolem, true);
        this.snowGolem = snowGolem;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

//     0: monster
//     1: target locator
//     2: enemy player
//     3: all
    public boolean canUse() {
        if (snowGolem.isOrderedToSit()) {
            return false;
        }
        LivingEntity owner = snowGolem.getOwner();
        if (owner == null) {
            return false;
        }
        ownerLastHurtBy = owner.getLastHurtByMob();
        switch (snowGolem.getLocator()) {
            case 0:
                if (!(ownerLastHurtBy instanceof Enemy)) {
                    return false;
                }
                break;
            case 1:
                return false;
            case 2:
                if (!snowGolem.canPassiveAttackInAttackEnemyTeamMode(ownerLastHurtBy)) {
                    return false;
                }
                break;
            default:
                if (snowGolem.isEntityHasSameOwner(ownerLastHurtBy)) {
                    return false;
                }
        }
        int $$1 = owner.getLastHurtByMobTimestamp();
        return $$1 != timestamp && canAttack(ownerLastHurtBy, TargetingConditions.DEFAULT);
    }

    public void start() {
        mob.setTarget(ownerLastHurtBy);
        LivingEntity owner = snowGolem.getOwner();
        if (owner != null) {
            timestamp = owner.getLastHurtByMobTimestamp();
        }
        super.start();
    }
}
