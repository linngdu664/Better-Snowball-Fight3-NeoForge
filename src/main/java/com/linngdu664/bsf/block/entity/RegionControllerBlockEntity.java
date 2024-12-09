package com.linngdu664.bsf.block.entity;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import com.linngdu664.bsf.network.to_client.ForwardRaysParticlesPayload;
import com.linngdu664.bsf.network.to_client.packed_paras.ForwardRaysParticlesParas;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.registry.*;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RegionControllerBlockEntity extends BlockEntity {
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
    private Block spawnBlock = Blocks.AIR;
    private float playerMultiplier;
    private float golemMultiplier;
    private float diversity;
    private float rankOffset;
    private float fastestStrength;
    private float slowestStrength;
    private int enemyTeamNum;
    private int maxGolem;

    private int timer;                // 定时器，不需要持久化
    private float probability;        // 刷新概率，不需要持久化
    private float currentStrength;    // 同步到客户端
    private byte teamId;              // 同步到客户端

    public RegionControllerBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegister.REGION_CONTROLLER_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (level.isClientSide || !level.hasNeighborSignal(pos) || !(blockEntity instanceof RegionControllerBlockEntity be) || be.snowGolemList.isEmpty() || be.summonPosList.isEmpty() || be.enemyTeamNum == 0) {
            return;
        }
        if (be.timer < 20) {
            be.timer++;
            return;
        }

        List<? extends Player> playerList = level.players();
        BSFTeamSavedData savedData = level.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
        float enemyGolemStrength = 0;
        float enemyPlayerStrength = 0;
        float friendlyGolemStrength = 0;
        float friendlyPlayerStrength = 0;
        List<BSFSnowGolemEntity> friendlyGolemList = level.getEntitiesOfClass(BSFSnowGolemEntity.class, be.region.toBoundingBox(), p -> p.getFixedTeamId() >= 0 && p.getFixedTeamId() == be.teamId);
        List<BSFSnowGolemEntity> enemyGolemList = level.getEntitiesOfClass(BSFSnowGolemEntity.class, be.region.toBoundingBox(), p -> p.getFixedTeamId() >= 0 && p.getFixedTeamId() != be.teamId);
        for (BSFSnowGolemEntity golem : friendlyGolemList) {
            friendlyGolemStrength += RegionControllerBlockEntity.lnRank(golem.getRank());
        }
        for (BSFSnowGolemEntity golem : enemyGolemList) {
            enemyGolemStrength += RegionControllerBlockEntity.lnRank(golem.getRank());
        }
        for (Player player : playerList) {
            if (!be.region.inRegion(player.position()) || player.isCreative() || player.isSpectator()) {
                continue;
            }
            int playerTeamId = savedData.getTeam(player.getUUID());
            if (playerTeamId == -1) {
                // 没加队伍的，传送走
                player.teleportTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                player.displayClientMessage(Component.translatable("region_controller_no_team_tip"), false);
            } else {
                // 只计算敌队和我方玩家强度
                List<ItemStack> scoringDevices = BSFCommonUtil.findInventoryItemStacks(player, p -> p.getItem().equals(ItemRegister.SCORING_DEVICE.get()));
                if (playerTeamId != be.teamId) {
                    for (ItemStack scoringDevice : scoringDevices) {
                        enemyPlayerStrength += RegionControllerBlockEntity.lnRank(scoringDevice.getOrDefault(DataComponentRegister.RANK, 0));
                    }
                } else {
                    for (ItemStack scoringDevice : scoringDevices) {
                        friendlyPlayerStrength += RegionControllerBlockEntity.lnRank(scoringDevice.getOrDefault(DataComponentRegister.RANK, 0));
                    }
                    // 扫描自己队伍的背包，把偷渡的东西全部清了
                    List<ItemStack> bannedItems = BSFCommonUtil.findInventoryItemStacks(player, p -> !be.region.equals(p.getOrDefault(DataComponentRegister.REGION, RegionData.EMPTY)));
                    for (ItemStack itemStack : bannedItems) {
                        itemStack.setCount(0);
                    }
                }
            }
        }
        be.currentStrength = be.golemMultiplier * (enemyGolemStrength / be.enemyTeamNum - friendlyGolemStrength) + be.playerMultiplier * (enemyPlayerStrength / be.enemyTeamNum - friendlyPlayerStrength);
        level.sendBlockUpdated(pos, state, state, 2);       // 强度同步到客户端

        if (be.probability <= 0 || level.random.nextFloat() < be.probability) {
            // 这一刻要尝试生成雪傀儡
            // 设置概率，对应期望为1.25s-6.25s
            if (be.currentStrength < be.slowestStrength) {
                be.probability = 0.16F;
            } else if (be.currentStrength < be.fastestStrength) {
                be.probability = 1F / (5F * (be.currentStrength - be.fastestStrength) / (be.slowestStrength - be.fastestStrength) + 1.25F);
            } else {
                be.probability = 0.8F;
            }
            if (friendlyGolemList.size() < be.maxGolem) {
                float minRank = be.snowGolemList.getFirst().getInt("Rank");
                float maxRank = be.snowGolemList.getLast().getInt("Rank");
                float mu = Mth.clamp(be.currentStrength + be.rankOffset, minRank - be.diversity, maxRank + be.diversity);
                int size = be.snowGolemList.size();
                float[] cumulativeDistribution = new float[size];
                float total = 0;
                for (int i = 0; i < size; i++) {
                    float f = (be.snowGolemList.get(i).getInt("Rank") - mu) / be.diversity;
                    float val = (float) Math.exp(f * f * -0.5F);        // 左右平移的正态分布
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
                        break;
                    }
                }
            }
        }
        be.timer = 0;
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
        for (long summonPose : tag.getLongArray("SummonPos")) {
            summonPosList.add(BlockPos.of(summonPose));
        }
        snowGolemList = new ArrayList<>();
        ListTag listTag = tag.getList("SnowGolem", 10);     // type 10 compound tag. see mc wiki.
        for (int i = 0, size = listTag.size(); i < size; i++) {
            snowGolemList.add(listTag.getCompound(i));
        }
        region = RegionData.loadFromCompoundTag("Region", tag);
        if (region == null) {
            region = RegionData.EMPTY;
        }
        spawnBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(tag.getString("SpawnBlock")));
        playerMultiplier = tag.getFloat("PlayerMultiplier");
        golemMultiplier = tag.getFloat("GolemMultiplier");
        diversity = tag.getFloat("Diversity");
        rankOffset = tag.getFloat("RankOffset");
        fastestStrength = tag.getFloat("FastestStrength");
        slowestStrength = tag.getFloat("SlowestStrength");
        enemyTeamNum = tag.getInt("EnemyTeamNum");
        maxGolem = tag.getInt("MaxGolem");
        currentStrength = tag.getFloat("CurrentStrength");
        teamId = tag.getByte("TeamId");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        long[] summonPoses = new long[summonPosList.size()];
        for (int i = 0, size = summonPosList.size(); i < size; i++) {
            summonPoses[i] = summonPosList.get(i).asLong();
        }
        tag.putLongArray("SummonPos", summonPoses);
        ListTag listTag = new ListTag();
        int i = 0;
        for (CompoundTag cTag : snowGolemList) {
            listTag.addTag(i, cTag);
            i++;
        }
        tag.put("SnowGolem", listTag);
        region.saveToCompoundTag("Region", tag);
        tag.putString("SpawnBlock", BuiltInRegistries.BLOCK.getKey(spawnBlock).toString());
        tag.putFloat("PlayerMultiplier", playerMultiplier);
        tag.putFloat("GolemMultiplier", golemMultiplier);
        tag.putFloat("Diversity", diversity);
        tag.putFloat("RankOffset", rankOffset);
        tag.putFloat("FastestStrength", fastestStrength);
        tag.putFloat("SlowestStrength", slowestStrength);
        tag.putInt("EnemyTeamNum", enemyTeamNum);
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

    public RegionData getRegion() {
        return region;
    }

    public ArrayList<BlockPos> getSummonPosList() {
        return summonPosList;
    }

    public void setRegionAndSummon(RegionData region) {
        this.region = RegionData.copy(region);       // copy
        summonPosList = new ArrayList<>();
        BlockPos.betweenClosedStream(region.start(), region.end()).forEach(p -> {
            if (level.getBlockState(p).getBlock() == spawnBlock) {
                summonPosList.add(new BlockPos(p.getX(), p.getY(), p.getZ()));
            }
        });
        setChanged();
    }

    public int getSnowGolemCount() {
        return snowGolemList.size();
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

    public Block getSpawnBlock() {
        return spawnBlock;
    }

    public void setSpawnBlock(Block block) {
        this.spawnBlock = block;
        setChanged();
    }

    public float getPlayerMultiplier() {
        return playerMultiplier;
    }

    public void setPlayerMultiplier(float playerMultiplier) {
        this.playerMultiplier = playerMultiplier;
        setChanged();
    }

    public float getGolemMultiplier() {
        return golemMultiplier;
    }

    public void setGolemMultiplier(float golemMultiplier) {
        this.golemMultiplier = golemMultiplier;
        setChanged();
    }

    public float getDiversity() {
        return diversity;
    }

    public void setDiversity(float diversity) {
        this.diversity = diversity;
        setChanged();
    }

    public float getRankOffset() {
        return rankOffset;
    }

    public void setRankOffset(float rankOffset) {
        this.rankOffset = rankOffset;
        setChanged();
    }

    public float getFastestStrength() {
        return fastestStrength;
    }

    public void setFastestStrength(float fastestStrength) {
        this.fastestStrength = fastestStrength;
        setChanged();
    }

    public float getSlowestStrength() {
        return slowestStrength;
    }

    public void setSlowestStrength(float slowestStrength) {
        this.slowestStrength = slowestStrength;
        setChanged();
    }

    public int getEnemyTeamNum() {
        return enemyTeamNum;
    }

    public void setEnemyTeamNum(int enemyTeamNum) {
        this.enemyTeamNum = enemyTeamNum;
        setChanged();
    }

    public int getMaxGolem() {
        return maxGolem;
    }

    public void setMaxGolem(int maxGolem) {
        this.maxGolem = maxGolem;
        setChanged();
    }

    public float getCurrentStrength() {
        return currentStrength;
    }
}
