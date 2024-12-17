package com.linngdu664.bsf.entity.ai.goal;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;

public class BSFGolemRangedAttackGoal extends RegionControllerGolemRangedAttackGoal {
    private final BSFSnowGolemEntity golem;

    public BSFGolemRangedAttackGoal(BSFSnowGolemEntity golem, double pSpeedModifier, int pAttackInterval, float pAttackRadius) {
        super(golem, pSpeedModifier, pAttackInterval, pAttackRadius);
        this.golem = golem;
    }

    @Override
    public boolean canUse() {
        if (golem.getStatus() != 1) {
            return super.canUse();
        }
        return false;
    }

    @Override
    protected void changeTargetWhenNecessary(LivingEntity entity) {
        LivingEntity owner = golem.getOwner();
        if (golem.getLocator() == 0) {
            if (entity instanceof Enemy) {
                golem.setTarget(entity);
            }
        } else if (golem.getLocator() == 2) {
            if (golem.canPassiveAttackInAttackEnemyTeamMode(entity)) {
                golem.setTarget(entity);
            }
        } else if (golem.getLocator() == 3 && owner != null) {
            if (entity.getType().equals(EntityType.PLAYER)) {
                if (!entity.isSpectator() && !entity.equals(owner)) {
                    golem.setTarget(entity);
                }
            } else if (!golem.isEntityHasSameOwner(entity)) {
                golem.setTarget(entity);
            }
        }
        attackTime = 1;
    }
}
