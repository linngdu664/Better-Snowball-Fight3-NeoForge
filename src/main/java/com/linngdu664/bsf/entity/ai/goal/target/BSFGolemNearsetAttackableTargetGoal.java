package com.linngdu664.bsf.entity.ai.goal.target;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Objects;

public class BSFGolemNearsetAttackableTargetGoal extends TargetGoal {
    private static final int DEFAULT_RANDOM_INTERVAL = 4;
    private static final int SEARCH_DISTANCE = 50;
    @Nullable
    protected LivingEntity target;
    private final BSFSnowGolemEntity snowGolem;


    public BSFGolemNearsetAttackableTargetGoal(BSFSnowGolemEntity snowGolem) {
        super(snowGolem, true, false);
        this.snowGolem = snowGolem;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if (this.mob.getRandom().nextInt(DEFAULT_RANDOM_INTERVAL) != 0) {
            return false;
        }
        this.findTarget();
        return target != null;
    }

    protected AABB getTargetSearchArea() {
        return this.mob.getBoundingBox().inflate(SEARCH_DISTANCE, SEARCH_DISTANCE, SEARCH_DISTANCE);
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
            target = level.getNearestEntity(level.getEntitiesOfClass(LivingEntity.class, getTargetSearchArea(), p_148152_ -> true), targetConditions, snowGolem, snowGolem.getX(), snowGolem.getEyeY(), snowGolem.getZ());
        } else if (snowGolem.getLocator() == 2) {
            BSFTeamSavedData savedData = snowGolem.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
            int teamId = snowGolem.getFixedTeamId();
            if (teamId >= 0) {
                targetConditions.selector(p -> {
                    if (p instanceof BSFSnowGolemEntity snowGolem1) {
                        if (snowGolem1.getFixedTeamId() >= 0) {
                            return teamId != snowGolem1.getFixedTeamId();
                        }
                        return teamId != savedData.getTeam(snowGolem1.getOwnerUUID());
                    }
                    if (p instanceof Player player) {
                        if (player.isCreative() || player.isSpectator()) {
                            return false;
                        }
                        return teamId != savedData.getTeam(player.getUUID());
                    }
                    return false;
                });
            } else {
                targetConditions.selector(p -> {
                    if (p instanceof BSFSnowGolemEntity snowGolem1) {
                        if (savedData.getTeam(snowGolem.getOwnerUUID()) < 0) {
                            return !Objects.equals(snowGolem.getOwner(), snowGolem1.getOwner());
                        }
                        if (snowGolem1.getFixedTeamId() >= 0) {
                            return savedData.getTeam(snowGolem.getOwnerUUID()) != snowGolem1.getFixedTeamId();
                        }
                        return !savedData.isSameTeam(snowGolem.getOwner(), snowGolem1.getOwner());
                    }
                    if (p instanceof Player player) {
                        if (player.isCreative() || player.isSpectator()) {
                            return false;
                        }
                        return !savedData.isSameTeam(snowGolem.getOwner(), player);
                    }
                    return false;
                });
            }
            target = level.getNearestEntity(level.getEntitiesOfClass(LivingEntity.class, getTargetSearchArea(), p_148152_ -> true), targetConditions, snowGolem, snowGolem.getX(), snowGolem.getEyeY(), snowGolem.getZ());
//            targetConditions.selector(p -> !savedData.isSameTeam(snowGolem.getOwner(), p) && !p.isSpectator() && !((Player) p).isCreative());
//            target = level.getNearestPlayer(targetConditions, snowGolem, snowGolem.getX(), snowGolem.getEyeY(), snowGolem.getZ());
        } else {
            if (snowGolem.getOwner() != null) {
                targetConditions.selector(p -> !(p instanceof Player) && snowGolem.wantsToAttack(p, snowGolem.getOwner()) || p instanceof Player player && !player.isCreative() && !player.isSpectator() && !player.equals(snowGolem.getOwner()));
                target = level.getNearestEntity(level.getEntitiesOfClass(LivingEntity.class, getTargetSearchArea(), p_148152_ -> true), targetConditions, snowGolem, snowGolem.getX(), snowGolem.getEyeY(), snowGolem.getZ());
            } else {
                targetConditions.selector(p -> !p.isSpectator());
                target = level.getNearestPlayer(targetConditions, snowGolem, snowGolem.getX(), snowGolem.getEyeY(), snowGolem.getZ());
            }
        }
    }

    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }

    public void setTarget(@Nullable LivingEntity pTarget) {
        this.target = pTarget;
    }
}
