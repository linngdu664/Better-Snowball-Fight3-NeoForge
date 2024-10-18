package com.linngdu664.bsf.block.entity;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import com.linngdu664.bsf.network.to_client.ForwardRaysParticlesPayload;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.particle.util.ForwardRaysParticlesParas;
import com.linngdu664.bsf.registry.*;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RegionControllerEntity extends BlockEntity {
    private static final float LN2 = 0.69314718F;
    private static final float[] LN_TABLE = new float[256];

    static {
        for (int i = 0; i < 256; i++) {
            LN_TABLE[i] = (float) Math.log(1.0 + (double) i / 256.0);
        }
    }

    private ArrayList<CompoundTag> snowGolemList = new ArrayList<>();
    private ArrayList<BlockPos> summonPosList = new ArrayList<>();
    private RegionData region = RegionData.EMPTY;
    private int playerMultiplier;
    private int golemMultiplier;
    private int diffMultiplier;
    private int lHalf;
    private int maxGolem;
    private int timer;                // 不需要持久化
    private float currentStrength;    // 同步到客户端
    private byte teamId;              // 同步到客户端

    public RegionControllerEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegister.REGION_CONTROLLER_ENTITY.get(), pos, blockState);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (!level.isClientSide && level.hasNeighborSignal(pos) && blockEntity instanceof RegionControllerEntity be && !be.snowGolemList.isEmpty()) {
            if (be.timer <= 0) {
                float enemyGolemStrength = 0;
                float enemyPlayerStrength = 0;
                float friendlyGolemStrength = 0;
                float friendlyPlayerStrength = 0;
                List<BSFSnowGolemEntity> friendlyGolemList = level.getEntitiesOfClass(BSFSnowGolemEntity.class, be.region.toBoundingBox(), p -> p.getFixedTeamId() >= 0 && p.getFixedTeamId() == be.teamId);
                List<BSFSnowGolemEntity> enemyGolemList = level.getEntitiesOfClass(BSFSnowGolemEntity.class, be.region.toBoundingBox(), p -> p.getFixedTeamId() >= 0 && p.getFixedTeamId() != be.teamId);
                for (BSFSnowGolemEntity golem : friendlyGolemList) {
                    friendlyGolemStrength += RegionControllerEntity.lnRank(golem.getRank());
                }
                for (BSFSnowGolemEntity golem : enemyGolemList) {
                    enemyGolemStrength += RegionControllerEntity.lnRank(golem.getRank());
                }
                List<? extends Player> playerList = level.players();
                BSFTeamSavedData savedData = level.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
                for (Player player : playerList) {
                    if (be.region.inRegion(player.position()) && !player.isCreative() && !player.isSpectator()) {
                        List<ItemStack> scoringDevices = BSFCommonUtil.findInventoryItemStacks(player, p -> p.getItem().equals(ItemRegister.SCORING_DEVICE.get()));
                        if (savedData.getTeam(player.getUUID()) != be.teamId) {
                            for (ItemStack scoringDevice : scoringDevices) {
                                enemyPlayerStrength += RegionControllerEntity.lnRank(scoringDevice.getOrDefault(DataComponentRegister.RANK.get(), 0));
                            }
                        } else {
                            for (ItemStack scoringDevice : scoringDevices) {
                                friendlyPlayerStrength += RegionControllerEntity.lnRank(scoringDevice.getOrDefault(DataComponentRegister.RANK.get(), 0));
                            }
                        }
                    }
                }
                float minRank = be.snowGolemList.getFirst().getInt("Rank");
                float maxRank = be.snowGolemList.getLast().getInt("Rank");
                be.currentStrength = be.diffMultiplier * 0.01F * (be.golemMultiplier * (enemyGolemStrength - friendlyGolemStrength) + be.playerMultiplier * (enemyPlayerStrength - friendlyPlayerStrength)) + 0.5F * (minRank + maxRank);
                level.sendBlockUpdated(pos, state, state, 2);
                float mu = Mth.clamp(be.currentStrength, minRank - be.lHalf * 0.5F, maxRank + be.lHalf * 0.5F);
                if (friendlyGolemList.size() < be.maxGolem) {
                    int size = be.snowGolemList.size();
                    float[] cumulativeDistribution = new float[size];
                    float total = 0;
                    for (int i = 0; i < size; i++) {
                        float f = (be.snowGolemList.get(i).getInt("Rank") - mu) / be.lHalf;
                        float val = (float) Math.exp(f * f * -0.5F);
//                        float val = Math.max(0, -Mth.abs(mu - rank) / be.lHalf + 1);
                        cumulativeDistribution[i] = val;
                        total += val;
                    }
                    for (int i = 0; i < size; i++) {
                        cumulativeDistribution[i] /= total;
                    }
                    for (int i = 1; i < size; i++) {
                        cumulativeDistribution[i] += cumulativeDistribution[i - 1];
                    }
                    float randNum = level.random.nextFloat();
                    for (int i = 0; i < size; i++) {
                        if (randNum < cumulativeDistribution[i]) {
                            // summon golem
                            List<BlockPos> blockPosList = be.summonPosList;
                            if (!blockPosList.isEmpty()) {
                                BlockPos blockPos = blockPosList.get(level.random.nextInt(blockPosList.size()));
                                Vec3 summonPos = blockPos.above().getBottomCenter();
                                BSFSnowGolemEntity snowGolem = EntityRegister.BSF_SNOW_GOLEM.get().create(level);
                                snowGolem.readAdditionalSaveData(be.snowGolemList.get(i));
                                snowGolem.setFixedTeamId(be.teamId);
                                snowGolem.setAliveRange(be.region);
                                snowGolem.setOwnerUUID(null);
                                snowGolem.setOrderedToSit(false);
                                snowGolem.setTame(false, false);
                                snowGolem.setLocator((byte) 2);
                                snowGolem.setStatus((byte) 3);
                                snowGolem.setDropEquipment(false);
                                snowGolem.setDropSnowball(false);
                                snowGolem.moveTo(summonPos.x, summonPos.y, summonPos.z, 0.0F, 0.0F);
                                level.addFreshEntity(snowGolem);
                                Vec3 color = new Vec3(0.9, 0.9, 0.9);
                                PacketDistributor.sendToPlayersTrackingEntity(snowGolem, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(snowGolem.getPosition(1).add(-0.5, 0, -0.5), snowGolem.getPosition(1).add(0.5, 1, 0.5), color, color.length(), color.length(), 100), BSFParticleType.SPAWN_SNOW.ordinal()));
                                snowGolem.playSound(SoundRegister.FORCE_EXECUTOR_START.get(), 3.0F, 1.0F);
                            }
                            break;
                        }
                    }
                }
                int minTime = (int) (60F / (minRank - maxRank - be.lHalf) * (mu - maxRank - be.lHalf * 0.5F) + 20F);
                be.timer = level.random.nextIntBetweenInclusive(minTime, 2 * minTime);
            } else {
                be.timer--;
            }
        }
    }

    private static float lnRank(int rank) {
        if (rank <= 0) {
            return 0F;
        }
        int fRank = Float.floatToIntBits((float) rank);
        return (float) ((fRank >> 23) - 127) * LN2 + LN_TABLE[(fRank >> 15) & 0xff];
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
        region = RegionData.loadFromCompoundTag("Region", tag);
        if (region == null) {
            region = RegionData.EMPTY;
        }
        playerMultiplier = tag.getInt("PlayerMultiplier");
        golemMultiplier = tag.getInt("GolemMultiplier");
        diffMultiplier = tag.getInt("DiffMultiplier");
        lHalf = tag.getInt("LHalf");
        maxGolem = tag.getInt("MaxGolem");
        currentStrength = tag.getFloat("CurrentStrength");
        teamId = tag.getByte("TeamId");
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
        region.saveToCompoundTag("Region", tag);
        tag.putInt("PlayerMultiplier", playerMultiplier);
        tag.putInt("GolemMultiplier", golemMultiplier);
        tag.putInt("DiffMultiplier", diffMultiplier);
        tag.putInt("LHalf", lHalf);
        tag.putInt("MaxGolem", maxGolem);
        tag.putFloat("CurrentStrength", currentStrength);
        tag.putByte("TeamId", teamId);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        // send these data to client
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putByte("TeamId", teamId);
        tag.putFloat("CurrentStrength", currentStrength);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        // in client these fields are valid
        super.handleUpdateTag(tag, lookupProvider);
        teamId = tag.getByte("TeamId");
        currentStrength = tag.getFloat("CurrentStrength");
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void setRegionAndSummon(RegionData region) {
        this.region = RegionData.copy(region);       // copy
        summonPosList = new ArrayList<>();
        BlockPos.betweenClosedStream(region.start(), region.end())
                .forEach(p -> {
                    if (level.getBlockState(p).getBlock() == Blocks.EMERALD_BLOCK) {
                        summonPosList.add(new BlockPos(p.getX(), p.getY(), p.getZ()));
                    }
                });
        setChanged();
    }

    public void setSnowGolemList(RegionData region) {
        List<BSFSnowGolemEntity> golemList = level.getEntitiesOfClass(BSFSnowGolemEntity.class, region.toBoundingBox(), p -> true);
        snowGolemList = new ArrayList<>();
        for (BSFSnowGolemEntity golem : golemList) {
            CompoundTag compoundTag = new CompoundTag();
            golem.addAdditionalSaveData(compoundTag);
            snowGolemList.add(compoundTag);
        }
        snowGolemList.sort(Comparator.comparingInt(e -> e.getInt("Rank")));
        setChanged();
    }

    public byte getTeamId() {
        return teamId;
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

    public float getCurrentStrength() {
        return currentStrength;
    }

    public void setPlayerMultiplier(int playerMultiplier) {
        this.playerMultiplier = playerMultiplier;
        setChanged();
    }

    public void setGolemMultiplier(int golemMultiplier) {
        this.golemMultiplier = golemMultiplier;
        setChanged();
    }

    public void setDiffMultiplier(int diffMultiplier) {
        this.diffMultiplier = diffMultiplier;
        setChanged();
    }

    public void setLHalf(int lHalf) {
        this.lHalf = lHalf;
        setChanged();
    }

    public void setMaxGolem(int maxGolem) {
        this.maxGolem = maxGolem;
        setChanged();
    }
}
