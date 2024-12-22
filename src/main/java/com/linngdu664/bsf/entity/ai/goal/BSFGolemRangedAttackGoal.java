package com.linngdu664.bsf.entity.ai.goal;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

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
        int locator = golem.getLocator();
        if (locator == 0) {
            if (entity instanceof Enemy) {
                golem.setTarget(entity);
            }
        } else if (locator == 2) {
            if (golem.canPassiveAttackInAttackEnemyTeamMode(entity)) {
                golem.setTarget(entity);
            }
        } else if (locator == 3) {
            if (golem.getOwner() != null) {
                if (entity instanceof Player player) {
                    if (!player.isCreative() && !player.equals(owner)) {    // 因为是直接设置，所以这里仍然需要判断创造模式
                        golem.setTarget(player);
                    }
                } else if (!golem.isEntityHasSameOwner(entity)) {
                    golem.setTarget(entity);
                }
            } else {
                if (entity instanceof Player player && !player.isCreative()) {
                    golem.setTarget(player);
                }
            }
        }
        attackTime = 1;
    }
}
