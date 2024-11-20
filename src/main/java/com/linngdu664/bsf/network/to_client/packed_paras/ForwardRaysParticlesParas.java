package com.linngdu664.bsf.network.to_client.packed_paras;

import com.linngdu664.bsf.network.CustomStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record ForwardRaysParticlesParas(Vec3 pos1, Vec3 pos2, Vec3 vec, double vMin, double vMax, int num) {
    public static final StreamCodec<ByteBuf, ForwardRaysParticlesParas> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull ByteBuf byteBuf, @NotNull ForwardRaysParticlesParas paras) {
            CustomStreamCodecs.VEC3_STREAM_CODEC.encode(byteBuf, paras.pos1());
            CustomStreamCodecs.VEC3_STREAM_CODEC.encode(byteBuf, paras.pos2());
            CustomStreamCodecs.VEC3_STREAM_CODEC.encode(byteBuf, paras.vec());
            byteBuf.writeDouble(paras.vMin());
            byteBuf.writeDouble(paras.vMax());
            byteBuf.writeInt(paras.num());
        }

        @Override
        public @NotNull ForwardRaysParticlesParas decode(@NotNull ByteBuf byteBuf) {
            Vec3 pos1 = CustomStreamCodecs.VEC3_STREAM_CODEC.decode(byteBuf);
            Vec3 pos2 = CustomStreamCodecs.VEC3_STREAM_CODEC.decode(byteBuf);
            Vec3 vec = CustomStreamCodecs.VEC3_STREAM_CODEC.decode(byteBuf);
            double vMin = byteBuf.readDouble();
            double vMax = byteBuf.readDouble();
            int num = byteBuf.readInt();
            return new ForwardRaysParticlesParas(pos1, pos2, vec, vMin, vMax, num);
        }
    };
}
