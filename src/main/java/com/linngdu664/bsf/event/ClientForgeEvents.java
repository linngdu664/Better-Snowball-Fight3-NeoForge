package com.linngdu664.bsf.event;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.client.screenshake.ScreenshakeHandler;
import com.linngdu664.bsf.item.tool.ColdCompressionJetEngineItem;
import com.linngdu664.bsf.item.tool.SnowGolemModeTweakerItem;
import com.linngdu664.bsf.item.tool.TeamLinkerItem;
import com.linngdu664.bsf.item.weapon.AbstractBSFWeaponItem;
import com.linngdu664.bsf.item.weapon.SnowballCannonItem;
import com.linngdu664.bsf.network.to_server.SculkSnowballLauncherSwitchSoundPayload;
import com.linngdu664.bsf.network.to_server.SwitchTweakerStatusModePayload;
import com.linngdu664.bsf.network.to_server.SwitchTweakerTargetModePayload;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;

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
                LinkedList<Item> launchOrder = weaponItem.getLaunchOrder();
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
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) {
            return;
        }
        Player player = mc.player;
        AbstractBSFWeaponItem weaponItem = null;
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offHandItem = player.getOffhandItem();
        if (mainHandItem.getItem() instanceof AbstractBSFWeaponItem item) {
            weaponItem = item;
        } else if (offHandItem.getItem() instanceof AbstractBSFWeaponItem item) {
            weaponItem = item;
        }
        GuiGraphics guiGraphics = event.getGuiGraphics();
        Window window = mc.getWindow();
        if (weaponItem != null) {
            ItemStack current = weaponItem.getCurrentAmmoItemStack();
            ItemStack prev = weaponItem.getPrevAmmoItemStack();
            ItemStack next = weaponItem.getNextAmmoItemStack();
            BSFGui.V2I v2I = BSFGui.SNOWBALL_GUI.renderCenterVertically(guiGraphics, window, 0);
            int startPos = v2I.y;
            guiGraphics.renderItem(prev, 3, startPos + 3);
            guiGraphics.renderItem(current, 3, startPos + 23);
            guiGraphics.renderItem(next, 3, startPos + 43);
            guiGraphics.drawString(mc.font, String.valueOf(prev.getCount()), 24, startPos + 7, 0xffffffff);
            guiGraphics.drawString(mc.font, String.valueOf(current.getCount()), 24, startPos + 27, 0xffffffff);
            guiGraphics.drawString(mc.font, String.valueOf(next.getCount()), 24, startPos + 47, 0xffffffff);
        }
        HitResult pick = mc.hitResult;
        BSFGui.V2I locateV2I = null,statusV2I = null;
        if (pick.getType() == HitResult.Type.ENTITY && ((EntityHitResult) pick).getEntity() instanceof BSFSnowGolemEntity entity && player.equals(entity.getOwner())) {
            byte locator = entity.getLocator();
            byte status = entity.getStatus();
            locateV2I = BSFGui.GOLEM_LOCATOR_GUI.renderRatio(guiGraphics, window, 0.7, 0.5);
            locateV2I.set(locateV2I.x-1,locateV2I.y-1+locator*20);
            BSFGui.GOLEM_SELECTOR_GUI.render(guiGraphics, locateV2I.x, locateV2I.y);
            statusV2I = BSFGui.GOLEM_STATUS_GUI.renderRatio(guiGraphics,window,0.7,0.5,60,0);
            statusV2I.set(statusV2I.x-1,statusV2I.y-1+status*20);
            BSFGui.GOLEM_SELECTOR_GUI.render(guiGraphics, statusV2I.x, statusV2I.y);
//                BSFGui.renderLineTool(guiGraphics,50,50,100,100,0xffffffff);
        }
        ItemStack tweaker = null;
        if (mainHandItem.getItem() instanceof SnowGolemModeTweakerItem) {
            tweaker = mainHandItem;
        } else if (offHandItem.getItem() instanceof SnowGolemModeTweakerItem) {
            tweaker = offHandItem;
        }
        if (tweaker != null) {
            byte status = tweaker.getOrDefault(DataComponentRegister.TWEAKER_STATUS_MODE, (byte) 0);
            byte target = tweaker.getOrDefault(DataComponentRegister.TWEAKER_TARGET_MODE, (byte) 0);
            BSFGui.V2I locateV2IT = BSFGui.TWEAKER_LOCATOR_GUI.renderRatio(guiGraphics, window, 0.7, 0.5,30,0);
            locateV2IT.set(locateV2IT.x-1, locateV2IT.y-1+target*20);
            BSFGui.TWEAKER_SELECTOR_GUI.render(guiGraphics, locateV2IT.x, locateV2IT.y);
            BSFGui.V2I statusV2IT = BSFGui.TWEAKER_STATUS_GUI.renderRatio(guiGraphics,window,0.7,0.5,90,0);
            statusV2IT.set(statusV2IT.x-1, statusV2IT.y-1+status*20);
            BSFGui.TWEAKER_SELECTOR_GUI.render(guiGraphics, statusV2IT.x, statusV2IT.y);
            if (locateV2I!=null &&  locateV2I.y!=locateV2IT.y){
                BSFGui.SETTER_ARROW.render(guiGraphics, locateV2I.x+23, locateV2IT.y+2);
            }
            if (statusV2I!=null &&  statusV2I.y!=statusV2IT.y){
                BSFGui.SETTER_ARROW.render(guiGraphics, statusV2I.x+23, statusV2IT.y+2);
            }
        }
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            if (minecraft.isPaused()) {
                return;
            }
            Camera camera = minecraft.gameRenderer.getMainCamera();
            ScreenshakeHandler.clientTick(camera, null);
            ScreenshakeHandler.clientTick(camera, BSF_RANDOM_SOURCE);
        }
    }
}
