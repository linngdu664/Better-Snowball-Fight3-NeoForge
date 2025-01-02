package com.linngdu664.bsf.block.entity;

import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import com.linngdu664.bsf.registry.BlockEntityRegister;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RegionPlayerInspectorBlockEntity extends BlockEntity {
    private RegionData region = RegionData.EMPTY;
    private BlockPos kickPos = BlockPos.ZERO;
    private HashSet<Item> clearDirectlyItems = new HashSet<>();
    private short permittedTeams;
    private boolean checkItem;
    private boolean checkTeam;

    public RegionPlayerInspectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegister.REGION_PLAYER_INSPECTOR.get(), pos, blockState);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (level.isClientSide || !(blockEntity instanceof RegionPlayerInspectorBlockEntity be) || (!be.checkTeam && !be.checkItem)) {
            return;
        }
        List<? extends Player> playerList = level.players();
        ArrayList<Player> filteredList = new ArrayList<>();
        for (Player player : playerList) {
            if (be.region.inRegion(player.position()) && !player.isCreative() && !player.isSpectator()) {
                filteredList.add(player);
            }
        }
        if (be.checkTeam) {
            BSFTeamSavedData savedData = level.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
            for (Player player : filteredList) {
                int playerTeamId = savedData.getTeam(player.getUUID());
                if (playerTeamId < 0 || (be.permittedTeams & (1 << playerTeamId)) == 0) {
                    // 没加队伍或队伍不对的，传送走
                    player.teleportTo(be.kickPos.getX() + 0.5, be.kickPos.getY() + 1.0, be.kickPos.getZ() + 0.5);
                    player.displayClientMessage(Component.translatable("region_player_inspector_team_kick.tip").withStyle(ChatFormatting.RED), false);
                }
            }
        }
        if (be.checkItem) {
            for (Player player : filteredList) {
                loop:
                for (NonNullList<ItemStack> inv : BSFCommonUtil.getPlayerInventoryList(player)) {
                    for (ItemStack itemStack : inv) {
                        if (itemStack.isEmpty() || be.region.equals(itemStack.get(DataComponentRegister.REGION))) {
                            continue;
                        }
                        if (be.clearDirectlyItems.contains(itemStack.getItem())) {
                            // 在集合里的物品直接清掉
                            itemStack.setCount(0);
                        } else {
                            // 如果有区域不符的物品，传送走
                            player.teleportTo(be.kickPos.getX() + 0.5, be.kickPos.getY() + 1.0, be.kickPos.getZ() + 0.5);
                            player.displayClientMessage(Component.translatable("region_player_inspector_item_kick.tip").withStyle(ChatFormatting.RED), false);
                            break loop;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        region = RegionData.loadFromCompoundTag("Region", tag);
        if (region == null) {
            region = RegionData.EMPTY;
        }
        kickPos = BlockPos.of(tag.getLong("KickPos"));
        permittedTeams = tag.getShort("PermittedTeams");
        checkItem = tag.getBoolean("CheckItem");
        checkTeam = tag.getBoolean("CheckTeam");
        clearDirectlyItems = new HashSet<>();
        ListTag listTag = tag.getList("DirectClearItems", 8);     // type 8 string tag. see mc wiki.
        for (int i = 0, size = listTag.size(); i < size; i++) {
            clearDirectlyItems.add(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(listTag.getString(i))));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        region.saveToCompoundTag("Region", tag);
        tag.putLong("KickPos", kickPos.asLong());
        tag.putShort("PermittedTeams", permittedTeams);
        tag.putBoolean("CheckItem", checkItem);
        tag.putBoolean("CheckTeam", checkTeam);
        ListTag listTag = new ListTag();
        int i = 0;
        for (Item item : clearDirectlyItems) {
            listTag.addTag(i, StringTag.valueOf(BuiltInRegistries.ITEM.getKey(item).toString()));
            i++;
        }
        tag.put("DirectClearItems", listTag);
    }

    public RegionData getRegion() {
        return region;
    }

    public void setRegion(RegionData region) {
        this.region = region;
    }

    public BlockPos getKickPos() {
        return kickPos;
    }

    public void setKickPos(BlockPos kickPos) {
        this.kickPos = kickPos;
    }

    public short getPermittedTeams() {
        return permittedTeams;
    }

    public void setPermittedTeams(short permittedTeams) {
        this.permittedTeams = permittedTeams;
    }

    public boolean isCheckItem() {
        return checkItem;
    }

    public void setCheckItem(boolean checkItem) {
        this.checkItem = checkItem;
    }

    public boolean isCheckTeam() {
        return checkTeam;
    }

    public void setCheckTeam(boolean checkTeam) {
        this.checkTeam = checkTeam;
    }

    public List<String> getClearDirectlyItems() {
        ArrayList<String> list = new ArrayList<>();
        for (Item item : clearDirectlyItems) {
            list.add(BuiltInRegistries.ITEM.getKey(item).toString());
        }
        return list;
    }

    public void setClearDirectlyItems(List<String> directClearItems) {
        clearDirectlyItems = new HashSet<>();
        for (String str : directClearItems) {
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(str));
            if (!item.equals(Items.AIR)) {
                clearDirectlyItems.add(item);
            }
        }
    }
}
