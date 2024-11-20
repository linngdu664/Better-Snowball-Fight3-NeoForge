package com.linngdu664.bsf.network.to_server.packed_paras;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record UpdateRegionControllerParas(BlockPos blockPos, String spawnBlock, float playerMultiplier, float golemMultiplier, float diversity, int enemyTeamNum, int maxGolem) {
    public static final StreamCodec<ByteBuf, UpdateRegionControllerParas> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull ByteBuf byteBuf, @NotNull UpdateRegionControllerParas paras) {
            BlockPos.STREAM_CODEC.encode(byteBuf, paras.blockPos);
            ByteBufCodecs.STRING_UTF8.encode(byteBuf, paras.spawnBlock);
            byteBuf.writeFloat(paras.playerMultiplier);
            byteBuf.writeFloat(paras.golemMultiplier);
            byteBuf.writeFloat(paras.diversity);
            VarInt.write(byteBuf, paras.enemyTeamNum);
            VarInt.write(byteBuf, paras.maxGolem);
        }

        @Override
        public @NotNull UpdateRegionControllerParas decode(@NotNull ByteBuf byteBuf) {
            return new UpdateRegionControllerParas(
                    BlockPos.STREAM_CODEC.decode(byteBuf),
                    ByteBufCodecs.STRING_UTF8.decode(byteBuf),
                    byteBuf.readFloat(),
                    byteBuf.readFloat(),
                    byteBuf.readFloat(),
                    VarInt.read(byteBuf),
                    VarInt.read(byteBuf)
            );
        }
    };
}
