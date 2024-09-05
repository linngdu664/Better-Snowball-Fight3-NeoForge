package com.linngdu664.bsf.network;

import com.linngdu664.bsf.particle.util.ForwardConeParticlesParas;
import com.linngdu664.bsf.particle.util.ForwardRaysParticlesParas;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CustomStreamCodecs {
    public static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull ByteBuf byteBuf, @NotNull Vec3 vec3) {
            byteBuf.writeDouble(vec3.x);
            byteBuf.writeDouble(vec3.y);
            byteBuf.writeDouble(vec3.z);
        }

        @Override
        public @NotNull Vec3 decode(@NotNull ByteBuf byteBuf) {
            double x = byteBuf.readDouble();
            double y = byteBuf.readDouble();
            double z = byteBuf.readDouble();
            return new Vec3(x, y, z);
        }
    };
    public static final StreamCodec<ByteBuf, ForwardConeParticlesParas> FORWARD_CONE_PARTICLES_PARAS_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull ByteBuf byteBuf, @NotNull ForwardConeParticlesParas paras) {
            VEC3_STREAM_CODEC.encode(byteBuf, paras.eyePos());
            VEC3_STREAM_CODEC.encode(byteBuf, paras.sightVec());
            byteBuf.writeFloat(paras.r());
            byteBuf.writeFloat(paras.aStep());
            byteBuf.writeFloat(paras.rStep());
            byteBuf.writeDouble(paras.loweredVision());
        }

        @Override
        public @NotNull ForwardConeParticlesParas decode(@NotNull ByteBuf byteBuf) {
            Vec3 eyePos = VEC3_STREAM_CODEC.decode(byteBuf);
            Vec3 sightVec = VEC3_STREAM_CODEC.decode(byteBuf);
            float r = byteBuf.readFloat();
            float aStep = byteBuf.readFloat();
            float rStep = byteBuf.readFloat();
            double loweredVision = byteBuf.readDouble();
            return new ForwardConeParticlesParas(eyePos, sightVec, r, aStep, rStep, loweredVision);
        }
    };
    public static final StreamCodec<ByteBuf, ForwardRaysParticlesParas> FORWARD_RAYS_PARTICLES_PARAS_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull ByteBuf byteBuf, @NotNull ForwardRaysParticlesParas paras) {
            VEC3_STREAM_CODEC.encode(byteBuf, paras.pos1());
            VEC3_STREAM_CODEC.encode(byteBuf, paras.pos2());
            VEC3_STREAM_CODEC.encode(byteBuf, paras.vec());
            byteBuf.writeDouble(paras.vMin());
            byteBuf.writeDouble(paras.vMax());
            byteBuf.writeInt(paras.num());
        }

        @Override
        public @NotNull ForwardRaysParticlesParas decode(@NotNull ByteBuf byteBuf) {
            Vec3 pos1 = VEC3_STREAM_CODEC.decode(byteBuf);
            Vec3 pos2 = VEC3_STREAM_CODEC.decode(byteBuf);
            Vec3 vec = VEC3_STREAM_CODEC.decode(byteBuf);
            double vMin = byteBuf.readDouble();
            double vMax = byteBuf.readDouble();
            int num = byteBuf.readInt();
            return new ForwardRaysParticlesParas(pos1, pos2, vec, vMin, vMax, num);
        }
    };
    public static final StreamCodec<ByteBuf, UUID> UUID_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull ByteBuf byteBuf, @NotNull UUID uuid) {
            byteBuf.writeLong(uuid.getMostSignificantBits());
            byteBuf.writeLong(uuid.getLeastSignificantBits());
        }

        @Override
        public @NotNull UUID decode(@NotNull ByteBuf byteBuf) {
            return new UUID(byteBuf.readLong(), byteBuf.readLong());
        }
    };
}
