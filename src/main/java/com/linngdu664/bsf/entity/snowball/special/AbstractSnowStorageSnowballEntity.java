package com.linngdu664.bsf.entity.snowball.special;

import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.registry.BlockRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("SnowStock", snowStock);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        snowStock = input.getIntOr("SnowStock", 0);
    }

    @Override
    protected void placeAndRecordBlock(Level level, BlockPos blockPos) {
        if (!level.isClientSide() && (getRegion() == null || getRegion().inRegion(blockPos)) && level.getBlockState(blockPos).canBeReplaced()) {
            level.setBlock(blockPos, BlockRegister.LOOSE_SNOW_BLOCK.get().defaultBlockState(), 3);
            snowStock--;
            allBlock.push(blockPos);
        }
    }
}
