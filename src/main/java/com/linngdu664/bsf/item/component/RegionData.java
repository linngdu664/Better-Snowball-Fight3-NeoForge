package com.linngdu664.bsf.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

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

    public boolean inRegion(Vec3 vec3) {
        Vec3 min = new Vec3(Math.min(start.getX(), end.getX()), Math.min(start.getY(), end.getY()), Math.min(start.getZ(), end.getZ()));
        Vec3 max = new Vec3(Math.max(start.getX(), end.getX()) + 1, Math.max(start.getY(), end.getY()) + 1, Math.max(start.getZ(), end.getZ()) + 1);
        return vec3.x > min.x && vec3.x < max.x && vec3.y > min.y && vec3.y < max.y && vec3.z > min.z && vec3.z < max.z;
    }
}
