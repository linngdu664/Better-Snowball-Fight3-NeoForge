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

public class GlassSnowballEntity extends AbstractNormalSnowballEntity {
    public GlassSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel, new BSFSnowballEntityProperties().basicDamage(3).basicBlazeDamage(5));
    }

    public GlassSnowballEntity(Level pLevel, double pX, double pY, double pZ, RegionData region) {
        super(EntityRegister.GLASS_SNOWBALL.get(), pX, pY, pZ, pLevel, new BSFSnowballEntityProperties().basicDamage(3).basicBlazeDamage(5), region);
    }

    public GlassSnowballEntity(LivingEntity pShooter, Level pLevel, ILaunchAdjustment launchAdjustment, RegionData region) {
        super(EntityRegister.GLASS_SNOWBALL.get(), pShooter, pLevel, new BSFSnowballEntityProperties().basicDamage(3).basicBlazeDamage(5).applyAdjustment(launchAdjustment), region);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ItemRegister.GLASS_SNOWBALL.get();
    }
}
