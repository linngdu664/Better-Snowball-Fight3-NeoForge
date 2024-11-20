package com.linngdu664.bsf.block;

import com.linngdu664.bsf.block.entity.VendingMachineBlockEntity;
import com.linngdu664.bsf.client.gui.screen.VendingMachineScreenShower;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

public class VendingMachineBlock extends Block implements EntityBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

    public VendingMachineBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new VendingMachineBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide && player.isCreative() && player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty() && level.getBlockEntity(pos) instanceof VendingMachineBlockEntity be) {
            // 因为客户端能拿到数据，所以直接显示gui，但是需要避免意外的类加载
            VendingMachineScreenShower.show(pos, be.getMinRank(), be.getPrice(), be.isCanSell());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.getAbilities().instabuild && level.getBlockEntity(pos) instanceof VendingMachineBlockEntity be) {
            if (hand == InteractionHand.OFF_HAND && !stack.isEmpty() && player.getMainHandItem().isEmpty()) {
                if (!level.isClientSide) {
                    be.setGoods(stack);
                    level.sendBlockUpdated(pos, state, state, 2);
                    player.displayClientMessage(Component.literal("Set goods to " + stack.getHoverName().getString()), false);
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
