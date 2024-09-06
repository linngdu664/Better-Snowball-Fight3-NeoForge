package com.linngdu664.bsf.entity.ai.goal.target;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
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
        LivingEntity owner = snowGolem.getOwner();
        // 待测试主人
        return switch (snowGolem.getLocator()) {
            case 0 -> {
                if (lastHurtByMob instanceof Enemy) {
                    yield super.canUse();
                }
                yield false;
            }
            case 2 -> {
                if (snowGolem.canAttackInAttackEnemyTeamMode(lastHurtByMob)) {
                    yield super.canUse();
                }
                yield false;
            }
            case 3 -> {
                if (owner == null) {
                    yield super.canUse();
                }
                yield snowGolem.wantsToAttack(lastHurtByMob, owner) && super.canUse();
            }
            default -> false;
        };


//        if (snowGolem.getLocator()==1 && snowGolem.getTarget() != null && !snowGolem.getTarget().isRemoved()) {
//            return false;
//        }
//        LivingEntity owner = snowGolem.getOwner();
//        LivingEntity livingEntity = snowGolem.getLastHurtByMob();
//        if (livingEntity instanceof OwnableEntity ownableEntity && owner != null && owner.equals(ownableEntity.getOwner())) {
//            return false;
//        }
//        BSFTeamSavedData savedData = snowGolem.getServer().overworld().getDataStorage().computeIfAbsent(BSFTeamSavedData::new, BSFTeamSavedData::new, "bsf_team");
//        if (savedData.isSameTeam(owner, livingEntity)) {
//            return false;
//        }
//        return super.canUse();
    }
}
