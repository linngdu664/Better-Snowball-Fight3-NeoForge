package com.linngdu664.bsf.block.entity;

import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import com.linngdu664.bsf.registry.BlockEntityRegister;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
        if (level.isClientSide() || !(blockEntity instanceof RegionPlayerInspectorBlockEntity be) || (!be.checkTeam && !be.checkItem)) {
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
            BSFTeamSavedData savedData = level.getServer().overworld().getDataStorage().computeIfAbsent(BSFTeamSavedData.TYPE);
            for (Player player : filteredList) {
                int playerTeamId = savedData.getTeam(player.getUUID());
                if (playerTeamId < 0 || (be.permittedTeams & (1 << playerTeamId)) == 0) {
                    // 没加队伍或队伍不对的，传送走
                    player.teleportTo(be.kickPos.getX() + 0.5, be.kickPos.getY() + 1.0, be.kickPos.getZ() + 0.5);
                    player.sendSystemMessage(Component.translatable("region_player_inspector_team_kick.tip").withStyle(ChatFormatting.RED));
                }
            }
        }
        if (be.checkItem) {
            for (Player player : filteredList) {
                for (ItemStack itemStack : BSFCommonUtil.getPlayerInventoryList(player)) {
                    if (itemStack.isEmpty() || be.region.equals(itemStack.get(DataComponentRegister.REGION))) {
                        continue;
                    }
                    if (be.clearDirectlyItems.contains(itemStack.getItem())) {
                        itemStack.setCount(0);
                    } else {
                        // 濡傛灉鏈夊尯鍩熶笉绗︾殑鐗╁搧锛屼紶閫佽蛋
                        player.teleportTo(be.kickPos.getX() + 0.5, be.kickPos.getY() + 1.0, be.kickPos.getZ() + 0.5);
                        player.sendSystemMessage(Component.translatable("region_player_inspector_item_kick.tip").withStyle(ChatFormatting.RED));
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        region = RegionData.loadFromValueInput("Region", input);
        if (region == null) {
            region = RegionData.EMPTY;
        }
        kickPos = BlockPos.of(input.getLongOr("KickPos", 0));
        permittedTeams = (short) input.getShortOr("PermittedTeams", (short) 0);
        checkItem = input.getBooleanOr("CheckItem", false);
        checkTeam = input.getBooleanOr("CheckTeam", false);
        clearDirectlyItems = new HashSet<>();
        for (String itemId : input.listOrEmpty("DirectClearItems", Codec.STRING)) {
            clearDirectlyItems.add(BuiltInRegistries.ITEM.getValue(Identifier.tryParse(itemId)));
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        region.saveToValueOutput("Region", output);
        output.putLong("KickPos", kickPos.asLong());
        output.putShort("PermittedTeams", permittedTeams);
        output.putBoolean("CheckItem", checkItem);
        output.putBoolean("CheckTeam", checkTeam);
        ValueOutput.TypedOutputList<String> listTag = output.list("DirectClearItems", Codec.STRING);
        for (Item item : clearDirectlyItems) {
            listTag.add(BuiltInRegistries.ITEM.getKey(item).toString());
        }
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
            Item item = BuiltInRegistries.ITEM.getValue(Identifier.tryParse(str));
            if (!item.equals(Items.AIR)) {
                clearDirectlyItems.add(item);
            }
        }
    }
}
