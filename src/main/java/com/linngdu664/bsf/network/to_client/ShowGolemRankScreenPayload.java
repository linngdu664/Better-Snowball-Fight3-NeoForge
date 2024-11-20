package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.network.to_client.handler.ShowGolemRankScreenHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;


public record ShowGolemRankScreenPayload(int id, int rank) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ShowGolemRankScreenPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("show_golem_rank_screen"));

    public static final StreamCodec<ByteBuf, ShowGolemRankScreenPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ShowGolemRankScreenPayload::id,
            ByteBufCodecs.VAR_INT, ShowGolemRankScreenPayload::rank,
            ShowGolemRankScreenPayload::new
    );

    public static void handleDataInClient(ShowGolemRankScreenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ShowGolemRankScreenHandler.handlePayload(payload));     // to prevent class loading
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
