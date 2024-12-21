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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
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
        Level level = snowGolem.level();
        if (snowGolem.getLocator() == 0) {
            targetConditions.selector(p -> p instanceof Enemy);
            target = level.getNearestEntity(level.getEntitiesOfClass(LivingEntity.class, getTargetSearchArea(), p -> true), targetConditions, snowGolem, snowGolem.getX(), snowGolem.getEyeY(), snowGolem.getZ());
        } else if (snowGolem.getLocator() == 2) {
            BSFTeamSavedData savedData = snowGolem.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
            int teamId = savedData.getTeam(snowGolem.getOwnerUUID());
            targetConditions.selector(p -> {
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
                    if (player.isCreative() || player.isSpectator()) {
                        return false;
                    }
                    return !savedData.isSameTeam(snowGolem.getOwner(), player);
                }
                return false;
            });
            target = level.getNearestEntity(level.getEntitiesOfClass(LivingEntity.class, getTargetSearchArea(), p -> true), targetConditions, snowGolem, snowGolem.getX(), snowGolem.getEyeY(), snowGolem.getZ());
        } else {
            if (snowGolem.getOwner() != null) {
                targetConditions.selector(p -> !(p instanceof Player) && !snowGolem.isEntityHasSameOwner(p) || p instanceof Player player && !player.isCreative() && !player.isSpectator() && !player.equals(snowGolem.getOwner()));
                target = level.getNearestEntity(level.getEntitiesOfClass(LivingEntity.class, getTargetSearchArea(), p -> true), targetConditions, snowGolem, snowGolem.getX(), snowGolem.getEyeY(), snowGolem.getZ());
            } else {
                targetConditions.selector(p -> !p.isSpectator());
                target = level.getNearestPlayer(targetConditions, snowGolem, snowGolem.getX(), snowGolem.getEyeY(), snowGolem.getZ());
            }
        }
    }

    public void start() {
        mob.setTarget(this.target);
        super.start();
    }
}
