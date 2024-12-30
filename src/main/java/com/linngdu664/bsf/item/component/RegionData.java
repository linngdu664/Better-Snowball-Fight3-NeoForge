package com.linngdu664.bsf.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class RegionData {
    public static final Codec<RegionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockPos.CODEC.fieldOf("start").forGetter(RegionData::start),
                    BlockPos.CODEC.fieldOf("end").forGetter(RegionData::end)
            ).apply(instance, RegionData::new)
    );
    public static final StreamCodec<ByteBuf, RegionData> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, RegionData::start,
            BlockPos.STREAM_CODEC, RegionData::end,
            RegionData::new
    );
    public static final RegionData EMPTY = new RegionData(BlockPos.ZERO, BlockPos.ZERO);

    private final BlockPos start, end;
    private final int minX, minY, minZ, maxX, maxY, maxZ;       // prevent replication computation
    public RegionData(BlockPos start, BlockPos end) {
        this.start = start;
        this.end = end;
        this.minX = Math.min(start.getX(), end.getX());
        this.minY = Math.min(start.getY(), end.getY());
        this.minZ = Math.min(start.getZ(), end.getZ());
        this.maxX = Math.max(start.getX(), end.getX());
        this.maxY = Math.max(start.getY(), end.getY());
        this.maxZ = Math.max(start.getZ(), end.getZ());
    }

    private RegionData(RegionData another) {
        this.start = new BlockPos(another.start.getX(), another.start.getY(), another.start.getZ());
        this.end = new BlockPos(another.end.getX(), another.end.getY(), another.end.getZ());
        this.minX = another.minX;
        this.minY = another.minY;
        this.minZ = another.minZ;
        this.maxX = another.maxX;
        this.maxY = another.maxY;
        this.maxZ = another.maxZ;
    }

    public static RegionData copy(RegionData another) {
        return another == null ? null : new RegionData(another);
    }

    public static RegionData loadFromCompoundTag(String key, CompoundTag tag) {
        if (tag.contains(key + "Start") && tag.contains(key + "End")) {
            BlockPos start = BlockPos.of(tag.getLong(key + "Start"));
            BlockPos end = BlockPos.of(tag.getLong(key + "End"));
            return new RegionData(start, end);
        }
        return null;
    }

    public void saveToCompoundTag(String key, CompoundTag tag) {
        tag.putLong(key + "Start", start.asLong());
        tag.putLong(key + "End", end.asLong());
    }

    public BlockPos start() {
        return start;
    }

    public BlockPos end() {
        return end;
    }

    public boolean inRegion(Vec3 vec3) {
        return vec3.x > minX && vec3.x < maxX + 1 && vec3.y > minY && vec3.y < maxY + 1 && vec3.z > minZ && vec3.z < maxZ + 1;
    }

    public boolean inRegion(BlockPos pos) {
        return pos.getX() >= minX && pos.getX() <= maxX && pos.getY() >= minY && pos.getY() <= maxY && pos.getZ() >= minZ && pos.getZ() <= maxZ;
    }

    public AABB toBoundingBox() {
        return new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionData that = (RegionData) o;
        return minX == that.minX && minY == that.minY && minZ == that.minZ && maxX == that.maxX && maxY == that.maxY && maxZ == that.maxZ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public String toString() {
        return "RegionData{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
