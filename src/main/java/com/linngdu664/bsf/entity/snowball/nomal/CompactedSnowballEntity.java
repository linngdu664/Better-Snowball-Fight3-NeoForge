package com.linngdu664.bsf.entity.snowball.nomal;

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

public class CompactedSnowballEntity extends AbstractNormalSnowballEntity {
    public CompactedSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, new BSFSnowballEntityProperties().basicPunch(2));
    }

    public CompactedSnowballEntity(Level pLevel, double pX, double pY, double pZ, RegionData region) {
        super(EntityRegister.COMPACTED_SNOWBALL.get(), pX, pY, pZ, pLevel, new BSFSnowballEntityProperties().basicPunch(2), region);
    }

    public CompactedSnowballEntity(LivingEntity pShooter, Level pLevel, ILaunchAdjustment launchAdjustment, RegionData region) {
        super(EntityRegister.COMPACTED_SNOWBALL.get(), pShooter, pLevel, new BSFSnowballEntityProperties().basicPunch(2).applyAdjustment(launchAdjustment), region);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemRegister.COMPACTED_SNOWBALL.get();
    }
}
