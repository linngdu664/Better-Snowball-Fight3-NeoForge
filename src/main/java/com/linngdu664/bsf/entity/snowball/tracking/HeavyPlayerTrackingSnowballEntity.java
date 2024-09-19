package com.linngdu664.bsf.entity.snowball.tracking;

import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.registry.EntityRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class HeavyPlayerTrackingSnowballEntity extends AbstractPlayerTrackingSnowballEntity {
    public HeavyPlayerTrackingSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, new BSFSnowballEntityProperties().basicDamage(4).basicBlazeDamage(6), false);
    }

    public HeavyPlayerTrackingSnowballEntity(LivingEntity pShooter, Level pLevel, ILaunchAdjustment launchAdjustment, RegionData region) {
        super(EntityRegister.HEAVY_PLAYER_TRACKING_SNOWBALL.get(), pShooter, pLevel, new BSFSnowballEntityProperties().basicDamage(4).basicBlazeDamage(6).applyAdjustment(launchAdjustment), false, region);
    }

    @Override
    public float getSubspacePower() {
        return 1.5F;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemRegister.HEAVY_PLAYER_TRACKING_SNOWBALL.get();
    }
}
