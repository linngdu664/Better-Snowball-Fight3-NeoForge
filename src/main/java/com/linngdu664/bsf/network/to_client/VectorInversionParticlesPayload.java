package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.particle.util.ParticleUtil;
import com.linngdu664.bsf.registry.ParticleRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record VectorInversionParticlesPayload(double px, double py, double pz, double range, double v, int num) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<VectorInversionParticlesPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("vector_inversion_particles"));
    public static final StreamCodec<ByteBuf, VectorInversionParticlesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, VectorInversionParticlesPayload::px,
            ByteBufCodecs.DOUBLE, VectorInversionParticlesPayload::py,
            ByteBufCodecs.DOUBLE, VectorInversionParticlesPayload::pz,
            ByteBufCodecs.DOUBLE, VectorInversionParticlesPayload::range,
            ByteBufCodecs.DOUBLE, VectorInversionParticlesPayload::v,
            ByteBufCodecs.VAR_INT, VectorInversionParticlesPayload::num,
            VectorInversionParticlesPayload::new
    );

    public static void handleDataInClient(VectorInversionParticlesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ParticleUtil.spawnVectorInversionParticles(
                    context.player().level(), ParticleRegister.VECTOR_INVERSION_PURPLE.get(),
                    new Vec3(payload.px, payload.py, payload.pz), payload.range, payload.num, payload.v
            );
            ParticleUtil.spawnVectorInversionParticles(
                    context.player().level(), ParticleRegister.VECTOR_INVERSION_RED.get(),
                    new Vec3(payload.px, payload.py, payload.pz), payload.range, payload.num, -payload.v
            );
        });
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
