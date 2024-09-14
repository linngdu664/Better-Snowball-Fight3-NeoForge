package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.particle.util.ParticleUtil;
import com.linngdu664.bsf.registry.ParticleRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SpawnSnowParticlesPayload(double px, double py, double pz,double height,int num) implements CustomPacketPayload {
    public static final Type<SpawnSnowParticlesPayload> TYPE = new Type<>(Main.makeResLoc("spawn_snow_particles"));
    public static final StreamCodec<ByteBuf, SpawnSnowParticlesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, SpawnSnowParticlesPayload::px,
            ByteBufCodecs.DOUBLE, SpawnSnowParticlesPayload::py,
            ByteBufCodecs.DOUBLE, SpawnSnowParticlesPayload::pz,
            ByteBufCodecs.DOUBLE, SpawnSnowParticlesPayload::height,
            ByteBufCodecs.VAR_INT, SpawnSnowParticlesPayload::num,
            SpawnSnowParticlesPayload::new
    );

    public static void handleDataInClient(SpawnSnowParticlesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            ParticleUtil.spawnSnowParticles(level,new Vec3(payload.px,payload.py,payload.pz),payload.height,payload.num);
        });
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
