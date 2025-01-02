package com.linngdu664.bsf.network.to_server;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.block.entity.VendingMachineBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UpdateVendingMachinePayload(BlockPos blockPos, int rank, int price, boolean canSell) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateVendingMachinePayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("update_vending_machine"));
    public static final StreamCodec<ByteBuf, UpdateVendingMachinePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, UpdateVendingMachinePayload::blockPos,
            ByteBufCodecs.VAR_INT, UpdateVendingMachinePayload::rank,
            ByteBufCodecs.VAR_INT, UpdateVendingMachinePayload::price,
            ByteBufCodecs.BOOL, UpdateVendingMachinePayload::canSell,
            UpdateVendingMachinePayload::new
    );

    public static void handleDataInServer(UpdateVendingMachinePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();
            BlockPos blockPos = payload.blockPos();
            BlockState blockState = level.getBlockState(blockPos);
            if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof VendingMachineBlockEntity be) {
                be.setMinRank(payload.rank);
                be.setPrice(payload.price);
                be.setCanSell(payload.canSell);
                be.setChanged();
                level.sendBlockUpdated(blockPos, blockState, blockState, 2);    // 更新blockstate同步
            }
        });
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends UpdateVendingMachinePayload> type() {
        return TYPE;
    }
}
