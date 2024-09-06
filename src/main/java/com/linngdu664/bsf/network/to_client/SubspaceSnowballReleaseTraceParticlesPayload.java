package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.registry.ParticleRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SubspaceSnowballReleaseTraceParticlesPayload(double px, double py, double pz, double dx, double dy, double dz) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SubspaceSnowballReleaseTraceParticlesPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("subspace_snowball_release_trace_particles"));
    public static final StreamCodec<ByteBuf, SubspaceSnowballReleaseTraceParticlesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, SubspaceSnowballReleaseTraceParticlesPayload::px,
            ByteBufCodecs.DOUBLE, SubspaceSnowballReleaseTraceParticlesPayload::py,
            ByteBufCodecs.DOUBLE, SubspaceSnowballReleaseTraceParticlesPayload::pz,
            ByteBufCodecs.DOUBLE, SubspaceSnowballReleaseTraceParticlesPayload::dx,
            ByteBufCodecs.DOUBLE, SubspaceSnowballReleaseTraceParticlesPayload::dy,
            ByteBufCodecs.DOUBLE, SubspaceSnowballReleaseTraceParticlesPayload::dz,
            SubspaceSnowballReleaseTraceParticlesPayload::new
    );

    public static void handleDataInClient(SubspaceSnowballReleaseTraceParticlesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            for (int i = 0; i < 10; i++) {
                level.addParticle(ParticleRegister.SUBSPACE_SNOWBALL_RELEASE_TRACE.get(), payload.px, payload.py, payload.pz, payload.dx, payload.dy, payload.dz);
            }
        });
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
