package com.linngdu664.bsf;

import com.linngdu664.bsf.config.ClientConfig;
import com.linngdu664.bsf.config.ServerConfig;
import com.linngdu664.bsf.registry.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import java.util.Objects;
import java.util.function.Supplier;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "bsf";
    private static final ThreadLocal<ResourceKey<Item>> CURRENT_ITEM_ID = new ThreadLocal<>();

    public Main(IEventBus modEventBus, ModContainer modContainer) {
        BlockRegister.BLOCKS.register(modEventBus);
        BlockEntityRegister.BLOCK_ENTITIES.register(modEventBus);
        DataComponentRegister.DATA_COMPONENTS.register(modEventBus);
        ItemRegister.ITEMS.register(modEventBus);
        SoundRegister.SOUNDS.register(modEventBus);
        ParticleRegister.PARTICLES.register(modEventBus);
        EffectRegister.EFFECTS.register(modEventBus);
        EntityRegister.ENTITY_TYPES.register(modEventBus);
        CreativeTabRegister.CREATIVE_TABS.register(modEventBus);
        TriggerTypeRegister.TRIGGER_TYPES.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    public static Identifier makeResLoc(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    public static BlockBehaviour.Properties blockProperties(String path, BlockBehaviour.Properties properties) {
        return properties.setId(ResourceKey.create(Registries.BLOCK, makeResLoc(path)));
    }

    public static <I extends Item> I withItemId(Identifier id, Supplier<? extends I> supplier) {
        CURRENT_ITEM_ID.set(ResourceKey.create(Registries.ITEM, id));
        try {
            return supplier.get();
        } finally {
            CURRENT_ITEM_ID.remove();
        }
    }

    public static Item.Properties itemProperties() {
        return new Item.Properties().setId(Objects.requireNonNull(CURRENT_ITEM_ID.get(), "Item id not set"));
    }
}
