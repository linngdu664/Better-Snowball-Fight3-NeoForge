package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.network.to_client.handler.ShowRegionPlayerInspectorScreenHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record ShowRegionPlayerInspectorScreenPayload(BlockPos blockPos, RegionData region, BlockPos kickPos, short permittedTeams, List<String> clearDirectlyItems, boolean checkItem, boolean checkTeam) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ShowRegionPlayerInspectorScreenPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("show_region_player_inspector_screen"));
    public static final StreamCodec<ByteBuf, ShowRegionPlayerInspectorScreenPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ShowRegionPlayerInspectorScreenPayload::blockPos,
            RegionData.STREAM_CODEC, ShowRegionPlayerInspectorScreenPayload::region,
            BlockPos.STREAM_CODEC, ShowRegionPlayerInspectorScreenPayload::kickPos,
            ByteBufCodecs.SHORT, ShowRegionPlayerInspectorScreenPayload::permittedTeams,
            ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8), ShowRegionPlayerInspectorScreenPayload::clearDirectlyItems,
            ByteBufCodecs.BYTE, ShowRegionPlayerInspectorScreenPayload::packCheck,
            ShowRegionPlayerInspectorScreenPayload::new
    );

    private ShowRegionPlayerInspectorScreenPayload(BlockPos blockPos, RegionData region, BlockPos kickPos, short permittedTeams, List<String> directClearItems, byte packedCheck) {
        this(blockPos, region, kickPos, permittedTeams, directClearItems, (packedCheck & 1) != 0, (packedCheck & 2) != 0);
    }

    public static void handleDataInClient(ShowRegionPlayerInspectorScreenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ShowRegionPlayerInspectorScreenHandler.handlePayload(payload));     // to prevent class loading
    }

    public byte packCheck() {
        return (byte) ((checkItem ? 1 : 0) | (checkTeam ? 2 : 0));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
