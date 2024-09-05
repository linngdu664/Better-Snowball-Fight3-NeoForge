package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.network.CustomStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.UUID;

public record TeamMembersPayload(HashSet<UUID> members) implements CustomPacketPayload {
    public static HashSet<UUID> staticMembers = null;
    public static final CustomPacketPayload.Type<TeamMembersPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("team_members"));
    public static final StreamCodec<ByteBuf, TeamMembersPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(HashSet::new, CustomStreamCodecs.UUID_STREAM_CODEC), TeamMembersPayload::members,
            TeamMembersPayload::new
    );

    public static void handleDataInClient(TeamMembersPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> staticMembers = payload.members);
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
