package com.linngdu664.bsf.misc;

import com.linngdu664.bsf.Main;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class BSFTeamSavedData extends SavedData {
    public static final Codec<BSFTeamSavedData> CODEC = CompoundTag.CODEC.xmap(BSFTeamSavedData::new, BSFTeamSavedData::saveToTag);
    public static final SavedDataType<BSFTeamSavedData> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath(Main.MODID, "bsf_team"), BSFTeamSavedData::new, CODEC);

    private final HashSet<UUID>[] groupMembers = new HashSet[16];
    private final HashMap<UUID, Integer> groupIdMap = new HashMap<>();

    public BSFTeamSavedData() {
        for (int i = 0; i < 16; i++) {
            groupMembers[i] = new HashSet<>();
        }
    }

    public BSFTeamSavedData(CompoundTag root, HolderLookup.Provider lookupProvider) {
        this(root);
    }

    public BSFTeamSavedData(CompoundTag root) {
        this();
        if (root.get("BSFTeam") instanceof ListTag listTag) {
            for (Tag tag : listTag) {
                CompoundTag current = (CompoundTag) tag;
                current.read("UUID", UUIDUtil.CODEC).ifPresent(uuid -> {
                    int groupId = current.getIntOr("TeamId", -1);
                    if (groupId >= 0 && groupId < groupMembers.length) {
                        groupMembers[groupId].add(uuid);
                        groupIdMap.put(uuid, groupId);
                    }
                });
            }
        }
    }

    public @NotNull CompoundTag save(@NotNull CompoundTag pCompoundTag, HolderLookup.@NotNull Provider provider) {
        pCompoundTag.merge(saveToTag());
        return pCompoundTag;
    }

    private CompoundTag saveToTag() {
        CompoundTag root = new CompoundTag();
        ListTag listTag = new ListTag();
        for (var e : groupIdMap.entrySet()) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.store("UUID", UUIDUtil.CODEC, e.getKey());
            compoundTag.putInt("TeamId", e.getValue());
            listTag.add(compoundTag);
        }
        root.put("BSFTeam", listTag);
        return root;
    }

    public int getTeam(UUID uuid) {
        if (groupIdMap.containsKey(uuid)) {
            return groupIdMap.get(uuid);
        }
        return -1;
    }

    public void exitTeam(UUID uuid) {
        int groupId = getTeam(uuid);
        if (groupId != -1) {
            groupIdMap.remove(uuid);
            groupMembers[groupId].remove(uuid);
            setDirty();
        }
    }

    public void joinTeam(@NotNull UUID uuid, int groupId) {
        int oldGroupId = getTeam(uuid);
        if (oldGroupId != -1) {
            groupMembers[oldGroupId].remove(uuid);
        }
        groupIdMap.put(uuid, groupId);
        groupMembers[groupId].add(uuid);
        setDirty();
    }

    public HashSet<UUID> getMembers(int groupId) {
        if (groupId < 0) {
            return new HashSet<>();
        }
        return groupMembers[groupId];
    }

    public boolean isSameTeam(@Nullable Entity entity1, @Nullable Entity entity2) {
        if (entity1 == null || entity2 == null) {
            return false;
        }
        int id = getTeam(entity1.getUUID());
        return id >= 0 && id == getTeam(entity2.getUUID());
    }
}
