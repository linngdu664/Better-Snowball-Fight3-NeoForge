package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record CurrentTeamPayload(byte teamId) implements CustomPacketPayload {
    public static byte currentTeam = -1;
    public static final CustomPacketPayload.Type<CurrentTeamPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("current_team"));
    public static final StreamCodec<ByteBuf, CurrentTeamPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, CurrentTeamPayload::teamId,
            CurrentTeamPayload::new
    );

    public static void handleDataInClient(CurrentTeamPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> currentTeam = payload.teamId);
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
