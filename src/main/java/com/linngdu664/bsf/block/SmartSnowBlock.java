package com.linngdu664.bsf.block;

import com.linngdu664.bsf.entity.AbstractBSFSnowGolemEntity;
import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.registry.BlockRegister;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EntityRegister;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class SmartSnowBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final MapCodec<SmartSnowBlock> CODEC = simpleCodec(SmartSnowBlock::new);
    private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    private static final Predicate<BlockState> PUMPKINS_PREDICATE = (p_51396_) -> p_51396_ != null && p_51396_.is(BlockRegister.SMART_SNOW_BLOCK.get());
    private BlockPattern snowGolemFull;

    public SmartSnowBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public void setPlacedBy(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @Nullable LivingEntity pPlacer, @NotNull ItemStack pStack) {
        if (pPlacer instanceof Player player) {
            BlockPattern.BlockPatternMatch blockPatternMatch = getOrCreateSnowGolemFull().find(pLevel, pPos);
            if (blockPatternMatch != null) {
                for (int i = 0; i < getOrCreateSnowGolemFull().getHeight(); ++i) {
                    BlockInWorld blockInWorld = blockPatternMatch.getBlock(0, i, 0);
                    pLevel.setBlock(blockInWorld.getPos(), Blocks.AIR.defaultBlockState(), 2);
                    pLevel.levelEvent(2001, blockInWorld.getPos(), Block.getId(blockInWorld.getState()));
                }
                BSFSnowGolemEntity snowGolem = EntityRegister.BSF_SNOW_GOLEM.get().create(pLevel);
                snowGolem.setOwnerUUID(player.getUUID());
                snowGolem.setOrderedToSit(true);
                snowGolem.setAliveRange(pStack.get(DataComponentRegister.REGION));
                snowGolem.setStyle((byte) (pLevel.getRandom().nextInt(0, AbstractBSFSnowGolemEntity.STYLE_NUM)));
                BlockPos blockPos = blockPatternMatch.getBlock(0, 2, 0).getPos();
                snowGolem.moveTo(blockPos.getX() + 0.5D, blockPos.getY() + 0.05D, blockPos.getZ() + 0.5D, 0.0F, 0.0F);
                pLevel.addFreshEntity(snowGolem);
                for (ServerPlayer serverplayer : pLevel.getEntitiesOfClass(ServerPlayer.class, snowGolem.getBoundingBox().inflate(5.0D))) {
                    CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, snowGolem);
                }
                for (int l = 0; l < getOrCreateSnowGolemFull().getHeight(); ++l) {
                    BlockInWorld blockInWorld = blockPatternMatch.getBlock(0, l, 0);
                    pLevel.blockUpdated(blockInWorld.getPos(), Blocks.AIR);
                }
            }
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    private BlockPattern getOrCreateSnowGolemFull() {
        if (snowGolemFull == null) {
            snowGolemFull = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }
        return snowGolemFull;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
