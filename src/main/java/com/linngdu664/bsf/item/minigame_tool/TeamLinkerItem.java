package com.linngdu664.bsf.item.minigame_tool;

import com.linngdu664.bsf.misc.BSFTeamSavedData;
import com.linngdu664.bsf.network.to_client.CurrentTeamPayload;
import com.linngdu664.bsf.network.to_client.TeamMembersPayload;
import com.linngdu664.bsf.registry.ItemRegister;
import com.linngdu664.bsf.util.BSFColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class TeamLinkerItem extends Item {
    public static boolean shouldShowHighlight = false;  // client only
    private final int teamId;

    public TeamLinkerItem(int teamId) {
        super(new Properties());
        this.teamId = teamId;
    }

    public static ItemStack getItemStackById(byte id) {
        return switch (id) {
            case 0 -> ItemRegister.WHITE_TEAM_LINKER.toStack();
            case 1 -> ItemRegister.ORANGE_TEAM_LINKER.toStack();
            case 2 -> ItemRegister.MAGENTA_TEAM_LINKER.toStack();
            case 3 -> ItemRegister.LIGHT_BLUE_TEAM_LINKER.toStack();
            case 4 -> ItemRegister.YELLOW_TEAM_LINKER.toStack();
            case 5 -> ItemRegister.LIME_TEAM_LINKER.toStack();
            case 6 -> ItemRegister.PINK_TEAM_LINKER.toStack();
            case 7 -> ItemRegister.GRAY_TEAM_LINKER.toStack();
            case 8 -> ItemRegister.LIGHT_GRAY_TEAM_LINKER.toStack();
            case 9 -> ItemRegister.CYAN_TEAM_LINKER.toStack();
            case 10 -> ItemRegister.PURPLE_TEAM_LINKER.toStack();
            case 11 -> ItemRegister.BLUE_TEAM_LINKER.toStack();
            case 12 -> ItemRegister.BROWN_TEAM_LINKER.toStack();
            case 13 -> ItemRegister.GREEN_TEAM_LINKER.toStack();
            case 14 -> ItemRegister.RED_TEAM_LINKER.toStack();
            default -> ItemRegister.BLACK_TEAM_LINKER.toStack();
        };
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer.isShiftKeyDown()) {
            if (pLevel.isClientSide) {
                shouldShowHighlight = !shouldShowHighlight;
            }
        } else if (!pLevel.isClientSide) {
            BSFTeamSavedData savedData = pPlayer.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
            Component playerName = pPlayer.getName();
            UUID uuid = pPlayer.getUUID();
            int oldId = savedData.getTeam(uuid);
            Object[] oldNameParam = new Object[]{playerName, BSFColorUtil.getColorTransNameById(oldId)};
            Object[] newNameParam = new Object[]{playerName, BSFColorUtil.getColorTransNameById(teamId)};
            HashSet<UUID> oldMembers = savedData.getMembers(oldId);
            for (UUID uuid1 : oldMembers) {
                ServerPlayer serverPlayer = (ServerPlayer) pLevel.getPlayerByUUID(uuid1);
                if (serverPlayer != null) {
                    serverPlayer.displayClientMessage(MutableComponent.create(new TranslatableContents("leave_bsf_team.tip", null, oldNameParam)), false);
                }
            }
            if (oldId == teamId) {
                // 退队
                savedData.exitTeam(uuid);       // 此时oldMembers已经不含自己了
                for (UUID uuid1 : oldMembers) {
                    ServerPlayer serverPlayer = (ServerPlayer) pLevel.getPlayerByUUID(uuid1);
                    if (serverPlayer != null) {
                        PacketDistributor.sendToPlayer(serverPlayer, new TeamMembersPayload(oldMembers));
                    }
                }
                PacketDistributor.sendToPlayer((ServerPlayer) pPlayer, new TeamMembersPayload(new HashSet<>()));
                PacketDistributor.sendToPlayer((ServerPlayer) pPlayer, new CurrentTeamPayload((byte) -1));
            } else {
                // 退队后进队
                savedData.joinTeam(uuid, teamId);
                for (UUID uuid1 : oldMembers) {
                    ServerPlayer serverPlayer = (ServerPlayer) pLevel.getPlayerByUUID(uuid1);
                    if (serverPlayer != null) {
                        PacketDistributor.sendToPlayer(serverPlayer, new TeamMembersPayload(oldMembers));
                    }
                }
                HashSet<UUID> newMembers = savedData.getMembers(teamId);
                for (UUID uuid1 : newMembers) {
                    ServerPlayer serverPlayer = (ServerPlayer) pLevel.getPlayerByUUID(uuid1);
                    if (serverPlayer != null) {
                        serverPlayer.displayClientMessage(MutableComponent.create(new TranslatableContents("join_bsf_team.tip", null, newNameParam)), false);
                        PacketDistributor.sendToPlayer(serverPlayer, new TeamMembersPayload(newMembers));
                    }
                }
                PacketDistributor.sendToPlayer((ServerPlayer) pPlayer, new CurrentTeamPayload((byte) teamId));
            }
            savedData.setDirty();
        }
        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.success(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Options options = Minecraft.getInstance().options;
        tooltipComponents.add(Component.translatable("team_linker.tooltip", options.keyUse.getTranslatedKeyMessage()).withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(Component.translatable("team_linker1.tooltip", options.keyShift.getTranslatedKeyMessage(), options.keyUse.getTranslatedKeyMessage()).withStyle(ChatFormatting.DARK_GRAY));
    }

    public byte getTeamId() {
        return (byte) teamId;
    }

}
