package com.linngdu664.bsf.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;


public class LooseSnowBlock extends Block {
    public static final IntegerProperty FROZEN = IntegerProperty.create("frozen", 0, 1);

    public LooseSnowBlock() {
        super(Properties.ofLegacyCopy(Blocks.SNOW_BLOCK)
                .noLootTable()
                .noOcclusion()
                .strength(0.1f)
                .pushReaction(PushReaction.DESTROY)
                .isSuffocating((BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) -> false)
                .isRedstoneConductor((BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) -> false)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FROZEN, 0));
    }

    @Override
    public @NotNull VoxelShape getBlockSupportShape(@NotNull BlockState pState, @NotNull BlockGetter pReader, @NotNull BlockPos pPos) {
        return Shapes.empty();
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FROZEN);
    }
}
