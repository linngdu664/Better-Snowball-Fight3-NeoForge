package com.linngdu664.bsf.block;

import com.linngdu664.bsf.block.entity.VendingMachineEntity;
import com.linngdu664.bsf.item.component.IntegerGroupData;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class VendingMachine extends Block implements EntityBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

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
        if (level.getBlockEntity(pos) instanceof VendingMachineEntity be && player.getAbilities().instabuild) {
            if (hand == InteractionHand.OFF_HAND && !stack.isEmpty() && !stack.getItem().equals(ItemRegister.VALUE_ADJUSTMENT_TOOL.get()) && player.getMainHandItem().isEmpty()) {
                if (!level.isClientSide) {
                    be.setGoods(stack);
                    level.sendBlockUpdated(pos, state, state, 2);
                    player.displayClientMessage(Component.literal("Set goods to " + stack.getHoverName().getString()), false);
                }
                return ItemInteractionResult.SUCCESS;
            }
            if (stack.getItem().equals(ItemRegister.VALUE_ADJUSTMENT_TOOL.get())) {
                if (!level.isClientSide) {
                    IntegerGroupData group = stack.getOrDefault(DataComponentRegister.INTEGER_GROUP, IntegerGroupData.EMPTY);
                    be.setMinRank(group.val1());
                    be.setPrice(group.val2());
                    be.setCanSell(group.val3() != 0);
                    level.sendBlockUpdated(pos, state, state, 2);
                    player.displayClientMessage(Component.literal("Set min rank to " + be.getMinRank()), false);
                    player.displayClientMessage(Component.literal("Set price to " + be.getPrice()), false);
                    player.displayClientMessage(Component.literal("Set allow sell to " + be.isCanSell()), false);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
