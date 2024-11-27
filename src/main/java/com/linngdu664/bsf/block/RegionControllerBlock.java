package com.linngdu664.bsf.block;

import com.linngdu664.bsf.block.entity.RegionControllerBlockEntity;
import com.linngdu664.bsf.network.to_client.packed_paras.RegionControllerGuiParas;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.item.minigame_tool.TeamLinkerItem;
import com.linngdu664.bsf.network.to_client.ShowRegionControllerScreenPayload;
import com.linngdu664.bsf.registry.BlockEntityRegister;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RegionControllerBlock extends Block implements EntityBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);

    public RegionControllerBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RegionControllerBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == BlockEntityRegister.REGION_CONTROLLER_BLOCK_ENTITY.get() ? RegionControllerBlockEntity::tick : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof RegionControllerBlockEntity be) {
            // 空手点击时的逻辑，创造模式打开GUI，生存模式选择一个雪傀儡出生点传送
            if (level.isClientSide) {
                return InteractionResult.SUCCESS;       // 客户端直接ok
            }
            if (player.isCreative()) {
                PacketDistributor.sendToPlayer((ServerPlayer) player, new ShowRegionControllerScreenPayload(new RegionControllerGuiParas(
                        pos,
                        be.getRegion(),
                        be.getSummonPosList().size(),
                        be.getSnowGolemCount(),
                        BuiltInRegistries.BLOCK.getKey(be.getSpawnBlock()).toString(),
                        be.getPlayerMultiplier(),
                        be.getGolemMultiplier(),
                        be.getDiversity(),
                        be.getRankOffset(),
                        be.getFastestStrength(),
                        be.getSlowestStrength(),
                        be.getEnemyTeamNum(),
                        be.getMaxGolem()
                )));
            } else {
                randomTp(be, level, player);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof RegionControllerBlockEntity regionControllerBlockEntity && player.getAbilities().instabuild) {
            if (stack.getItem().equals(ItemRegister.REGION_TOOL.get())) {
                if (!level.isClientSide()) {
                    RegionData regionData = stack.getOrDefault(DataComponentRegister.REGION, RegionData.EMPTY);
                    if (regionData.start().getY() < regionData.end().getY()) {
                        regionControllerBlockEntity.setSnowGolemList(regionData);
                        player.displayClientMessage(Component.literal("Add " + regionControllerBlockEntity.getSnowGolemCount() + " golems"), false);
                    } else {
                        regionControllerBlockEntity.setRegionAndSummon(regionData);
                        player.displayClientMessage(Component.literal("Add " + regionControllerBlockEntity.getSummonPosList().size() + " spawn points"), false);
                    }
                }
                return ItemInteractionResult.SUCCESS;
            }
            if (stack.getItem() instanceof TeamLinkerItem teamLinkerItem) {
                if (!level.isClientSide()) {
                    byte teamId = teamLinkerItem.getTeamId();
                    regionControllerBlockEntity.setTeamId(teamId);
                    level.sendBlockUpdated(pos, state, state, 2);
                    player.displayClientMessage(Component.literal("Set controller team to " + DyeColor.byId(teamId).getName()), false);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;     // 传到空手右击的逻辑
    }

    private void randomTp(RegionControllerBlockEntity entity, Level level, Player player) {
        List<BlockPos> blockPosList = entity.getSummonPosList();
        if (!blockPosList.isEmpty()) {
            BlockPos blockPos = blockPosList.get(level.random.nextInt(blockPosList.size()));
            Vec3 pos = blockPos.above().getBottomCenter();
            player.teleportTo(pos.x, pos.y, pos.z);
            ((ServerPlayer) player).connection.send(new ClientboundSetEntityMotionPacket(player));
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
