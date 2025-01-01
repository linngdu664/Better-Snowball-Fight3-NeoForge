package com.linngdu664.bsf.network.to_server;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.block.entity.RegionPlayerInspectorBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateRegionPlayerInspectorPayload(BlockPos blockPos, BlockPos kickPos, short permittedTeams, String clearDirectlyItems, boolean checkItem, boolean checkTeam) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateRegionPlayerInspectorPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("update_region_player_inspector"));
    public static final StreamCodec<ByteBuf, UpdateRegionPlayerInspectorPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, UpdateRegionPlayerInspectorPayload::blockPos,
            BlockPos.STREAM_CODEC, UpdateRegionPlayerInspectorPayload::kickPos,
            ByteBufCodecs.SHORT, UpdateRegionPlayerInspectorPayload::permittedTeams,
            ByteBufCodecs.STRING_UTF8, UpdateRegionPlayerInspectorPayload::clearDirectlyItems,
            ByteBufCodecs.BOOL, UpdateRegionPlayerInspectorPayload::checkItem,
            ByteBufCodecs.BOOL, UpdateRegionPlayerInspectorPayload::checkTeam,
            UpdateRegionPlayerInspectorPayload::new
    );

    public static void handleDataInServer(UpdateRegionPlayerInspectorPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();
            if (level.hasChunkAt(payload.blockPos) && level.getBlockEntity(payload.blockPos) instanceof RegionPlayerInspectorBlockEntity be) {
                be.setKickPos(payload.kickPos);
                be.setPermittedTeams(payload.permittedTeams);
                be.setClearDirectlyItems(payload.clearDirectlyItems);
                be.setCheckItem(payload.checkItem);
                be.setCheckTeam(payload.checkTeam);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
