package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class RegionTool extends Item {
    public RegionTool(Properties properties) {
        super(properties.component(DataComponentRegister.REGION, RegionData.EMPTY));
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
}
