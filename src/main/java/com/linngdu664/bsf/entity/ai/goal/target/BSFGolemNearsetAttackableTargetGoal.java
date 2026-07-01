package com.linngdu664.bsf.entity.ai.goal.target;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.entity.RegionControllerSnowGolemEntity;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.Objects;

public class BSFGolemNearsetAttackableTargetGoal extends TargetGoal {
    private static final int DEFAULT_RANDOM_INTERVAL = 4;
    private static final int SEARCH_DISTANCE = 50;
    private final BSFSnowGolemEntity snowGolem;
    protected LivingEntity target;

    public BSFGolemNearsetAttackableTargetGoal(BSFSnowGolemEntity snowGolem) {
        super(snowGolem, true, false);
        this.snowGolem = snowGolem;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (this.mob.getRandom().nextInt(DEFAULT_RANDOM_INTERVAL) != 0) {
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
        if (snowGolem.getLocator() == 1) {
            target = null;
            return;
        }
        TargetingConditions targetConditions = TargetingConditions.forCombat().range(SEARCH_DISTANCE);
        if (!(snowGolem.level() instanceof ServerLevel level)) {
            target = null;
            return;
        }
        if (snowGolem.getLocator() == 0) {
            targetConditions.selector((p, serverLevel) -> p instanceof Enemy);
            target = getNearestEntity(level, targetConditions);
        } else if (snowGolem.getLocator() == 2) {
            BSFTeamSavedData savedData = level.getServer().overworld().getDataStorage().computeIfAbsent(BSFTeamSavedData.TYPE);
            int teamId = savedData.getTeam(snowGolem.getOwnerUUID());
            targetConditions.selector((p, serverLevel) -> {
                if (p instanceof BSFSnowGolemEntity snowGolem1) {
                    if (teamId < 0) {
                        return !Objects.equals(snowGolem.getOwner(), snowGolem1.getOwner());
                    }
                    return !savedData.isSameTeam(snowGolem.getOwner(), snowGolem1.getOwner());
                }
                if (p instanceof RegionControllerSnowGolemEntity snowGolem1) {
                    if (teamId < 0) {
                        return true;
                    }
                    return teamId != snowGolem1.getFixedTeamId();
                }
                if (p instanceof Player player) {
                    return !savedData.isSameTeam(snowGolem.getOwner(), player);
                }
                return false;
            });
            target = getNearestEntity(level, targetConditions);
        } else {
            if (snowGolem.getOwner() != null) {
                targetConditions.selector((p, serverLevel) -> {
                    if (p instanceof Player) {
                        return !p.equals(snowGolem.getOwner());
                    }
                    return !snowGolem.isEntityHasSameOwner(p);
                });
                target = getNearestEntity(level, targetConditions);
            } else {
                targetConditions.selector((p, serverLevel) -> p instanceof Player);
                target = getNearestEntity(level, targetConditions);
            }
        }
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
        mob.setTarget(this.target);
        super.start();
    }
}
