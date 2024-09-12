package com.linngdu664.bsf.gui;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.block.entity.VendingMachineEntity;
import com.linngdu664.bsf.block.entity.ZoneControllerEntity;
import com.linngdu664.bsf.entity.BSFDummyEntity;
import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.gui.BSFGuiTool;
import com.linngdu664.bsf.item.misc.SnowGolemCoreItem;
import com.linngdu664.bsf.item.tool.ScoringDevice;
import com.linngdu664.bsf.item.tool.SnowGolemModeTweakerItem;
import com.linngdu664.bsf.item.tool.TeamLinkerItem;
import com.linngdu664.bsf.item.weapon.AbstractBSFWeaponItem;
import com.linngdu664.bsf.item.weapon.SnowballMachineGunItem;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EntityRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.Consumer;

import static com.linngdu664.bsf.gui.BSFGuiTool.*;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class RenderGuiEventHandler {
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft instance = Minecraft.getInstance();
        if (instance.options.hideGui) {
            return;
        }
        Map<String,Object> varMap = new HashMap<>();
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
        GuiHandler.itemInHandBSFWeapon(guiGraphics,mainHandItem,offHandItem);
        if (pickType == HitResult.Type.ENTITY) {
            Entity entity1 = ((EntityHitResult) pick).getEntity();
            GuiHandler.pickEntityBSFSnowGolem(guiGraphics,entity1,partialTick,varMap);
            GuiHandler.pickEntityBSFDummy(guiGraphics,entity1);
        } else if (pickType == HitResult.Type.BLOCK) {
            BlockEntity blockEntity = player.level().getBlockEntity(((BlockHitResult) pick).getBlockPos());
            GuiHandler.pickBlockEntityVendingMachine(guiGraphics,blockEntity,mainHandItem,partialTick);
            GuiHandler.pickBlockEntityZoneController(guiGraphics,blockEntity,partialTick);
        }
        GuiHandler.itemInHandSnowGolemModeTweaker(guiGraphics,mainHandItem,offHandItem,varMap);
        GuiHandler.specialModelText(guiGraphics,varMap);


        guiGraphics.pose().popPose();
    }
}
