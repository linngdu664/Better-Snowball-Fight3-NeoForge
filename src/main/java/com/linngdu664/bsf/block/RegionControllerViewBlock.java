package com.linngdu664.bsf.block;

import com.linngdu664.bsf.block.entity.RegionControllerViewBlockEntity;
import com.linngdu664.bsf.registry.BlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RegionControllerViewBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);

    public RegionControllerViewBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isCreative() && level.getBlockEntity(pos) instanceof RegionControllerViewBlockEntity be) {
            // 空手点击时的逻辑
            if (!level.isClientSide) {
                BlockPos blockPos = be.getControllerBEBlockPos();
                player.displayClientMessage(Component.literal(String.format("Bind to (%d, %d, %d)", blockPos.getX(), blockPos.getY(), blockPos.getZ())), false);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RegionControllerViewBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == BlockEntityRegister.REGION_CONTROLLER_VIEW_BLOCK_ENTITY.get() ? RegionControllerViewBlockEntity::tick : null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
