package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.network.to_client.packed_paras.ForwardRaysParticlesParas;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.particle.util.ParticleUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ForwardRaysParticlesPayload(ForwardRaysParticlesParas paras,
                                          int particleType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ForwardRaysParticlesPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("forward_rays_particles"));
    public static final StreamCodec<ByteBuf, ForwardRaysParticlesPayload> STREAM_CODEC = StreamCodec.composite(
            ForwardRaysParticlesParas.STREAM_CODEC, ForwardRaysParticlesPayload::paras,
            ByteBufCodecs.VAR_INT, ForwardRaysParticlesPayload::particleType,
            ForwardRaysParticlesPayload::new
    );

    public static void handleDataInClient(ForwardRaysParticlesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ParticleUtil.spawnForwardRaysParticles(context.player().level(), BSFParticleType.values()[payload.particleType].get(), payload.paras));
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
