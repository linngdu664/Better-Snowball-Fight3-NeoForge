package com.linngdu664.bsf.event;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.block.entity.VendingMachineEntity;
import com.linngdu664.bsf.block.entity.ZoneControllerEntity;
import com.linngdu664.bsf.entity.BSFDummyEntity;
import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.item.misc.SnowGolemCoreItem;
import com.linngdu664.bsf.item.tool.ScoringDevice;
import com.linngdu664.bsf.item.tool.SnowGolemModeTweakerItem;
import com.linngdu664.bsf.item.tool.TeamLinkerItem;
import com.linngdu664.bsf.item.weapon.AbstractBSFWeaponItem;
import com.linngdu664.bsf.item.weapon.SnowballMachineGunItem;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EntityRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.linngdu664.bsf.event.BSFGui.*;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class RenderGuiEventHandler {
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft instance = Minecraft.getInstance();
        if (instance.options.hideGui) {
            return;
        }
        Player player = instance.player;
        AbstractBSFWeaponItem weaponItem = null;
        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offHandItem = player.getOffhandItem();
        ItemStack selectItem = null;
        if (mainHandItem.getItem() instanceof AbstractBSFWeaponItem item) {
            weaponItem = item;
            selectItem = mainHandItem;
        } else if (offHandItem.getItem() instanceof AbstractBSFWeaponItem item) {
            weaponItem = item;
            selectItem = offHandItem;
        }
        GuiGraphics guiGraphics = event.getGuiGraphics();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0F, 0F, 4932F);        // 显示在原版gui的上方
        Window window = instance.getWindow();
        if (weaponItem != null) {
            ItemStack current = weaponItem.getCurrentAmmoItemStack();
            ItemStack prev = weaponItem.getPrevAmmoItemStack();
            ItemStack next = weaponItem.getNextAmmoItemStack();
            BSFGui.V2I v2I = SNOWBALL_SLOT_FRAME_GUI.renderCenterVertically(guiGraphics, window, 0);
            int startPos = v2I.y;
            guiGraphics.renderItem(prev, 3, startPos + 3);
            guiGraphics.renderItem(current, 3, startPos + 23);
            guiGraphics.renderItem(next, 3, startPos + 43);
            guiGraphics.drawString(instance.font, String.valueOf(prev.getCount()), 24, startPos + 7, 0xffffffff);
            guiGraphics.drawString(instance.font, String.valueOf(current.getCount()), 24, startPos + 27, 0xffffffff);
            guiGraphics.drawString(instance.font, String.valueOf(next.getCount()), 24, startPos + 47, 0xffffffff);
            if (weaponItem.getTypeFlag()== SnowballMachineGunItem.TYPE_FLAG){
                BSFGui.V2I barFrame = new BSFGui.V2I(100, 10);
                int padding = 2;
                BSFGui.V2I barPos = new BSFGui.V2I(widthFrameCenter(window, barFrame.x), heightFrameRatio(window, barFrame.y, 0.7));
                int timer = selectItem.getOrDefault(DataComponentRegister.MACHINE_GUN_TIMER,0);
                boolean isCoolDown = selectItem.getOrDefault(DataComponentRegister.MACHINE_GUN_IS_COOL_DOWN,false);
                renderProgressBar(guiGraphics, barPos, barFrame, padding, 0xffffffff, isCoolDown?0xfffc3d49:0xffffffff, (float) timer /360);
            }
        }
        HitResult pick = instance.hitResult;
        BSFGui.V2I locateV2I = null,statusV2I = null;
        String sLocatorStr = "";
        String sStatusStr = "";
        if (pick.getType() == HitResult.Type.ENTITY) {
            Entity entity1 = ((EntityHitResult) pick).getEntity();
            if (entity1.getType().equals(EntityRegister.BSF_SNOW_GOLEM.get()) && player.equals(((BSFSnowGolemEntity) entity1).getOwner())) {
                BSFSnowGolemEntity entity = (BSFSnowGolemEntity) entity1;
                //显示装备
                float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
                List<Pair<Vec3, Consumer<Vec2>>> list = new ArrayList<>();
                Vec3 entityPosition = entity.getPosition(partialTick);
                Vec3 viewVector0Y = entity.getMiddleModelForward(partialTick, 0);
                ItemStack equip = entity.getWeapon();
                if (equip != ItemStack.EMPTY) {
                    ItemStack finalEquip = equip;
                    list.add(new Pair<>(entityPosition.add(entity.getMiddleModelForward(partialTick, 4).scale(0.7).add(0, 1.3, 0)), v2 -> {
                        V2I v2IRatio = v2IRatio(window, EQUIPMENT_SLOT_FRAME_GUI.width, EQUIPMENT_SLOT_FRAME_GUI.height, 0.3, 0.3);
                        renderEquipIntroduced(guiGraphics, v2, v2IRatio.getVec2(), widthWinRatio(window, 0.1), 0xffffffff, finalEquip, instance.font, BSFCommonUtil.getTransStr("gui_text_weapon"));
                        float percent = (float) (finalEquip.getMaxDamage() - finalEquip.getDamageValue()) / finalEquip.getMaxDamage();
                        renderProgressBar(guiGraphics, new V2I(v2IRatio.x - 4, v2IRatio.y + 23), new V2I(30, 6), 2, 0xffffffff, percent > 0.3 ? 0xff85e900 : 0xfffc3d49, percent);
                    }));
                }
                equip = entity.getAmmo();
                if (equip != ItemStack.EMPTY) {
                    ItemStack finalEquip = equip;
                    list.add(new Pair<>(entityPosition.add(viewVector0Y.scale(-0.3).add(0, 1.2, 0)), v2 -> {
                        V2I v2IRatio = v2IRatio(window, EQUIPMENT_SLOT_FRAME_GUI.width, EQUIPMENT_SLOT_FRAME_GUI.height, 0.3, 0.5);
                        renderEquipIntroduced(guiGraphics, v2, v2IRatio.getVec2(), widthWinRatio(window, 0.07), 0xffffffff, finalEquip, instance.font, BSFCommonUtil.getTransStr("gui_text_snowball"));
                        float percent = (float) (finalEquip.getMaxDamage() - finalEquip.getDamageValue()) / finalEquip.getMaxDamage();
                        renderProgressBar(guiGraphics, new V2I(v2IRatio.x - 4, v2IRatio.y + 23), new V2I(30, 6), 2, 0xffffffff, percent > 0.3 ? 0xff85e900 : 0xfffc3d49, percent);
                    }));
                }
                equip = entity.getCore();
                if (equip != ItemStack.EMPTY) {
                    ItemStack finalEquip = equip;
                    list.add(new Pair<>(entityPosition.add(viewVector0Y.scale(0.3).add(0, 1.05, 0)), v2 -> {
                        renderEquipIntroduced(guiGraphics, v2, v2IRatio(window, EQUIPMENT_SLOT_FRAME_GUI.width, EQUIPMENT_SLOT_FRAME_GUI.height, 0.3, 0.7).getVec2(), widthWinRatio(window, 0.12), 0xffffffff, finalEquip, instance.font, BSFCommonUtil.getTransStr("gui_text_core"));
                    }));
                }
                calcScreenPosFromWorldPos(list, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0, 0, partialTick);
                //显示模式
                byte locator = entity.getLocator();
                byte status = entity.getStatus();
                sLocatorStr = BSFCommonUtil.getTransStr(SnowGolemModeTweakerItem.locatorMap(locator));
                sStatusStr = BSFCommonUtil.getTransStr(SnowGolemModeTweakerItem.statusMap(status));
                locateV2I = GOLEM_LOCATOR_GUI.renderRatio(guiGraphics, window, 0.7, 0.5);
                locateV2I.set(locateV2I.x - 1, locateV2I.y - 1 + locator * 20);
                GOLEM_SELECTOR_GUI.render(guiGraphics, locateV2I.x, locateV2I.y);
                statusV2I = GOLEM_STATUS_GUI.renderRatio(guiGraphics, window, 0.7, 0.5, 60, 0);
                statusV2I.set(statusV2I.x - 1, statusV2I.y - 1 + status * 20);
                GOLEM_SELECTOR_GUI.render(guiGraphics, statusV2I.x, statusV2I.y);
                if (entity.getEnhance()) {
                    ADVANCE_MODE_GUI.renderRatio(guiGraphics, window, 0.5, 0.8);
                }

                //显示血条/cd
                V2I barFrame = new V2I(100, 10);
                int padding = 2;
                V2I barPos = new V2I(widthFrameCenter(window, barFrame.x), heightFrameRatio(window, barFrame.y, 0.1));
                renderProgressBar(guiGraphics, barPos, barFrame, padding, 0xffffffff, 0xffe82f27, entity.getHealth() / entity.getMaxHealth());
                if (entity.getCore().getItem() instanceof SnowGolemCoreItem item && entity.getCoreCoolDown() > 0) {
                    barPos.y += 15;
                    renderProgressBar(guiGraphics, barPos, barFrame, padding, 0xffffffff, 0xff26a7ff, (float) entity.getCoreCoolDown() / item.getCoolDown());
                }
                if (entity.getPotionSickness() > 0) {
                    barPos.y += 15;
                    renderProgressBar(guiGraphics, barPos, barFrame, padding, 0xffffffff, 0xff62df86, (float) entity.getPotionSickness() / 100);
                }

                //显示当前目标
                Optional<Component> targetName = entity.getTargetName();
                V2I v2I = v2IRatio(window, 0.4, 0.75);
                Component transComp = Component.translatable("tweaker_target_now.tip", targetName.orElseGet(() -> Component.translatable("snow_golem_target_null.tip")));
                guiGraphics.drawString(instance.font, transComp, v2I.x - instance.font.width(transComp), v2I.y, 0xffffffff);
            } else if (entity1.getType().equals(EntityRegister.BSF_DUMMY.get())) {
                BSFDummyEntity dummy = (BSFDummyEntity) entity1;
                V2I v2I = v2IRatio(window, 0.4, 0.5);
                String dpsStr = String.format(dummy.getDPS() < 10 ? "DPS: %.2f" : "DPS: %.3g", dummy.getDPS());
                guiGraphics.drawString(instance.font, dpsStr, v2I.x - instance.font.width(dpsStr), v2I.y - 5, 0xffffffff);
            }
        } else if (pick.getType() == HitResult.Type.BLOCK) {
            BlockEntity blockEntity = player.level().getBlockEntity(((BlockHitResult) pick).getBlockPos());
            if (blockEntity instanceof VendingMachineEntity vendingMachine){
                calcScreenPosFromWorldPos(new Pair<>(vendingMachine.getBlockPos().getCenter(), v2->{
                    V2I v2IRatio = v2IRatio(window, EQUIPMENT_SLOT_FRAME_GUI.width, EQUIPMENT_SLOT_FRAME_GUI.height, 0.3, 0.4);
                    renderEquipIntroduced(guiGraphics, v2, v2IRatio.getVec2(), widthWinRatio(window, 0.1), 0xffffffff, vendingMachine.getGoods(), instance.font, "["+ BSFCommonUtil.getTransStr("scoring_device_rank.tooltip", vendingMachine.getMinRank()) +"] ["+BSFCommonUtil.getTransStr("scoring_device_money.tooltip", vendingMachine.getPrice())+"]");
                }),guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0, 0, event.getPartialTick().getGameTimeDeltaPartialTick(true));
                if (mainHandItem.getItem() instanceof ScoringDevice){
                    V2I barFrame = new V2I(100, 10);
                    int padding = 2;
                    V2I barPos = new V2I(widthFrameCenter(window, barFrame.x), heightFrameRatio(window, barFrame.y, 0.1));
                    int deviceMoney = mainHandItem.getOrDefault(DataComponentRegister.MONEY.get(), 0);
                    renderProgressBar(guiGraphics, barPos, barFrame, padding, 0xffffffff, 0xffe82f27,  (float) deviceMoney / vendingMachine.getPrice());
                    String moneyTransStr = BSFCommonUtil.getTransStr("scoring_device_money.tooltip", deviceMoney + "/" + vendingMachine.getPrice());
                    guiGraphics.drawString(instance.font, moneyTransStr,barPos.x+(barFrame.x-instance.font.width(moneyTransStr)/2),barPos.y+10,0xffffffff);
                    barPos.y+=25;
                    int deviceRank = mainHandItem.getOrDefault(DataComponentRegister.RANK.get(), 0);
                    renderProgressBar(guiGraphics, barPos, barFrame, padding, 0xffffffff, 0xffe82f27,  (float) deviceRank / vendingMachine.getMinRank());
                    String rankTransStr = BSFCommonUtil.getTransStr("scoring_device_rank.tooltip", deviceMoney + "/" + vendingMachine.getPrice());
                    guiGraphics.drawString(instance.font, rankTransStr,barPos.x+(barFrame.x-instance.font.width(rankTransStr)/2),barPos.y+10,0xffffffff);
                }
            }else if(blockEntity instanceof ZoneControllerEntity zoneController){
                calcScreenPosFromWorldPos(new Pair<>(zoneController.getBlockPos().getCenter(), v2->{
                    V2I v2IRatio = v2IRatio(window, EQUIPMENT_SLOT_FRAME_GUI.width, EQUIPMENT_SLOT_FRAME_GUI.height, 0.3, 0.3);
                    byte teamId = zoneController.getTeamId();
                    renderEquipIntroduced(guiGraphics, v2, v2IRatio.getVec2(), widthWinRatio(window, 0.1), DyeColor.byId(teamId).getTextColor(), TeamLinkerItem.getItemStackById(teamId), instance.font, TeamLinkerItem.getColorTransNameById(teamId));
                }),guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0, 0, event.getPartialTick().getGameTimeDeltaPartialTick(true));
            }
        }
        ItemStack tweaker = null;
        if (mainHandItem.getItem() instanceof SnowGolemModeTweakerItem) {
            tweaker = mainHandItem;
        } else if (offHandItem.getItem() instanceof SnowGolemModeTweakerItem) {
            tweaker = offHandItem;
        }
        String tLocatorStr = "";
        String tStatusStr = "";
        if (tweaker != null) {
            byte locator = tweaker.getOrDefault(DataComponentRegister.TWEAKER_TARGET_MODE, (byte) 0);
            tLocatorStr = BSFCommonUtil.getTransStr(SnowGolemModeTweakerItem.locatorMap(locator));
            byte status = tweaker.getOrDefault(DataComponentRegister.TWEAKER_STATUS_MODE, (byte) 0);
            tStatusStr = BSFCommonUtil.getTransStr(SnowGolemModeTweakerItem.statusMap(status));
            V2I locateV2IT = TWEAKER_LOCATOR_GUI.renderRatio(guiGraphics, window, 0.7, 0.5, 30, 0);
            locateV2IT.set(locateV2IT.x - 1, locateV2IT.y - 1 + locator * 20);
            TWEAKER_SELECTOR_GUI.render(guiGraphics, locateV2IT.x, locateV2IT.y);
            V2I statusV2IT = TWEAKER_STATUS_GUI.renderRatio(guiGraphics, window, 0.7, 0.5, 90, 0);
            statusV2IT.set(statusV2IT.x - 1, statusV2IT.y - 1 + status * 20);
            TWEAKER_SELECTOR_GUI.render(guiGraphics, statusV2IT.x, statusV2IT.y);
            if (locateV2I != null && locateV2I.y != locateV2IT.y) {
                SETTER_ARROW_GUI.render(guiGraphics, locateV2I.x + 23, locateV2IT.y + 2);
            }
            if (statusV2I != null && statusV2I.y != statusV2IT.y) {
                SETTER_ARROW_GUI.render(guiGraphics, statusV2I.x + 23, statusV2IT.y + 2);
            }
        }
        if (!(sLocatorStr.isEmpty() && tLocatorStr.isEmpty())) {
            String lStr = BSFCommonUtil.getTransStr("tweaker_target.tip", sLocatorStr.isEmpty() ? tLocatorStr : tLocatorStr.isEmpty() || sLocatorStr.equals(tLocatorStr) ? sLocatorStr : sLocatorStr + " << " + tLocatorStr);
            String sStr = BSFCommonUtil.getTransStr("tweaker_status.tip", sStatusStr.isEmpty() ? tStatusStr : tStatusStr.isEmpty() || sStatusStr.equals(tStatusStr) ? sStatusStr : sStatusStr + " << " + tStatusStr);
            V2I v2I = v2IRatio(window, 0.6, 0.75);
            guiGraphics.drawString(instance.font, lStr, v2I.x, v2I.y, 0xffffffff);
            guiGraphics.drawString(instance.font, sStr, v2I.x, v2I.y+10, 0xffffffff);
        }
        guiGraphics.pose().popPose();
    }
}
