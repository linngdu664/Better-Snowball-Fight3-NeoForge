package com.linngdu664.bsf.network.to_server;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record UpdateGolemRankPayload(int id, int rank) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateGolemRankPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("update_golem_rank"));

    public static final StreamCodec<ByteBuf, UpdateGolemRankPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, UpdateGolemRankPayload::id,
            ByteBufCodecs.VAR_INT, UpdateGolemRankPayload::rank,
            UpdateGolemRankPayload::new
    );

    public static void handleDataInServer(UpdateGolemRankPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();
            if (level.getEntity(payload.id) instanceof BSFSnowGolemEntity golem) {
                golem.setRank(payload.rank);
            }
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
