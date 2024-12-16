package com.linngdu664.bsf.entity.ai.goal.target;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.entity.RegionControllerSnowGolemEntity;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class RegionControllerGolemNearestAttackableTargetGoal extends TargetGoal {
    private static final int DEFAULT_RANDOM_INTERVAL = 4;
    private static final int SEARCH_DISTANCE = 100;
    private final RegionControllerSnowGolemEntity snowGolem;
    @Nullable
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
        this.findTarget();
        return target != null;
    }

    protected AABB getTargetSearchArea() {
        return mob.getBoundingBox().inflate(SEARCH_DISTANCE, SEARCH_DISTANCE, SEARCH_DISTANCE);
    }

    protected void findTarget() {
        TargetingConditions targetConditions = TargetingConditions.forCombat().range(SEARCH_DISTANCE);
        targetConditions.ignoreLineOfSight();
        Level level = snowGolem.level();
        BSFTeamSavedData savedData = snowGolem.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
        int teamId = snowGolem.getFixedTeamId();
        targetConditions.selector(p -> {
            if (p instanceof BSFSnowGolemEntity snowGolem1) {
                return teamId != savedData.getTeam(snowGolem1.getOwnerUUID());
            }
            if (p instanceof RegionControllerSnowGolemEntity snowGolem1) {
                return teamId != snowGolem1.getFixedTeamId();
            }
            if (p instanceof Player player) {
                if (player.isCreative() || player.isSpectator()) {
                    return false;
                }
                return teamId != savedData.getTeam(player.getUUID());
            }
            return false;
        });
        target = level.getNearestEntity(level.getEntitiesOfClass(LivingEntity.class, getTargetSearchArea(), p_148152_ -> true), targetConditions, snowGolem, snowGolem.getX(), snowGolem.getEyeY(), snowGolem.getZ());
    }

    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }

    public void setTarget(@Nullable LivingEntity pTarget) {
        this.target = pTarget;
    }
}
