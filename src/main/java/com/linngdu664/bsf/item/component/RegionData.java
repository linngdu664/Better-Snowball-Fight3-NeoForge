package com.linngdu664.bsf.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
        int minX = Math.min(start.getX(), end.getX());
        int minY = Math.min(start.getY(), end.getY());
        int minZ = Math.min(start.getZ(), end.getZ());
        int maxX = Math.max(start.getX(), end.getX()) + 1;
        int maxY = Math.max(start.getY(), end.getY()) + 1;
        int maxZ = Math.max(start.getZ(), end.getZ()) + 1;
        return vec3.x > minX && vec3.x < maxX && vec3.y > minY && vec3.y < maxY && vec3.z > minZ && vec3.z < maxZ;
    }

    public boolean inRegion(BlockPos pos) {
        int minX = Math.min(start.getX(), end.getX());
        int minY = Math.min(start.getY(), end.getY());
        int minZ = Math.min(start.getZ(), end.getZ());
        int maxX = Math.max(start.getX(), end.getX()) + 1;
        int maxY = Math.max(start.getY(), end.getY()) + 1;
        int maxZ = Math.max(start.getZ(), end.getZ()) + 1;
        return pos.getX() >= minX && pos.getX() <= maxX && pos.getY() >= minY && pos.getY() <= maxY && pos.getZ() >= minZ && pos.getZ() <= maxZ;
    }

    public void saveToCompoundTag(String key, CompoundTag tag) {
        CompoundTag cTag = new CompoundTag();
        cTag.putInt("x", start.getX());
        cTag.putInt("y", start.getY());
        cTag.putInt("z", start.getZ());
        tag.put(key + "Start", cTag);
        cTag = new CompoundTag();
        cTag.putInt("x", end.getX());
        cTag.putInt("y", end.getY());
        cTag.putInt("z", end.getZ());
        tag.put(key + "End", cTag);
    }

    public static RegionData loadFromCompoundTag(String key, CompoundTag tag) {
        if (tag.contains(key + "Start") && tag.contains(key + "End")) {
            CompoundTag cTag = tag.getCompound("AliveRangeStart");
            BlockPos start = new BlockPos(cTag.getInt("x"), cTag.getInt("y"), cTag.getInt("z"));
            cTag = tag.getCompound("AliveRangeEnd");
            BlockPos end = new BlockPos(cTag.getInt("x"), cTag.getInt("y"), cTag.getInt("z"));
            return new RegionData(start, end);
        }
        return null;
    }
}
