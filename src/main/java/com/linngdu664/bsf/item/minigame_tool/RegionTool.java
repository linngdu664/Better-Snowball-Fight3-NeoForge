package com.linngdu664.bsf.item.minigame_tool;

import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class RegionTool extends Item {
    public RegionTool() {
        super(new Properties().stacksTo(1).component(DataComponentRegister.REGION, RegionData.EMPTY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemInHand = context.getItemInHand();
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (!level.isClientSide) {
            BlockPos blockPos = context.getClickedPos();
            RegionData regionData = itemInHand.getOrDefault(DataComponentRegister.REGION, RegionData.EMPTY);
            if (player.isShiftKeyDown()) {
                itemInHand.set(DataComponentRegister.REGION, new RegionData(blockPos, regionData.end()));
                player.displayClientMessage(Component.literal("Start pos recorded"), false);
            } else {
                itemInHand.set(DataComponentRegister.REGION, new RegionData(regionData.start(), blockPos));
                player.displayClientMessage(Component.literal("start=" + regionData.start() + ", end=" + blockPos), false);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide && usedHand.equals(InteractionHand.MAIN_HAND)) {
            ItemStack offhandItem = player.getOffhandItem();
            ItemStack mainHandItem = player.getMainHandItem();
            offhandItem.set(DataComponentRegister.REGION, mainHandItem.getOrDefault(DataComponentRegister.REGION, RegionData.EMPTY));
        }
        return InteractionResultHolder.success(player.getMainHandItem());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        RegionData region = stack.getOrDefault(DataComponentRegister.REGION.get(), RegionData.EMPTY);
        tooltipComponents.add(Component.translatable(
                "scoring_device_region.tooltip",
                region.start().getX(),
                region.start().getY(),
                region.start().getZ(),
                region.end().getX(),
                region.end().getY(),
                region.end().getZ()
        ));
        tooltipComponents.add(MutableComponent.create(new PlainTextContents.LiteralContents("mode: " + (region.start().getY() > region.end().getY() ? "spawn point" : "golem"))));
    }
}
