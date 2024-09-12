package com.linngdu664.bsf.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record IntegerGroupData(int val1, int val2, int val3, int val4) {
    public static final Codec<IntegerGroupData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("val1").forGetter(IntegerGroupData::val1),
                    Codec.INT.fieldOf("val2").forGetter(IntegerGroupData::val2),
                    Codec.INT.fieldOf("val3").forGetter(IntegerGroupData::val3),
                    Codec.INT.fieldOf("val4").forGetter(IntegerGroupData::val4)
            ).apply(instance, IntegerGroupData::new)
    );
    public static final StreamCodec<ByteBuf, IntegerGroupData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, IntegerGroupData::val1,
            ByteBufCodecs.INT, IntegerGroupData::val2,
            ByteBufCodecs.INT, IntegerGroupData::val3,
            ByteBufCodecs.INT, IntegerGroupData::val4,
            IntegerGroupData::new
    );
    public static final IntegerGroupData EMPTY = new IntegerGroupData(0, 0, 0, 0);
}
