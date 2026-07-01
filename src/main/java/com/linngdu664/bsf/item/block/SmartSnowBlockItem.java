package com.linngdu664.bsf.item.block;

import com.linngdu664.bsf.registry.BlockRegister;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class SmartSnowBlockItem extends BlockItem {
    public SmartSnowBlockItem() {
        super(BlockRegister.SMART_SNOW_BLOCK.get(), com.linngdu664.bsf.Main.itemProperties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
            tooltipComponents.accept(Component.translatable("smart_snow_block0.tooltip").withStyle(ChatFormatting.BLUE));
            tooltipComponents.accept(Component.translatable("smart_snow_block1.tooltip").withStyle(ChatFormatting.GRAY));
            tooltipComponents.accept(Component.translatable("smart_snow_block2.tooltip").withStyle(ChatFormatting.BLUE));
            tooltipComponents.accept(Component.translatable("smart_snow_block3.tooltip").withStyle(ChatFormatting.GRAY));
            tooltipComponents.accept(Component.translatable("smart_snow_block4.tooltip").withStyle(ChatFormatting.GRAY));
            tooltipComponents.accept(Component.translatable("smart_snow_block5.tooltip").withStyle(ChatFormatting.GRAY));
            tooltipComponents.accept(Component.translatable("smart_snow_block6.tooltip").withStyle(ChatFormatting.GRAY));
            tooltipComponents.accept(Component.translatable("smart_snow_block7.tooltip").withStyle(ChatFormatting.BLUE));
            tooltipComponents.accept(Component.translatable("smart_snow_block8.tooltip").withStyle(ChatFormatting.GRAY));
            tooltipComponents.accept(Component.translatable("smart_snow_block9.tooltip").withStyle(ChatFormatting.BLUE));
            tooltipComponents.accept(Component.translatable("smart_snow_block10.tooltip").withStyle(ChatFormatting.GRAY));
            tooltipComponents.accept(Component.translatable("smart_snow_block11.tooltip").withStyle(ChatFormatting.GRAY));
            tooltipComponents.accept(Component.translatable("smart_snow_block12.tooltip").withStyle(ChatFormatting.GRAY));
            tooltipComponents.accept(Component.translatable("smart_snow_block13.tooltip").withStyle(ChatFormatting.BLUE));
            tooltipComponents.accept(Component.translatable("smart_snow_block14.tooltip").withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.accept(Component.translatable("show_detail.tip", "Ctrl").withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockPos blockPos1 = context.getClickedPos().below();
        ItemStack itemStack = context.getPlayer().getItemInHand(context.getHand());
        // 淇濊瘉鏈変簡鍖哄煙闄愬埗鍚庡彧鏈夎兘鍙敜鍑洪洩鍌€鍎℃椂鎵嶈兘鏀剧疆
        if (itemStack.has(DataComponentRegister.REGION) && (!level.getBlockState(blockPos).getBlock().equals(Blocks.SNOW_BLOCK) || !level.getBlockState(blockPos1).getBlock().equals(Blocks.SNOW_BLOCK))) {
            return InteractionResult.FAIL;
        }
        return super.useOn(context);
    }
}

