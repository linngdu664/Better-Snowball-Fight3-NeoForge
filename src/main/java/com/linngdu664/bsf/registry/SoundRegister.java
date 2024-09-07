package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundRegister {
    public static final int MEME_SOUND_AMOUNT = 64;

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, Main.MODID);
    public static final DeferredHolder<SoundEvent, SoundEvent> SNOWBALL_CANNON_SHOOT = build("snowball_cannon_shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> SNOWBALL_MACHINE_GUN_SHOOT = build("snowball_machine_gun_shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHOTGUN_FIRE_1 = build("shotgun_fire1");
    public static final DeferredHolder<SoundEvent, SoundEvent> SHOTGUN_FIRE_2 = build("shotgun_fire2");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLACK_HOLE_START = build("black_hole_start", 32);
    public static final DeferredHolder<SoundEvent, SoundEvent> POWDER_SNOWBALL = build("powder_snowball");
    public static final DeferredHolder<SoundEvent, SoundEvent> FIELD_PUSH = build("field_push");
    public static final DeferredHolder<SoundEvent, SoundEvent> FIELD_SNOWBALL_STOP = build("field_snowball_stop");
    public static final DeferredHolder<SoundEvent, SoundEvent> FIELD_START = build("field_start");
    public static final DeferredHolder<SoundEvent, SoundEvent> SUBSPACE_SNOWBALL_CUT = build("subspace_snowball_cut");
    public static final DeferredHolder<SoundEvent, SoundEvent> UNSTABLE_CORE_BREAK = build("unstable_core_break");
    public static final DeferredHolder<SoundEvent, SoundEvent> MACHINE_GUN_COOLING = build("machine_gun_cooling");
    public static final DeferredHolder<SoundEvent, SoundEvent>[] MEME = buildMeme();
    public static final DeferredHolder<SoundEvent, SoundEvent> DUCK = build("duck_sound");
    public static final DeferredHolder<SoundEvent, SoundEvent> FREEZING = build("freezing");
    public static final DeferredHolder<SoundEvent, SoundEvent> FORCE_EXECUTOR_START = build("force_executor_start");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLACK_HOLE_AMBIENCE = build("black_hole_ambience");
    public static final DeferredHolder<SoundEvent, SoundEvent> COLD_COMPRESSION_JET_ENGINE_STARTUP1 = build("cold_compression_jet_engine_startup1");
    public static final DeferredHolder<SoundEvent, SoundEvent> COLD_COMPRESSION_JET_ENGINE_STARTUP2 = build("cold_compression_jet_engine_startup2");
    public static final DeferredHolder<SoundEvent, SoundEvent> COLD_COMPRESSION_JET_ENGINE_STARTUP3 = build("cold_compression_jet_engine_startup3");
    public static final DeferredHolder<SoundEvent, SoundEvent> COLD_COMPRESSION_JET_ENGINE_STARTUP4 = build("cold_compression_jet_engine_startup4");
    public static final DeferredHolder<SoundEvent, SoundEvent> COLD_COMPRESSION_JET_ENGINE_STARTUP5 = build("cold_compression_jet_engine_startup5");
    public static final DeferredHolder<SoundEvent, SoundEvent> VECTOR_INVERSION = build("vector_inversion");
    public static final DeferredHolder<SoundEvent, SoundEvent> SUBSPACE_SNOWBALL_ATTACK = build("subspace_snowball_attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> IMPLOSION_SNOWBALL_CANNON = build("implosion_snowball_cannon", 24);

    private static DeferredHolder<SoundEvent, SoundEvent> build(String id, float range) {
        return SOUNDS.register(id, () -> SoundEvent.createFixedRangeEvent(Main.makeResLoc(id), range));
    }

    private static DeferredHolder<SoundEvent, SoundEvent> build(String id) {
        return SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(Main.makeResLoc(id)));
    }

    private static DeferredHolder<SoundEvent, SoundEvent>[] buildMeme() {
        DeferredHolder<SoundEvent, SoundEvent>[] memes = new DeferredHolder[MEME_SOUND_AMOUNT];
        for (int i = 0; i < 64; i++) {
            memes[i] = build(String.format("memesound%02d", i));
        }
        return memes;
    }
}
