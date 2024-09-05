package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.item.component.ItemData;
import com.linngdu664.bsf.item.component.UuidData;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DataComponentRegister {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Main.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemData>> SNOWBALL_TANK_TYPE =
            DATA_COMPONENTS.registerComponentType(
                    "snowball_tank_type",
                    builder -> builder.persistent(ItemData.CODEC).networkSynchronized(ItemData.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemData>> AMMO_ITEM =
            DATA_COMPONENTS.registerComponentType(
                    "ammo_item",
                    builder -> builder.persistent(ItemData.CODEC).networkSynchronized(ItemData.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SCULK_SOUND_ID =
            DATA_COMPONENTS.registerComponentType(
                    "sculk_sound_id",
                    builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.VAR_INT)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> TWEAKER_STATUS_MODE =
            DATA_COMPONENTS.registerComponentType(
                    "tweaker_status_mode",
                    builder -> builder.persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> TWEAKER_TARGET_MODE =
            DATA_COMPONENTS.registerComponentType(
                    "tweaker_target_mode",
                    builder -> builder.persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> BASIN_SNOW_TYPE =
            DATA_COMPONENTS.registerComponentType(
                    "basin_snow_type",
                    builder -> builder.persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UuidData>> TARGET_UUID =
            DATA_COMPONENTS.registerComponentType(
                    "target_uuid",
                    builder -> builder.persistent(UuidData.CODEC).networkSynchronized(UuidData.STREAM_CODEC)
            );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> SNOW_GOLEM_DATA =
            DATA_COMPONENTS.registerComponentType(
                    "snow_golem_data",
                    builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
            );

}
