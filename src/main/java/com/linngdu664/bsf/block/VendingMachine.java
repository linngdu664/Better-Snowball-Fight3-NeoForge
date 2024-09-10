package com.linngdu664.bsf.block;

import com.linngdu664.bsf.block.entity.VendingMachineEntity;
import com.linngdu664.bsf.registry.ItemRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class VendingMachine extends Block implements EntityBlock {
    public VendingMachine() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new VendingMachineEntity(blockPos, blockState);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof VendingMachineEntity be) {
            if (player.getAbilities().instabuild && !stack.getItem().equals(ItemRegister.VALUE_ADJUSTMENT_TOOL.get())) {
                if (!level.isClientSide) {
                    be.setGoods(stack);
                    player.displayClientMessage(Component.literal("Set goods to " + stack.getHoverName().getString()), false);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
