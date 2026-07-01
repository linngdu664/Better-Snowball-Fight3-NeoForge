package com.linngdu664.bsf.item.minigame_tool;

import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class RegionToolItem extends Item {
    public RegionToolItem() {
        super(com.linngdu664.bsf.Main.itemProperties().stacksTo(1).component(DataComponentRegister.REGION, RegionData.EMPTY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack itemInHand = context.getItemInHand();
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            BlockPos blockPos = context.getClickedPos();
            RegionData regionData = itemInHand.getOrDefault(DataComponentRegister.REGION, RegionData.EMPTY);
            if (!player.isShiftKeyDown()) {
                itemInHand.set(DataComponentRegister.REGION, new RegionData(blockPos, regionData.end()));
                player.sendSystemMessage(Component.literal("start: (" + blockPos.toShortString() + ")"));
            } else {
                itemInHand.set(DataComponentRegister.REGION, new RegionData(regionData.start(), blockPos));
                player.sendSystemMessage(Component.literal("end: (" + blockPos.toShortString() + ")"));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide() && usedHand.equals(InteractionHand.MAIN_HAND)) {
            ItemStack offhandItem = player.getOffhandItem();
            ItemStack mainHandItem = player.getMainHandItem();
            offhandItem.set(DataComponentRegister.REGION, mainHandItem.getOrDefault(DataComponentRegister.REGION, RegionData.EMPTY));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        RegionData region = stack.getOrDefault(DataComponentRegister.REGION.get(), RegionData.EMPTY);
        tooltipComponents.accept(Component.translatable(
                "scoring_device_region.tooltip",
                region.start().getX(),
                region.start().getY(),
                region.start().getZ(),
                region.end().getX(),
                region.end().getY(),
                region.end().getZ()
        ));
        tooltipComponents.accept(Component.literal("mode: " + (region.start().getY() > region.end().getY() ? "spawn point" : "golem")));
    }
}

