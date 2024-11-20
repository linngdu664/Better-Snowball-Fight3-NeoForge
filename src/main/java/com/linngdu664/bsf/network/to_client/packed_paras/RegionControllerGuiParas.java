package com.linngdu664.bsf.network.to_client.packed_paras;

import com.linngdu664.bsf.item.component.RegionData;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record RegionControllerGuiParas(
        BlockPos blockPos,
        RegionData region,
        int spawnNum,
        int golemNum,
        String spawnBlock,
        float playerMultiplier,
        float golemMultiplier,
        float diversity,
        int enemyTeamNum,
        int maxGolem
) {
    public static final StreamCodec<ByteBuf, RegionControllerGuiParas> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull ByteBuf byteBuf, @NotNull RegionControllerGuiParas regionControllerGuiParas) {
            BlockPos.STREAM_CODEC.encode(byteBuf, regionControllerGuiParas.blockPos());
            RegionData.STREAM_CODEC.encode(byteBuf, regionControllerGuiParas.region());
            VarInt.write(byteBuf, regionControllerGuiParas.spawnNum());
            VarInt.write(byteBuf, regionControllerGuiParas.golemNum());
            ByteBufCodecs.STRING_UTF8.encode(byteBuf, regionControllerGuiParas.spawnBlock());
            byteBuf.writeFloat(regionControllerGuiParas.playerMultiplier());
            byteBuf.writeFloat(regionControllerGuiParas.golemMultiplier());
            byteBuf.writeFloat(regionControllerGuiParas.diversity());
            VarInt.write(byteBuf, regionControllerGuiParas.enemyTeamNum());
            VarInt.write(byteBuf, regionControllerGuiParas.maxGolem());
        }

        @Override
        public @NotNull RegionControllerGuiParas decode(@NotNull ByteBuf byteBuf) {
            return new RegionControllerGuiParas(
                    BlockPos.STREAM_CODEC.decode(byteBuf),
                    RegionData.STREAM_CODEC.decode(byteBuf),
                    VarInt.read(byteBuf),
                    VarInt.read(byteBuf),
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
