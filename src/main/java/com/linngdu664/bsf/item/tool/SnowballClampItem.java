package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.misc.BSFTiers;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SnowballClampItem extends TieredItem {
    public SnowballClampItem(Tier pTier, int durability) {
        super(pTier, new Properties().stacksTo(1).durability(durability));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();
        ItemStack itemStack = pContext.getItemInHand();
        Level level = pContext.getLevel();
        Block block = level.getBlockState(pContext.getClickedPos()).getBlock();
        if ((block == Blocks.SNOW_BLOCK || block == Blocks.SNOW || block == Blocks.POWDER_SNOW) && player != null) {
            if (player.getMainHandItem().isEmpty() || player.getOffhandItem().isEmpty()) {
                ItemStack stack;
                if (getTier().equals(BSFTiers.EMERALD)) {
                    stack = ItemRegister.DUCK_SNOWBALL.get().getDefaultInstance();
                } else {
                    stack = ItemRegister.SMOOTH_SNOWBALL.get().getDefaultInstance();
                }
                if (itemStack.has(DataComponentRegister.REGION.get())) {
                    stack.set(DataComponentRegister.REGION.get(), itemStack.get(DataComponentRegister.REGION.get()));
                }
                player.getInventory().placeItemBackInInventory(stack, true);
                itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(pContext.getHand()));
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack pStack, @NotNull Player pPlayer, @NotNull LivingEntity pInteractionTarget, @NotNull InteractionHand pUsedHand) {
        if (pInteractionTarget instanceof SnowGolem && (pPlayer.getMainHandItem().isEmpty() || pPlayer.getOffhandItem().isEmpty())) {
            if (getTier().equals(BSFTiers.EMERALD)) {
                pPlayer.getInventory().placeItemBackInInventory(ItemRegister.DUCK_SNOWBALL.get().getDefaultInstance(), true);
            } else {
                pPlayer.getInventory().placeItemBackInInventory(ItemRegister.SMOOTH_SNOWBALL.get().getDefaultInstance(), true);
            }
            pStack.hurtAndBreak(1, pPlayer, LivingEntity.getSlotForHand(pUsedHand));
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("snowball_clamp.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
