package com.linngdu664.bsf.entity.ai.goal;

import com.linngdu664.bsf.entity.AbstractBSFSnowGolemEntity;
import com.linngdu664.bsf.item.component.ItemData;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.item.weapon.AbstractBSFWeaponItem;
import com.linngdu664.bsf.item.weapon.SnowballShotgunItem;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EffectRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class RegionControllerGolemRangedAttackGoal extends Goal {
    private final AbstractBSFSnowGolemEntity golem;
    protected final double speedModifier;
    protected final int attackInterval;
    protected final float attackRadius;
    protected final float attackRadiusSqr;
    protected int seeTime;
    protected int attackTime = -1;
    protected int strafingTime = -1;
    protected boolean strafingClockwise;
    protected boolean strafingBackwards;
    protected Vec3 lastPos;

    public RegionControllerGolemRangedAttackGoal(AbstractBSFSnowGolemEntity golem, double pSpeedModifier, int pAttackInterval, float pAttackRadius) {
        this.golem = golem;
        this.speedModifier = pSpeedModifier;
        this.attackInterval = pAttackInterval;
        this.attackRadius = pAttackRadius;
        this.attackRadiusSqr = pAttackRadius * pAttackRadius;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = golem.getTarget();
        if (livingEntity != null && livingEntity.isAlive()) {
            lastPos = golem.getTarget().getEyePosition();
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() || !golem.getNavigation().isDone();
    }

    @Override
    public void stop() {
        seeTime = 0;
        attackTime = -1;
        golem.setZza(0);
        golem.setXxa(0);
        golem.setSpeed(0);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    protected boolean canShoot(LivingEntity pTarget) {
        ItemStack weapon = golem.getWeapon();
        if (weapon.isEmpty() || golem.hasEffect(EffectRegister.WEAPON_JAM)) {
            return false;
        }
        AbstractBSFWeaponItem weaponItem = (AbstractBSFWeaponItem) weapon.getItem();
        float v = 3.0F;
        float acc = 1.0F;
        if (weaponItem.equals(ItemRegister.POWERFUL_SNOWBALL_CANNON.get())) {
            v = 4.0F;
        } else if (weaponItem.equals(ItemRegister.SNOWBALL_SHOTGUN.get())) {
            v = 2.0F;
            acc = 10.0F;
        }
        double h = pTarget.getEyeY() - golem.getEyeY();
        double dx = pTarget.getX() - golem.getX();
        double dz = pTarget.getZ() - golem.getZ();
        double x2 = BSFCommonUtil.lengthSqr(dx, dz);
        double d = Math.sqrt(x2 + h * h);   // d是总距离
        double x = Math.sqrt(x2);
        double cosTheta, sinTheta;
        boolean isNoGravity = golem.getAmmo().getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item().equals(ItemRegister.GHOST_SNOWBALL.get());
        if (isNoGravity) {
            if (golem.getCore().getItem().equals(ItemRegister.MOVEMENT_PREDICTION_GOLEM_CORE.get())) {
                double t = d / v;
                Vec3 vel = pTarget.getEyePosition().subtract(lastPos);
                h += t * vel.y;
                dx += t * vel.x;
                dz += t * vel.z;
                x2 = BSFCommonUtil.lengthSqr(dx, dz);
                d = Math.sqrt(x2 + h * h);
                x = Math.sqrt(x2);
            }
            cosTheta = x / d;
            sinTheta = h / d;
        } else {
            double k = 0.015 * x2 / (v * v);    // 0.5 * g / 400.0, g = 12
            cosTheta = 0.7071067811865475 / d * Math.sqrt(x2 - 2 * k * h + x * Math.sqrt(x2 - 4 * k * k - 4 * k * h));
            if (golem.getCore().getItem().equals(ItemRegister.MOVEMENT_PREDICTION_GOLEM_CORE.get())) {
                double t = x / (v * cosTheta);      // tick
                Vec3 vel = pTarget.getEyePosition().subtract(lastPos);
                h += t * vel.y;
                dx += t * vel.x;
                dz += t * vel.z;
                x2 = BSFCommonUtil.lengthSqr(dx, dz);
                d = Math.sqrt(x2 + h * h);
                x = Math.sqrt(x2);
                k = 0.015 * x2 / (v * v);
                cosTheta = 0.7071067811865475 / d * Math.sqrt(x2 - 2 * k * h + x * Math.sqrt(x2 - 4 * k * k - 4 * k * h));
            }
            if (cosTheta > 1) {
                sinTheta = 0;
            } else {
                sinTheta = Math.sqrt(1 - cosTheta * cosTheta);
                if (h < -k) {
                    sinTheta = -sinTheta;
                }
            }
        }
        dx = dx / x * cosTheta;
        dz = dz / x * cosTheta;
        List<LivingEntity> list = golem.level().getEntitiesOfClass(LivingEntity.class, golem.getBoundingBox().inflate(x), p -> !p.isSpectator() && !golem.equals(p) && !pTarget.equals(p));
        for (LivingEntity entity : list) {
            double dx1 = entity.getX() - golem.getX();
            double dz1 = entity.getZ() - golem.getZ();
            double cosAlpha = BSFCommonUtil.vec2AngleCos(dx, dz, dx1, dz1);
            if (cosAlpha <= 0.17) {
                continue;
            }
            AABB aabb = entity.getBoundingBox();
            double sinAlpha = Math.sqrt(1 - cosAlpha * cosAlpha);
            double r = Math.sqrt(BSFCommonUtil.lengthSqr(dx1, dz1));
            if (r < x && r * sinAlpha < Math.sqrt(BSFCommonUtil.lengthSqr(aabb.maxX - aabb.minX, aabb.maxZ - aabb.minZ)) * 0.5 + 0.8) {
                double t = r * cosAlpha / (v * cosTheta);
                double y = (isNoGravity ? v * sinTheta * t : v * sinTheta * t - 0.015 * t * t) + golem.getEyeY();
                if (y >= aabb.minY - 0.8 && y <= aabb.maxY + 0.8) {
                    changeTargetWhenNecessary(entity);
                    return false;
                }
            }
        }
        golem.setShootX(dx);
        golem.setShootY(sinTheta);
        golem.setShootZ(dz);
        golem.setLaunchAccuracy(acc);
        golem.setLaunchVelocity(v);
        return true;
    }

    protected void changeTargetWhenNecessary(LivingEntity entity) {
        if (golem.canPassiveAttackInAttackEnemyTeamMode(entity)) {
            golem.setTarget(entity);
        }
        attackTime = 1;
    }

    public void tick() {
        LivingEntity target = golem.getTarget();
        if (target != null) {
            float attackRadiusSqr = this.attackRadiusSqr;
            float attackRadius = this.attackRadius;
            if (golem.getWeapon().getItem() instanceof SnowballShotgunItem) {
                attackRadius *= 0.2F;
                attackRadiusSqr *= 0.04F;
            }
            if (golem.getCore().getItem().equals(ItemRegister.ACTIVE_TELEPORTATION_GOLEM_CORE.get()) && golem.getCoreCoolDown() == 0 && golem.canMoveAndAttack()) {
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
            if (d0 <= attackRadiusSqr && seeTime >= 20 || !golem.canMoveAndAttack()) {
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
            if (strafingTime > -1 && golem.canMoveAndAttack()) {
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

    protected Vec3 getTargetBackTeleportPos() {
        Level level = golem.level();
        LivingEntity target = golem.getTarget();
        double targetX = target.getX();
        double targetY = target.getY();
        double targetZ = target.getZ();
        float initTheta = (float) Mth.atan2(targetZ - golem.getZ(), targetX - golem.getX());
        Vec3 targetPos = target.getEyePosition();
        BlockPos targetBlockPos = new BlockPos(BSFCommonUtil.vec3ToI(targetPos));
        RegionData aliveRange = golem.getAliveRange();
        for (int r = 4; r <= 10; r++) {
            float step = 1.0F / r;
            for (float theta = 0; theta < Mth.PI * 0.25F; theta += step) {
                for (float phi = 0; phi < Mth.PI * 0.5F; phi += step) {
                    int x = Mth.floor(targetX + r * Mth.cos(initTheta + theta) * Mth.cos(phi));
                    int y = Mth.floor(targetY + r * Mth.sin(phi));
                    int z = Mth.floor(targetZ + r * Mth.sin(initTheta + theta) * Mth.cos(phi));
                    int x1 = Mth.floor(targetX + r * Mth.cos(initTheta - theta) * Mth.cos(phi));
                    int y1 = Mth.floor(targetY + r * Mth.sin(-phi));
                    int z1 = Mth.floor(targetZ + r * Mth.sin(initTheta - theta) * Mth.cos(phi));
                    BlockPos blockPos = new BlockPos(x, y, z);
                    if ((aliveRange == null || aliveRange.inRegion(blockPos)) && golem.canStandOn(blockPos, level) && level.clip(new ClipContext(blockPos.above().getCenter(), targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, golem)).getBlockPos().equals(targetBlockPos)) {
                        return new Vec3(x, y, z);
                    }
                    blockPos = new BlockPos(x1, y, z1);
                    if ((aliveRange == null || aliveRange.inRegion(blockPos)) && golem.canStandOn(blockPos, level) && level.clip(new ClipContext(blockPos.above().getCenter(), targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, golem)).getBlockPos().equals(targetBlockPos)) {
                        return new Vec3(x1, y, z1);
                    }
                    blockPos = new BlockPos(x1, y1, z1);
                    if ((aliveRange == null || aliveRange.inRegion(blockPos)) && golem.canStandOn(blockPos, level) && level.clip(new ClipContext(blockPos.above().getCenter(), targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, golem)).getBlockPos().equals(targetBlockPos)) {
                        return new Vec3(x1, y1, z1);
                    }
                    blockPos = new BlockPos(x, y1, z);
                    if ((aliveRange == null || aliveRange.inRegion(blockPos)) && golem.canStandOn(blockPos, level) && level.clip(new ClipContext(blockPos.above().getCenter(), targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, golem)).getBlockPos().equals(targetBlockPos)) {
                        return new Vec3(x, y1, z);
                    }
                }
            }
        }
        return null;
    }
}
