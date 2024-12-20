package com.linngdu664.bsf.item.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SnowBlockBlenderItem extends AbstractBSFEnhanceableToolItem {
    public SnowBlockBlenderItem() {
        super(Rarity.COMMON, 256);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.NONE);
        BlockPos blockPos = blockHitResult.getBlockPos();
        if (!pLevel.getBlockState(blockPos).getBlock().equals(Blocks.SNOW_BLOCK)) {
            return InteractionResultHolder.pass(itemStack);
        }
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration) {
        if (pLivingEntity instanceof Player player) {
            BlockHitResult blockHitResult = getPlayerPOVHitResult(pLevel, player, ClipContext.Fluid.NONE);
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!pLevel.getBlockState(blockPos).getBlock().equals(Blocks.SNOW_BLOCK)) {
                player.stopUsingItem();
            } else if (!pLevel.isClientSide) {
                ServerLevel serverLevel = (ServerLevel) pLevel;
                RandomSource random = serverLevel.random;
                if (pRemainingUseDuration == 1) {
                    pLevel.setBlockAndUpdate(blockPos, Blocks.POWDER_SNOW.defaultBlockState());
                    for (int i = 0; i < 5; i++) {
                        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, blockPos.getX(), blockPos.getY() + random.nextDouble(), blockPos.getZ() + random.nextDouble(), 5, 0, 0, 0, 0.1);
                        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, blockPos.getX() + 1, blockPos.getY() + random.nextDouble(), blockPos.getZ() + random.nextDouble(), 5, 0, 0, 0, 0.1);
                        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, blockPos.getX() + random.nextDouble(), blockPos.getY(), blockPos.getZ() + random.nextDouble(), 5, 0, 0, 0, 0.1);
                        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, blockPos.getX() + random.nextDouble(), blockPos.getY() + 1, blockPos.getZ() + random.nextDouble(), 5, 0, 0, 0, 0.1);
                        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, blockPos.getX() + random.nextDouble(), blockPos.getY() + random.nextDouble(), blockPos.getZ(), 5, 0, 0, 0, 0.1);
                        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, blockPos.getX() + random.nextDouble(), blockPos.getY() + random.nextDouble(), blockPos.getZ() + 1, 5, 0, 0, 0, 0.1);
                    }
                    pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOW_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);

                    if (!player.getAbilities().instabuild) {
                        pStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                    }
                    player.awardStat(Stats.ITEM_USED.get(this));
                } else {
                    for (int i = 0; i < 5; i++) {
                        switch (random.nextInt(0, 6)) {
                            case 0 ->
                                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, blockPos.getX(), blockPos.getY() + random.nextDouble(), blockPos.getZ() + random.nextDouble(), 3, 0, 0, 0, 0.04);
                            case 1 ->
                                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, blockPos.getX() + 1, blockPos.getY() + random.nextDouble(), blockPos.getZ() + random.nextDouble(), 3, 0, 0, 0, 0.04);
                            case 2 ->
                                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, blockPos.getX() + random.nextDouble(), blockPos.getY(), blockPos.getZ() + random.nextDouble(), 3, 0, 0, 0, 0.04);
                            case 3 ->
                                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, blockPos.getX() + random.nextDouble(), blockPos.getY() + 1, blockPos.getZ() + random.nextDouble(), 3, 0, 0, 0, 0.04);
                            case 4 ->
                                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, blockPos.getX() + random.nextDouble(), blockPos.getY() + random.nextDouble(), blockPos.getZ(), 3, 0, 0, 0, 0.04);
                            case 5 ->
                                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, blockPos.getX() + random.nextDouble(), blockPos.getY() + random.nextDouble(), blockPos.getZ() + 1, 3, 0, 0, 0, 0.04);
                        }
                    }
                    if (pRemainingUseDuration % 2 == 1) {
                        pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOW_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                    }
                }
            }
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, LivingEntity livingEntity) {
        return 60;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack pStack, ItemStack pRepairCandidate) {
        return pRepairCandidate.is(Items.IRON_INGOT);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("snow_block_blender.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
