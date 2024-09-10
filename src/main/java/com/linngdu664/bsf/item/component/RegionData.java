package com.linngdu664.bsf.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;

public record RegionData(BlockPos start, BlockPos end) {
    public static final Codec<RegionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos.CODEC.fieldOf("start").forGetter(RegionData::start),
                    BlockPos.CODEC.fieldOf("end").forGetter(RegionData::end)
            ).apply(instance, RegionData::new));
    public static final StreamCodec<ByteBuf, RegionData> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, RegionData::start,
            BlockPos.STREAM_CODEC, RegionData::end,
            RegionData::new
    );

    public static final RegionData EMPTY = new RegionData(BlockPos.ZERO, BlockPos.ZERO);
}
