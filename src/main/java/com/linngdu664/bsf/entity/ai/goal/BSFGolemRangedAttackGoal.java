package com.linngdu664.bsf.entity.ai.goal;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.item.weapon.SnowballShotgunItem;
import com.linngdu664.bsf.registry.ItemRegister;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.Vec3;

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

    @Override
    public void tick() {
        LivingEntity target = golem.getTarget();
        if (target != null) {
            float attackRadiusSqr = this.attackRadiusSqr;
            float attackRadius = this.attackRadius;
            if (golem.getWeapon().getItem() instanceof SnowballShotgunItem) {
                attackRadius *= 0.2F;
                attackRadiusSqr *= 0.04F;
            }
            if (golem.getCore().getItem().equals(ItemRegister.ACTIVE_TELEPORTATION_GOLEM_CORE.get()) && golem.getCoreCoolDown() == 0 && (golem.getStatus() == 2 || golem.getStatus() == 3)) {
                Vec3 vec3 = getTargetBackTeleportPos();
                if (vec3 != null) {
                    golem.tpWithParticlesAndResetCD(vec3);
                }
            }
            double d0 = golem.distanceToSqr(target.getX(), target.getY(), target.getZ());
            boolean flag = golem.getSensing().hasLineOfSight(target);
            boolean flag1 = seeTime > 0;
            if (flag != flag1) {
                seeTime = 0;
            }
            if (flag) {
                ++seeTime;
            } else {
                --seeTime;
            }
            if (d0 <= attackRadiusSqr && seeTime >= 20 || golem.getStatus() == 4) {
                golem.getNavigation().stop();
                ++strafingTime;
            } else {
                golem.getNavigation().moveTo(target, speedModifier);
                strafingTime = -1;
            }
            if (strafingTime >= 20) {
                if (golem.getRandom().nextFloat() < 0.3F) {
                    strafingClockwise = !strafingClockwise;
                }
                if (golem.getRandom().nextFloat() < 0.3F) {
                    strafingBackwards = !strafingBackwards;
                }
                strafingTime = 0;
            }
            if (strafingTime > -1 && golem.getStatus() != 4) {
                if (d0 > attackRadiusSqr * 0.64F) {
                    strafingBackwards = false;
                } else if (d0 < attackRadiusSqr * 0.09F) {
                    strafingBackwards = true;
                }
                golem.getMoveControl().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
                golem.lookAt(target, 30.0F, 30.0F);
            } else {
                golem.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }
            if (--attackTime <= 0) {
                if (attackTime == 0) {
                    if (!flag || !canShoot(target)) {
                        return;
                    }
                    float f = (float) Math.sqrt(d0) / attackRadius;
                    float f1 = Mth.clamp(f, 0.1F, 1.0F);
                    golem.performRangedAttack(target, f1);
                }
                attackTime = attackInterval;
            }
            lastPos = target.getEyePosition();
        }
    }
}
