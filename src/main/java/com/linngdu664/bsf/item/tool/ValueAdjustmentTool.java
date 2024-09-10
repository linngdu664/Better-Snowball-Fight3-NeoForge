package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.block.entity.VendingMachineEntity;
import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ValueAdjustmentTool extends Item {
    public ValueAdjustmentTool(Properties properties) {
        super(properties.stacksTo(1).component(DataComponentRegister.GENERIC_INT_VALUE.get(), 0));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (player.getAbilities().instabuild && level.getBlockEntity(context.getClickedPos()) instanceof VendingMachineEntity be) {
            if (!level.isClientSide) {
                int val = stack.getOrDefault(DataComponentRegister.GENERIC_INT_VALUE, 0);
                if (player.isShiftKeyDown()) {
                    be.setMinRank(val);
                    player.displayClientMessage(Component.literal("Set min rank to " + val), false);
                } else {
                    be.setPrice(val);
                    player.displayClientMessage(Component.literal("Set price to " + val), false);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
