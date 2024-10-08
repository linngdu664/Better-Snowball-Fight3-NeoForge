package com.linngdu664.bsf.entity.executor;

import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.registry.ItemRegister;
import com.linngdu664.bsf.registry.ParticleRegister;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PowderExecutor extends AbstractExecutor {
    public PowderExecutor(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public PowderExecutor(EntityType<?> pEntityType, double pX, double pY, double pZ, Level pLevel, RegionData region) {
        super(pEntityType, pLevel, 200, region);
        setPos(pX, pY, pZ);
    }

    @Override
    public void tick() {
        super.tick();
        Level level = level();
        if (!level.isClientSide) {
            ((ServerLevel) level).sendParticles(ParticleRegister.BIG_LONG_TIME_SNOWFLAKE.get(), this.getX(), this.getY(), this.getZ(), 8, 0, 0, 0, 0.2);
        }
    }

    @Override
    public float getSubspacePower() {
        return 1;
    }

    @Override
    public ItemStack getSnowballItem() {
        return new ItemStack(ItemRegister.POWDER_SNOWBALL.get());
    }
}
