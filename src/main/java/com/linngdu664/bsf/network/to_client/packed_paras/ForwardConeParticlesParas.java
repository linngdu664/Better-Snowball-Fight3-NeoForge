package com.linngdu664.bsf.network.to_client.packed_paras;

import com.linngdu664.bsf.network.CustomStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record ForwardConeParticlesParas(Vec3 eyePos, Vec3 sightVec, float r, float aStep, float rStep,
                                        double loweredVision) {
    public static final StreamCodec<ByteBuf, ForwardConeParticlesParas> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull ByteBuf byteBuf, @NotNull ForwardConeParticlesParas paras) {
            CustomStreamCodecs.VEC3_STREAM_CODEC.encode(byteBuf, paras.eyePos());
            CustomStreamCodecs.VEC3_STREAM_CODEC.encode(byteBuf, paras.sightVec());
            byteBuf.writeFloat(paras.r());
            byteBuf.writeFloat(paras.aStep());
            byteBuf.writeFloat(paras.rStep());
            byteBuf.writeDouble(paras.loweredVision());
        }

        @Override
        public @NotNull ForwardConeParticlesParas decode(@NotNull ByteBuf byteBuf) {
            Vec3 eyePos = CustomStreamCodecs.VEC3_STREAM_CODEC.decode(byteBuf);
            Vec3 sightVec = CustomStreamCodecs.VEC3_STREAM_CODEC.decode(byteBuf);
            float r = byteBuf.readFloat();
            float aStep = byteBuf.readFloat();
            float rStep = byteBuf.readFloat();
            double loweredVision = byteBuf.readDouble();
            return new ForwardConeParticlesParas(eyePos, sightVec, r, aStep, rStep, loweredVision);
        }
    };
}
