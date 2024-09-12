package com.linngdu664.bsf.gui;

import com.linngdu664.bsf.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class RenderGuiEventHandler {
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft instance = Minecraft.getInstance();
        if (instance.options.hideGui) {
            return;
        }
        Map<String, Object> varMap = new HashMap<>();
        Player player = instance.player;
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offHandItem = player.getOffhandItem();
        HitResult pick = instance.hitResult;
        HitResult.Type pickType = pick.getType();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        GuiGraphics guiGraphics = event.getGuiGraphics();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0F, 0F, 4932F);        // 显示在原版gui的上方

        //gui队列
        GuiHandler.itemInHandBSFWeapon(guiGraphics, mainHandItem, offHandItem);
        if (pickType == HitResult.Type.ENTITY) {
            Entity entity1 = ((EntityHitResult) pick).getEntity();
            GuiHandler.pickEntityBSFSnowGolem(guiGraphics, entity1, partialTick, varMap);
            GuiHandler.pickEntityBSFDummy(guiGraphics, entity1);
        } else if (pickType == HitResult.Type.BLOCK) {
            BlockEntity blockEntity = player.level().getBlockEntity(((BlockHitResult) pick).getBlockPos());
            GuiHandler.pickBlockEntityVendingMachine(guiGraphics, blockEntity, mainHandItem, partialTick);
            GuiHandler.pickBlockEntityZoneController(guiGraphics, blockEntity, partialTick);
        }
        GuiHandler.itemInHandSnowGolemModeTweaker(guiGraphics, mainHandItem, offHandItem, varMap);
        GuiHandler.specialModelText(guiGraphics, varMap);


        guiGraphics.pose().popPose();
    }
}
