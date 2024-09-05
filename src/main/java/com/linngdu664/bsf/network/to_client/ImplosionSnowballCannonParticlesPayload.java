package com.linngdu664.bsf.network.to_client;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.registry.ParticleRegister;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ImplosionSnowballCannonParticlesPayload(double px, double py, double pz, double dx, double dy, double dz) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ImplosionSnowballCannonParticlesPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("implosion_snowball_cannon_particles"));
    public static final StreamCodec<ByteBuf, ImplosionSnowballCannonParticlesPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, ImplosionSnowballCannonParticlesPayload::px,
            ByteBufCodecs.DOUBLE, ImplosionSnowballCannonParticlesPayload::py,
            ByteBufCodecs.DOUBLE, ImplosionSnowballCannonParticlesPayload::pz,
            ByteBufCodecs.DOUBLE, ImplosionSnowballCannonParticlesPayload::dx,
            ByteBufCodecs.DOUBLE, ImplosionSnowballCannonParticlesPayload::dy,
            ByteBufCodecs.DOUBLE, ImplosionSnowballCannonParticlesPayload::dz,
            ImplosionSnowballCannonParticlesPayload::new
    );

    public static void handleDataInClient(ImplosionSnowballCannonParticlesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            for (int i = 0; i < 20; i++) {
                level.addParticle(ParticleRegister.IMPLOSION_SNOWBALL_CANNON.get(), payload.px, payload.py, payload.pz, payload.dx, payload.dy, payload.dz);
            }
        });
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
