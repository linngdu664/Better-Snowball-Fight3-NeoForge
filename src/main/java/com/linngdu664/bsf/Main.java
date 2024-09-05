package com.linngdu664.bsf;

import com.linngdu664.bsf.config.ClientConfig;
import com.linngdu664.bsf.config.ServerConfig;
import com.linngdu664.bsf.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Main.MODID)
public class Main {
    public static final String MODID = "bsf";

    public static ResourceLocation makeResLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public Main(IEventBus modEventBus, ModContainer modContainer) {
        BlockRegister.BLOCKS.register(modEventBus);
        BlockEntityRegister.BLOCK_ENTITIES.register(modEventBus);
        ItemRegister.ITEMS.register(modEventBus);
        SoundRegister.SOUNDS.register(modEventBus);
        ParticleRegister.PARTICLES.register(modEventBus);
        EffectRegister.EFFECTS.register(modEventBus);
        EntityRegister.ENTITY_TYPES.register(modEventBus);
        CreativeTabRegister.CREATIVE_TABS.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}
