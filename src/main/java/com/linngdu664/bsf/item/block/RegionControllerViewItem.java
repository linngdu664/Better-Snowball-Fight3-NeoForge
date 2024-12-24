package com.linngdu664.bsf.item.block;

import com.linngdu664.bsf.block.entity.RegionControllerViewBlockEntity;
import com.linngdu664.bsf.registry.BlockRegister;
import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;

public class RegionControllerViewItem extends BlockItem {
    public RegionControllerViewItem() {
        super(BlockRegister.REGION_CONTROLLER_VIEW_BLOCK.get(), new Properties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        ItemStack itemStack = context.getItemInHand();
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (!itemStack.has(DataComponentRegister.BIND_POS)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("No binding"), false);
            }
            return InteractionResult.FAIL;
        }
        InteractionResult result = super.place(context);
        if (result != InteractionResult.FAIL && !level.isClientSide && level.getBlockEntity(context.getClickedPos()) instanceof RegionControllerViewBlockEntity be) {
            // 设置方块实体绑定
            be.setControllerBE(itemStack.get(DataComponentRegister.BIND_POS));
        }
        return result;
    }
}
