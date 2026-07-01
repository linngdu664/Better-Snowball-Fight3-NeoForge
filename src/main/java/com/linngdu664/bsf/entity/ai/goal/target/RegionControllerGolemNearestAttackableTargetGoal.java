package com.linngdu664.bsf.entity.ai.goal.target;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.entity.RegionControllerSnowGolemEntity;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;

public class RegionControllerGolemNearestAttackableTargetGoal extends TargetGoal {
    private static final int DEFAULT_RANDOM_INTERVAL = 4;
    private static final int SEARCH_DISTANCE = 100;
    private final RegionControllerSnowGolemEntity snowGolem;
    protected LivingEntity target;

    public RegionControllerGolemNearestAttackableTargetGoal(RegionControllerSnowGolemEntity snowGolem) {
        super(snowGolem, false, false);
        this.snowGolem = snowGolem;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (mob.getRandom().nextInt(DEFAULT_RANDOM_INTERVAL) != 0) {
            return false;
        }
        findTarget();
        return target != null;
    }

    protected AABB getTargetSearchArea() {
        RegionData aliveRange = snowGolem.getAliveRange();
        if (aliveRange != null) {
            return mob.getBoundingBox().inflate(SEARCH_DISTANCE, SEARCH_DISTANCE, SEARCH_DISTANCE).intersect(aliveRange.toBoundingBox());
        }
        return mob.getBoundingBox().inflate(SEARCH_DISTANCE, SEARCH_DISTANCE, SEARCH_DISTANCE);
    }

    protected void findTarget() {
        TargetingConditions targetConditions = TargetingConditions.forCombat().range(SEARCH_DISTANCE);
        targetConditions.ignoreLineOfSight();
        if (!(snowGolem.level() instanceof ServerLevel level)) {
            target = null;
            return;
        }
        BSFTeamSavedData savedData = level.getServer().overworld().getDataStorage().computeIfAbsent(BSFTeamSavedData.TYPE);
        int teamId = snowGolem.getFixedTeamId();
        targetConditions.selector((p, serverLevel) -> {
            if (p instanceof BSFSnowGolemEntity snowGolem1) {
                return teamId != savedData.getTeam(snowGolem1.getOwnerUUID());
            }
            if (p instanceof RegionControllerSnowGolemEntity snowGolem1) {
                return teamId != snowGolem1.getFixedTeamId();
            }
            if (p instanceof Player player) {
                return teamId != savedData.getTeam(player.getUUID());
            }
            return false;
        });
        target = getNearestEntity(level, targetConditions);
    }

    private LivingEntity getNearestEntity(ServerLevel level, TargetingConditions targetConditions) {
        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, getTargetSearchArea(), p -> true)) {
            if (targetConditions.test(level, snowGolem, entity)) {
                double distance = snowGolem.distanceToSqr(entity);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = entity;
                }
            }
        }
        return nearest;
    }

    public void start() {
        mob.setTarget(target);
        super.start();
    }
}
