package com.linngdu664.bsf.entity.snowball.tracking;

import com.linngdu664.bsf.entity.AbstractBSFSnowGolemEntity;
import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.entity.RegionControllerSnowGolemEntity;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class AbstractPlayerTrackingSnowballEntity extends AbstractTrackingSnowballEntity {
    public AbstractPlayerTrackingSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel, BSFSnowballEntityProperties pProperties, boolean isLockFeet) {
        super(pEntityType, pLevel, pProperties, isLockFeet);
    }

    public AbstractPlayerTrackingSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, LivingEntity pShooter, Level pLevel, BSFSnowballEntityProperties pProperties, boolean isLockFeet, RegionData region) {
        super(pEntityType, pShooter, pLevel, pProperties, isLockFeet, region);
    }

    @Override
    public Entity getTarget() {
        Vec3 velocity = getDeltaMovement();
        Vec3 selfPos = getPosition(1);
        Level level = level();
        Entity shooter = getOwner();
        AABB aabb = getBoundingBox().inflate(range);
        BSFTeamSavedData savedData = getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
        if (shooter instanceof Player player) {
            List<Player> list = level.getEntitiesOfClass(Player.class, aabb, p -> !p.isSpectator() && !p.isCreative() && !p.equals(shooter) && !savedData.isSameTeam(player, p) && BSFCommonUtil.vec3AngleCos(velocity, p.getPosition(1).subtract(selfPos)) > 0.5);
            if (!list.isEmpty()) {
                return getNearest(list);
            }
            List<AbstractBSFSnowGolemEntity> list1 = level.getEntitiesOfClass(AbstractBSFSnowGolemEntity.class, aabb, p -> {
                Vec3 targetPos = p.getPosition(1);
                if (p instanceof RegionControllerSnowGolemEntity golem) {
                    if (savedData.getTeam(player.getUUID()) != golem.getFixedTeamId()) {
                        return BSFCommonUtil.vec3AngleCos(velocity, targetPos.subtract(selfPos)) > 0.5;
                    }
                    return false;
                }
                BSFSnowGolemEntity golem = (BSFSnowGolemEntity) p;
                if (golem.getOwner() == null) {
                    return true;
                }
                return !golem.getOwner().equals(player) && !savedData.isSameTeam(player, golem.getOwner()) && BSFCommonUtil.vec3AngleCos(velocity, targetPos.subtract(selfPos)) > 0.5;
            });
            return getNearest(list1);
        }
        if (shooter instanceof AbstractBSFSnowGolemEntity snowGolem) {
            LivingEntity target = snowGolem.getTarget();
            if (target != null && (target.getType().equals(EntityType.PLAYER) || target instanceof AbstractBSFSnowGolemEntity) && BSFCommonUtil.vec3AngleCos(velocity, target.getPosition(1).subtract(selfPos)) > 0.5) {
                return target;
            }
        }
        return null;

//        List<Player> list = level.getEntitiesOfClass(Player.class, aabb, p -> !p.isSpectator() && !p.equals(shooter) && !savedData.isSameTeam(shooter, p) && !(shooter instanceof BSFSnowGolemEntity golem && (p.equals(golem.getOwner()) || savedData.isSameTeam(golem.getOwner(), p))) && BSFCommonUtil.vec3AngleCos(velocity, p.getPosition(1).subtract(getPosition(1))) > 0.5);
//        if (!list.isEmpty()) {
//            return level.getNearestEntity(list, TargetingConditions.DEFAULT, null, getX(), getY(), getZ());
//        }
//        List<BSFSnowGolemEntity> list1 = level.getEntitiesOfClass(BSFSnowGolemEntity.class, aabb, p -> {
//            LivingEntity enemyGolemTarget = p.getTarget();      // 这个雪傀儡的目标
//            if (enemyGolemTarget == null) {
//                return false;
//            }
//            LivingEntity enemyGolemOwner = p.getOwner();        // 这个雪傀儡的主人
//            if (shooter instanceof BSFSnowGolemEntity golem) {
//
//                Entity golemOwner = golem.getOwner();
//                return enemyGolemTarget.equals(golemOwner) && !savedData.isSameTeam(golemOwner, enemyGolemOwner) && BSFCommonUtil.vec3AngleCos(velocity, p.getPosition(1).subtract(getPosition(1))) > 0.5;
//            }
//            return enemyGolemTarget.equals(shooter) && !savedData.isSameTeam(shooter, enemyGolemOwner) && BSFCommonUtil.vec3AngleCos(velocity, p.getPosition(1).subtract(getPosition(1))) > 0.5;
//        });
//        return level.getNearestEntity(list1, TargetingConditions.DEFAULT, null, getX(), getY(), getZ());
    }
}
