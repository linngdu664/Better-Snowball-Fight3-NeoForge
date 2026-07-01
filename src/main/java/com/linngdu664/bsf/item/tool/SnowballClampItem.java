package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.misc.BSFTiers;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SnowballClampItem extends Item {
    private static final TagKey<Block> NO_EFFICIENT_BLOCKS = TagKey.create(Registries.BLOCK, Main.makeResLoc("none"));
    private final boolean isForDuck;

    public SnowballClampItem(ToolMaterial material, int durability) {
        super(com.linngdu664.bsf.Main.itemProperties().tool(material, NO_EFFICIENT_BLOCKS, -1.0F, -2.0F, 0.0F).durability(durability));
        this.isForDuck = material == BSFTiers.EMERALD;
    }

    public boolean isForDuck() {
        return isForDuck;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();
        ItemStack itemStack = pContext.getItemInHand();
        Level level = pContext.getLevel();
        Block block = level.getBlockState(pContext.getClickedPos()).getBlock();
        if ((block == Blocks.SNOW_BLOCK || block == Blocks.SNOW || block == Blocks.POWDER_SNOW) && player != null) {
            if (player.getMainHandItem().isEmpty() || player.getOffhandItem().isEmpty()) {
                ItemStack stack = isForDuck ? ItemRegister.DUCK_SNOWBALL.get().getDefaultInstance() : ItemRegister.SMOOTH_SNOWBALL.get().getDefaultInstance();
                if (itemStack.has(DataComponentRegister.REGION.get())) {
                    stack.set(DataComponentRegister.REGION.get(), itemStack.get(DataComponentRegister.REGION.get()));
                }
                player.getInventory().placeItemBackInInventory(stack, true);
                itemStack.hurtAndBreak(1, player, pContext.getHand());
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack pStack, @NotNull Player pPlayer, @NotNull LivingEntity pInteractionTarget, @NotNull InteractionHand pUsedHand) {
        if (pInteractionTarget instanceof SnowGolem && (pPlayer.getMainHandItem().isEmpty() || pPlayer.getOffhandItem().isEmpty())) {
            pPlayer.getInventory().placeItemBackInInventory(isForDuck ? ItemRegister.DUCK_SNOWBALL.get().getDefaultInstance() : ItemRegister.SMOOTH_SNOWBALL.get().getDefaultInstance(), true);
            pStack.hurtAndBreak(1, pPlayer, pUsedHand);
            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("snowball_clamp.tooltip").withStyle(ChatFormatting.GRAY));
    }
}

