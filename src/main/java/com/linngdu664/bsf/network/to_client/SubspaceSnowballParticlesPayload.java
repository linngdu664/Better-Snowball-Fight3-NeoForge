package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.particle.util.ParticleUtil;
import com.linngdu664.bsf.registry.ParticleRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SubspaceSnowballParticlesPayload(double px, double py, double pz, double range, int num) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SubspaceSnowballParticlesPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("subspace_snowball_particles"));
    public static final StreamCodec<ByteBuf, SubspaceSnowballParticlesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, SubspaceSnowballParticlesPayload::px,
            ByteBufCodecs.DOUBLE, SubspaceSnowballParticlesPayload::py,
            ByteBufCodecs.DOUBLE, SubspaceSnowballParticlesPayload::pz,
            ByteBufCodecs.DOUBLE, SubspaceSnowballParticlesPayload::range,
            ByteBufCodecs.VAR_INT, SubspaceSnowballParticlesPayload::num,
            SubspaceSnowballParticlesPayload::new
    );

    public static void handleDataInClient(SubspaceSnowballParticlesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ParticleUtil.spawnSubspaceSnowballParticles(Minecraft.getInstance().level, ParticleRegister.SUBSPACE_SNOWBALL_HIT_PARTICLE.get(), new Vec3(payload.px, payload.py, payload.pz), payload.range, payload.num));
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
