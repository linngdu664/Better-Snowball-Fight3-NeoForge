package com.linngdu664.bsf.item.component;

import com.linngdu664.bsf.network.CustomStreamCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record RegionData(Vec3 start, Vec3 end) {
    public static final Codec<RegionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Vec3.CODEC.fieldOf("start").forGetter(RegionData::start),
                    Vec3.CODEC.fieldOf("end").forGetter(RegionData::end)
            ).apply(instance, RegionData::new));
    public static final StreamCodec<ByteBuf, RegionData> STREAM_CODEC = StreamCodec.composite(
            CustomStreamCodecs.VEC3_STREAM_CODEC, RegionData::start,
            CustomStreamCodecs.VEC3_STREAM_CODEC, RegionData::end,
            RegionData::new
    );
}
