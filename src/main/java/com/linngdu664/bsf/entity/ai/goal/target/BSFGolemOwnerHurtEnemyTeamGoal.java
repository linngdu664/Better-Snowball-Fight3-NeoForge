package com.linngdu664.bsf.entity.ai.goal.target;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.item.component.RegionData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.EnumSet;

public class BSFGolemOwnerHurtEnemyTeamGoal extends TargetGoal {
    private final BSFSnowGolemEntity snowGolem;
    private LivingEntity ownerLastHurt;
    private int timestamp;

    public BSFGolemOwnerHurtEnemyTeamGoal(BSFSnowGolemEntity snowGolem) {
        super(snowGolem, true);
        this.snowGolem = snowGolem;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        LivingEntity owner = snowGolem.getOwner();
        if (snowGolem.getStatus() == 0 || owner == null) {
            return false;
        }
        ownerLastHurt = owner.getLastHurtMob();
        RegionData aliveRange = snowGolem.getAliveRange();
        if (snowGolem.getLocator() != 2 || aliveRange != null && ownerLastHurt != null && !aliveRange.inRegion(ownerLastHurt.position()) || !snowGolem.canPassiveAttackInAttackEnemyTeamMode(ownerLastHurt)) {
            return false;
        }
        int $$1 = owner.getLastHurtMobTimestamp();
        return $$1 != timestamp && canAttack(ownerLastHurt, TargetingConditions.DEFAULT);
    }

    public void start() {
        mob.setTarget(ownerLastHurt);
        LivingEntity owner = snowGolem.getOwner();
        if (owner != null) {
            timestamp = owner.getLastHurtMobTimestamp();
        }
        super.start();
    }
}
