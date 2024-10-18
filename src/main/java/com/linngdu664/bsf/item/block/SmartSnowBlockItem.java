package com.linngdu664.bsf.item.block;

import com.linngdu664.bsf.registry.BlockRegister;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class SmartSnowBlockItem extends BlockItem {
    public SmartSnowBlockItem() {
        super(BlockRegister.SMART_SNOW_BLOCK.get(), new Properties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block0.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block1.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block2.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block3.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block4.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block5.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block6.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block7.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block8.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block9.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block10.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block11.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block12.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block13.tooltip", null, new Object[0])).withStyle(ChatFormatting.BLUE));
            tooltipComponents.add(MutableComponent.create(new TranslatableContents("smart_snow_block14.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        } else {
            BSFCommonUtil.addTrans(tooltipComponents, "show_detail.tip", ChatFormatting.DARK_GRAY, "Ctrl");
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockPos blockPos1 = context.getClickedPos().below();
        ItemStack itemStack = context.getPlayer().getItemInHand(context.getHand());
        // 保证有了区域限制后只有能召唤出雪傀儡时才能放置
        if (itemStack.has(DataComponentRegister.REGION) && (!level.getBlockState(blockPos).getBlock().equals(Blocks.SNOW_BLOCK) || !level.getBlockState(blockPos1).getBlock().equals(Blocks.SNOW_BLOCK))) {
            return InteractionResult.FAIL;
        }
        return super.useOn(context);
    }
}
