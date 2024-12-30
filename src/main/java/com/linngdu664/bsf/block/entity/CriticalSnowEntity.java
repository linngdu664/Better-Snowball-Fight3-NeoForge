package com.linngdu664.bsf.block.entity;

import com.linngdu664.bsf.registry.BlockEntityRegister;
import com.linngdu664.bsf.registry.BlockRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CriticalSnowEntity extends BlockEntity {
    private int targetAge = BSFCommonUtil.staticRandInt(100, 140);
    private int age;

    public CriticalSnowEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegister.CRITICAL_SNOW.get(), pPos, pBlockState);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (!level.isClientSide) {
            CriticalSnowEntity criticalSnowEntity = (CriticalSnowEntity) blockEntity;
            if (criticalSnowEntity.age < criticalSnowEntity.targetAge) {
                criticalSnowEntity.age++;
                criticalSnowEntity.setChanged();
            } else {
                criticalSnowEntity.setRemoved();
                BlockState snow = Blocks.SNOW.defaultBlockState();
                if (level.getBlockState(pos).canBeReplaced() && snow.canSurvive(level, pos) && !level.getBlockState(pos.below()).getBlock().getName().getString().equals(BlockRegister.LOOSE_SNOW_BLOCK.get().getName().getString())) {
                    level.setBlockAndUpdate(pos, snow);
                } else {
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.SNOW_STEP, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, pos.getX(), pos.getY(), pos.getZ(), 5, 0, 0, 0, 0.12);
            }
        }
    }

    public void suicide() {
        this.age = this.targetAge;
        this.setChanged();
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        age = tag.getInt("age");
        targetAge = tag.getInt("target_age");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("age", age);
        tag.putInt("target_age", targetAge);
    }
}
