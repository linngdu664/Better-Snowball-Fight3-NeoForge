package com.linngdu664.bsf.block.entity;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.registry.BlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ZoneControllerEntity extends BlockEntity {
    private ArrayList<CompoundTag> snowGolemList = new ArrayList<>();
    private ArrayList<BlockPos> summonPosList = new ArrayList<>();
    private BlockPos regionStart = BlockPos.ZERO, regionEnd = BlockPos.ZERO;
    private int currentRank;        // 同步到客户端
    private byte teamId;            // 同步到客户端

    public ZoneControllerEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegister.ZONE_CONTROLLER_ENTITY.get(), pos, blockState);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (!level.isClientSide && level.hasNeighborSignal(pos)) {
            // 启动！
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        summonPosList = new ArrayList<>();
        ListTag listTag = tag.getList("SummonPos", 10);
        for (int i = 0, size = listTag.size(); i < size; i++) {
            int x = listTag.getCompound(i).getInt("x");
            int y = listTag.getCompound(i).getInt("y");
            int z = listTag.getCompound(i).getInt("z");
            summonPosList.add(new BlockPos(x, y, z));
        }
        snowGolemList = new ArrayList<>();
        listTag = tag.getList("SnowGolem", 10);     // type 10 compound tag. see mc wiki.
        for (int i = 0, size = listTag.size(); i < size; i++) {
            snowGolemList.add(listTag.getCompound(i));
        }
        CompoundTag compoundTag = tag.getCompound("RegionStart");
        regionStart = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
        compoundTag = tag.getCompound("RegionEnd");
        regionEnd = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
        currentRank = compoundTag.getInt("CurrentRank");
        teamId = compoundTag.getByte("TeamId");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag listTag = new ListTag();
        int i = 0;
        for (BlockPos pos : summonPosList) {
            CompoundTag cTag = new CompoundTag();
            cTag.putInt("x", pos.getX());
            cTag.putInt("y", pos.getY());
            cTag.putInt("z", pos.getZ());
            listTag.addTag(i, cTag);
            i++;
        }
        tag.put("SummonPos", listTag);
        listTag = new ListTag();
        i = 0;
        for (CompoundTag cTag : snowGolemList) {
            listTag.addTag(i, cTag);
            i++;
        }
        tag.put("SnowGolem", listTag);
        CompoundTag cTag = new CompoundTag();
        cTag.putInt("x", regionStart.getX());
        cTag.putInt("y", regionStart.getY());
        cTag.putInt("z", regionStart.getZ());
        tag.put("RegionStart", cTag);
        cTag = new CompoundTag();
        cTag.putInt("x", regionEnd.getX());
        cTag.putInt("y", regionEnd.getY());
        cTag.putInt("z", regionEnd.getZ());
        tag.put("RegionEnd", cTag);
        tag.putInt("CurrentRank", currentRank);
        tag.putByte("TeamId", teamId);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        // send these data to client
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putByte("TeamId", teamId);
        tag.putInt("CurrentRank", currentRank);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        // in client these fields are valid
        super.handleUpdateTag(tag, lookupProvider);
        teamId = tag.getByte("TeamId");
        currentRank = tag.getInt("CurrentRank");
    }

    public void setRegionAndSummon(BlockPos regionStart, BlockPos regionEnd) {
        this.regionStart = regionStart;
        this.regionEnd = regionEnd;
        summonPosList = new ArrayList<>();
        BlockPos.betweenClosedStream(regionStart, regionEnd)
                .forEach(p -> {
                    if (level.getBlockState(p).getBlock() == Blocks.EMERALD_BLOCK) {
                        summonPosList.add(new BlockPos(p.getX(), p.getY(), p.getZ()));
                    }
                });
        setChanged();
    }

    public void setSnowGolemList(BlockPos regionStart, BlockPos regionEnd) {
        AABB aabb = new AABB(new Vec3(regionStart.getX(), regionStart.getY(), regionStart.getZ()), new Vec3(regionEnd.getX(), regionEnd.getY(), regionEnd.getZ()));
        List<BSFSnowGolemEntity> golemList = level.getEntitiesOfClass(BSFSnowGolemEntity.class, aabb, p -> true);
        snowGolemList = new ArrayList<>();
        for (BSFSnowGolemEntity golem : golemList) {
            CompoundTag compoundTag = new CompoundTag();
            golem.addAdditionalSaveData(compoundTag);
            snowGolemList.add(compoundTag);
        }
        setChanged();
    }

    public void setTeamId(byte teamId) {
        this.teamId = teamId;
        setChanged();
    }

    public ArrayList<BlockPos> getSummonPosList() {
        return summonPosList;
    }

    public int getSnowGolemCount() {
        return snowGolemList.size();
    }

    public int getCurrentRank() {
        return currentRank;
    }

    public byte getTeamId() {
        return teamId;
    }
}
