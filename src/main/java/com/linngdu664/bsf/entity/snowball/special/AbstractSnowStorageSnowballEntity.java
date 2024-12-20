package com.linngdu664.bsf.entity.snowball.special;

import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.registry.BlockRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSnowStorageSnowballEntity extends AbstractConstructSnowballEntity {
    protected int snowStock = 0;

    public AbstractSnowStorageSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel, int duration) {
        super(pEntityType, pLevel, duration, new BSFSnowballEntityProperties().canBeCaught(false));
    }

    public AbstractSnowStorageSnowballEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, LivingEntity pShooter, Level pLevel, ILaunchAdjustment launchAdjustment, int snowStock, int duration, RegionData region) {
        super(pEntityType, pShooter, pLevel, duration, new BSFSnowballEntityProperties().canBeCaught(false).applyAdjustment(launchAdjustment), region);
        this.snowStock = snowStock;
    }


    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("SnowStock", snowStock);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        snowStock = pCompound.getInt("SnowStock");
    }

    @Override
    protected void placeAndRecordBlock(Level level, BlockPos blockPos) {
        if (!level.isClientSide && (getRegion() == null || getRegion().inRegion(blockPos)) && level.getBlockState(blockPos).canBeReplaced()) {
            level.setBlock(blockPos, BlockRegister.LOOSE_SNOW_BLOCK.get().defaultBlockState(), 3);
            snowStock--;
            allBlock.push(blockPos);
        }
    }
}
