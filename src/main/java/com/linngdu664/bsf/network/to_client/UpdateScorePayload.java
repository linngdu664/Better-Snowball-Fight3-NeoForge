package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.gui.ScoringGuiHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UpdateScorePayload(int score) implements CustomPacketPayload {
    public static final Type<UpdateScorePayload> TYPE = new Type<>(Main.makeResLoc("update_score"));
    public static final StreamCodec<ByteBuf, UpdateScorePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, UpdateScorePayload::score,
            UpdateScorePayload::new
    );

    public static void handleDataInClient(UpdateScorePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ScoringGuiHandler.set(payload.score));
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
