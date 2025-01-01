package com.linngdu664.bsf.block;

import com.linngdu664.bsf.block.entity.RegionPlayerInspectorBlockEntity;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.network.to_client.ShowRegionPlayerInspectorScreenPayload;
import com.linngdu664.bsf.registry.BlockEntityRegister;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class RegionPlayerInspectorBlock extends Block implements EntityBlock {
    public RegionPlayerInspectorBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.BEDROCK));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new RegionPlayerInspectorBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return blockEntityType == BlockEntityRegister.REGION_PLAYER_INSPECTOR.get() ? RegionPlayerInspectorBlockEntity::tick : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof RegionPlayerInspectorBlockEntity be && player.getAbilities().instabuild) {
            if (!level.isClientSide()) {
                // 发包开gui了
                PacketDistributor.sendToPlayer((ServerPlayer) player, new ShowRegionPlayerInspectorScreenPayload(
                        pos,
                        be.getRegion(),
                        be.getKickPos(),
                        be.getPermittedTeams(),
                        be.getClearDirectlyItemsStr(),
                        be.isCheckItem(),
                        be.isCheckTeam()
                ));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof RegionPlayerInspectorBlockEntity be && player.getAbilities().instabuild) {
            Item item = stack.getItem();
            if (item.equals(ItemRegister.REGION_TOOL.get())) {
                if (!level.isClientSide()) {
                    be.setRegion(stack.getOrDefault(DataComponentRegister.REGION, RegionData.EMPTY));
                    player.displayClientMessage(Component.literal("Inspection area was set"), false);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;     // 传到空手右击的逻辑
    }
}
