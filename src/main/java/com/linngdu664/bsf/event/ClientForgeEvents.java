package com.linngdu664.bsf.event;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.client.screenshake.ScreenshakeHandler;
import com.linngdu664.bsf.item.tool.ColdCompressionJetEngineItem;
import com.linngdu664.bsf.item.tool.TeamLinkerItem;
import com.linngdu664.bsf.item.weapon.AbstractBSFWeaponItem;
import com.linngdu664.bsf.item.weapon.SnowballCannonItem;
import com.linngdu664.bsf.network.to_server.SculkSnowballLauncherSwitchSoundPayload;
import com.linngdu664.bsf.network.to_server.SwitchTweakerStatusModePayload;
import com.linngdu664.bsf.network.to_server.SwitchTweakerTargetModePayload;
import com.linngdu664.bsf.registry.ItemRegister;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashSet;


@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientForgeEvents {
    public static final RandomSource BSF_RANDOM_SOURCE = RandomSource.create();

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.is(ItemRegister.SCULK_SNOWBALL_LAUNCHER.get()) && player.isShiftKeyDown()) {
            PacketDistributor.sendToServer(new SculkSnowballLauncherSwitchSoundPayload(event.getScrollDeltaY() > 0));
            event.setCanceled(true);
        } else if (itemStack.is(ItemRegister.SNOW_GOLEM_MODE_TWEAKER.get()) && minecraft.options.keyShift.isDown()) {
            PacketDistributor.sendToServer(new SwitchTweakerTargetModePayload(event.getScrollDeltaY() < 0));
            event.setCanceled(true);
        } else if (itemStack.is(ItemRegister.SNOW_GOLEM_MODE_TWEAKER.get()) && minecraft.options.keySprint.isDown()) {
            PacketDistributor.sendToServer(new SwitchTweakerStatusModePayload(event.getScrollDeltaY() < 0));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        int key = event.getKey();
        if (minecraft.screen == null && (key == ClientModEvents.CYCLE_MOVE_AMMO_NEXT.getKey().getValue() || key == ClientModEvents.CYCLE_MOVE_AMMO_PREV.getKey().getValue()) && event.getAction() == GLFW.GLFW_PRESS) {
            Player player = minecraft.player;
            AbstractBSFWeaponItem weaponItem = null;
            if (player.getMainHandItem().getItem() instanceof AbstractBSFWeaponItem item) {
                weaponItem = item;
            } else if (player.getOffhandItem().getItem() instanceof AbstractBSFWeaponItem item) {
                weaponItem = item;
            }
            if (weaponItem != null) {
                LinkedHashSet<Item> launchOrder = weaponItem.getLaunchOrder();
                if (!launchOrder.isEmpty()) {
                    if (key == ClientModEvents.CYCLE_MOVE_AMMO_NEXT.getKey().getValue()) {
                        Item item = launchOrder.getFirst();
                        launchOrder.removeFirst();
                        launchOrder.addLast(item);
                    } else {
                        Item item = launchOrder.getLast();
                        launchOrder.removeLast();
                        launchOrder.addFirst(item);
                    }
                    player.playSound(SoundEvents.DISPENSER_DISPENSE, 1.0F, 1.0F / (player.level().getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        TeamLinkerItem.shouldShowHighlight = false;
    }

    @SubscribeEvent
    public static void onComputeFovModifier(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getUseItem();
        if (player.isUsingItem()){
            if (itemStack.getItem() instanceof SnowballCannonItem) {
                int i = player.getTicksUsingItem();
                float f = event.getFovModifier();
                float f1 = (float) i / 20.0F;
                if (f1 > 1.0F) {
                    f1 = 1.0F;
                } else {
                    f1 *= f1;
                }
                if (itemStack.is(ItemRegister.POWERFUL_SNOWBALL_CANNON.get())) {
                    f *= 1.0F - f1 * 0.5F;
                } else {
                    f *= 1.0F - f1 * 0.3F;
                }
                event.setNewFovModifier(f);
            } else if (itemStack.getItem() instanceof ColdCompressionJetEngineItem) {
                float f = event.getFovModifier();
                if (player.getTicksUsingItem() >= ColdCompressionJetEngineItem.STARTUP_DURATION) {
                    f *= 1.4F;
                }
                event.setNewFovModifier(f);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTickPost(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.isPaused()) {
            return;
        }
        Camera camera = minecraft.gameRenderer.getMainCamera();
        ScreenshakeHandler.clientTick(camera, null);
        ScreenshakeHandler.clientTick(camera, BSF_RANDOM_SOURCE);
    }
}
