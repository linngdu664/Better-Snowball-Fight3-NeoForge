package com.linngdu664.bsf.network.to_client.handler;

import com.linngdu664.bsf.client.resources.sounds.MovingSoundInstance;
import com.linngdu664.bsf.network.to_client.ToggleMovingSoundPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ToggleMovingSoundClientHandler {
    public static void handlePayload(ToggleMovingSoundPayload payload) {
        Level level = Minecraft.getInstance().level;
        Entity entity = level.getEntity(payload.entityId());
        if (entity != null) {
            SoundManager soundManager = Minecraft.getInstance().getSoundManager();
            if (payload.flag() == ToggleMovingSoundPayload.PLAY_LOOP) {
                MovingSoundInstance soundInstance = new MovingSoundInstance(entity, payload.soundEvent(), true);
                if (!soundManager.isActive(soundInstance)) {
                    soundManager.queueTickingSound(soundInstance);
                }
            } else if (payload.flag() == ToggleMovingSoundPayload.STOP_LOOP) {
                MovingSoundInstance soundInstance = new MovingSoundInstance(entity, payload.soundEvent(), true);
                soundManager.stop(soundInstance);
            } else {
                MovingSoundInstance soundInstance = new MovingSoundInstance(entity, payload.soundEvent(), false);
                soundManager.queueTickingSound(soundInstance);
            }
        }
    }
}
