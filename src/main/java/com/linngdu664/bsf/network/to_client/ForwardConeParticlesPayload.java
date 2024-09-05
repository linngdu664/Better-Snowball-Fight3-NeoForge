package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.network.CustomStreamCodecs;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.particle.util.ForwardConeParticlesParas;
import com.linngdu664.bsf.particle.util.ParticleUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ForwardConeParticlesPayload(ForwardConeParticlesParas paras, int particleType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ForwardConeParticlesPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("forward_cone_particles"));
    public static final StreamCodec<ByteBuf, ForwardConeParticlesPayload> STREAM_CODEC = StreamCodec.composite(
            CustomStreamCodecs.FORWARD_CONE_PARTICLES_PARAS_STREAM_CODEC, ForwardConeParticlesPayload::paras,
            ByteBufCodecs.VAR_INT, ForwardConeParticlesPayload::particleType,
            ForwardConeParticlesPayload::new
    );

    public static void handleDataInClient(ForwardConeParticlesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ParticleUtil.spawnForwardConeParticles(Minecraft.getInstance().level, BSFParticleType.values()[payload.particleType].get(), payload.paras));
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
