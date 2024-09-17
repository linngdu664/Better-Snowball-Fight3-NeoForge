package com.linngdu664.bsf.mixin;

import com.linngdu664.bsf.item.tool.TeamLinkerItem;
import com.linngdu664.bsf.network.to_client.TeamMembersPayload;
import com.linngdu664.bsf.registry.EntityRegister;
import com.mojang.blaze3d.platform.WindowEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.extensions.IMinecraftExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler, IMinecraftExtension {
    public MinecraftMixin(String pName) {
        super(pName);
    }

    @Inject(method = "shouldEntityAppearGlowing", at = @At(value = "HEAD"), cancellable = true)
    private void shouldEntityAppearGlowing(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        if (TeamLinkerItem.shouldShowHighlight) {
            if (TeamMembersPayload.isFriendly(pEntity)) {
                cir.setReturnValue(true);
            }
        }
        if (pEntity.getType().equals(EntityRegister.BLACK_HOLE_EXECUTOR.get())) {
            cir.setReturnValue(true);       // some TeaCon mod screwed up glowing and thus force our black hole glowing here
        }
    }
}
