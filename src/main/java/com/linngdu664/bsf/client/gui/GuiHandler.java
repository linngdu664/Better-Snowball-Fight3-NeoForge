package com.linngdu664.bsf.client.gui;

import com.linngdu664.bsf.block.entity.RegionControllerBlockEntity;
import com.linngdu664.bsf.block.entity.VendingMachineBlockEntity;
import com.linngdu664.bsf.entity.BSFDummyEntity;
import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.item.minigame_tool.ScoringDeviceItem;
import com.linngdu664.bsf.item.minigame_tool.TeamLinkerItem;
import com.linngdu664.bsf.item.misc.SnowGolemCoreItem;
import com.linngdu664.bsf.item.tool.SnowGolemModeTweakerItem;
import com.linngdu664.bsf.item.weapon.AbstractBSFWeaponItem;
import com.linngdu664.bsf.item.weapon.SnowballMachineGunItem;
import com.linngdu664.bsf.network.to_client.TeamMembersPayload;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EffectRegister;
import com.linngdu664.bsf.registry.EntityRegister;
import com.linngdu664.bsf.util.BSFColorUtil;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.linngdu664.bsf.client.gui.BSFGuiTool.*;

@OnlyIn(Dist.CLIENT)
public class GuiHandler {
    public static void itemInHandBSFWeapon(GuiGraphics guiGraphics, ItemStack mainHandItem, ItemStack offHandItem) {
        Minecraft instance = Minecraft.getInstance();
        AbstractBSFWeaponItem weaponItem = null;
        ItemStack selectItem = null;
        if (mainHandItem.getItem() instanceof AbstractBSFWeaponItem item) {
            weaponItem = item;
            selectItem = mainHandItem;
        } else if (offHandItem.getItem() instanceof AbstractBSFWeaponItem item) {
            weaponItem = item;
            selectItem = offHandItem;
        }
        if (weaponItem != null) {
            Window window = instance.getWindow();
            ItemStack current = weaponItem.getCurrentAmmoItemStack();
            ItemStack prev = weaponItem.getPrevAmmoItemStack();
            ItemStack next = weaponItem.getNextAmmoItemStack();
            BSFGuiTool.V2I v2I = SNOWBALL_SLOT_FRAME_GUI.renderCenterVertically(guiGraphics, window, 0);
            int startPos = v2I.y;
            guiGraphics.renderItem(prev, 3, startPos + 3);
            guiGraphics.renderItem(current, 3, startPos + 23);
            guiGraphics.renderItem(next, 3, startPos + 43);
            guiGraphics.drawString(instance.font, String.valueOf(prev.getCount()), 24, startPos + 7, 0xffffffff);
            guiGraphics.drawString(instance.font, String.valueOf(current.getCount()), 24, startPos + 27, 0xffffffff);
            guiGraphics.drawString(instance.font, String.valueOf(next.getCount()), 24, startPos + 47, 0xffffffff);
            if (weaponItem.getTypeFlag() == SnowballMachineGunItem.TYPE_FLAG) {
                BSFGuiTool.V2I barFrame = new BSFGuiTool.V2I(100, 10);
                int padding = 2;
                BSFGuiTool.V2I barPos = new BSFGuiTool.V2I(widthFrameCenter(window, barFrame.x), heightFrameRatio(window, barFrame.y, 0.7));
                int timer = selectItem.getOrDefault(DataComponentRegister.MACHINE_GUN_TIMER, 0);
                boolean isCoolDown = selectItem.getOrDefault(DataComponentRegister.MACHINE_GUN_IS_COOL_DOWN, false);
                renderProgressBar(guiGraphics, barPos, barFrame, padding, 0xffffffff, isCoolDown ? 0xfffc3d49 : 0xffffffff, (float) timer / 360);
            }
        }
    }

    public static void pickEntityBSFSnowGolem(GuiGraphics guiGraphics, CoordinateConverter converter, Entity pickEntity, float partialTick, VarObj varObj) {
        Minecraft instance = Minecraft.getInstance();
        Player player = instance.player;
        if (pickEntity.getType().equals(EntityRegister.BSF_SNOW_GOLEM.get()) && player.equals(((BSFSnowGolemEntity) pickEntity).getOwner())) {
            Window window = instance.getWindow();
            BSFSnowGolemEntity entity = (BSFSnowGolemEntity) pickEntity;
            //显示装备
            List<Pair<Vec3, Consumer<Vec2>>> list = new ArrayList<>();
            Vec3 entityPosition = entity.getPosition(partialTick);
            Vec3 viewVector0Y = entity.getMiddleModelForward(partialTick, 0);
            ItemStack equip = entity.getWeapon();
            if (equip != ItemStack.EMPTY) {
                ItemStack finalEquip = equip;
                list.add(new Pair<>(entityPosition.add(entity.getMiddleModelForward(partialTick, 4).scale(0.7).add(0, 1.3, 0)), v2 -> {
                    V2I v2IRatio = v2IRatio(window, EQUIPMENT_SLOT_FRAME_GUI.width, EQUIPMENT_SLOT_FRAME_GUI.height, 0.3, 0.3);
                    renderEquipIntroduced(guiGraphics, v2, v2IRatio.getVec2(), widthWinRatio(window, 0.1), 0xffffffff, finalEquip, instance.font, Component.translatable("weapon.tip"));
                    float percent = (float) (finalEquip.getMaxDamage() - finalEquip.getDamageValue()) / finalEquip.getMaxDamage();
                    renderProgressBar(guiGraphics, new V2I(v2IRatio.x - 4, v2IRatio.y + 23), new V2I(30, 6), 2, 0xffffffff, percent > 0.3 ? 0xff85e900 : 0xfffc3d49, percent);
                }));
            }
            equip = entity.getAmmo();
            if (equip != ItemStack.EMPTY) {
                ItemStack finalEquip = equip;
                list.add(new Pair<>(entityPosition.add(viewVector0Y.scale(-0.3).add(0, 1.2, 0)), v2 -> {
                    V2I v2IRatio = v2IRatio(window, EQUIPMENT_SLOT_FRAME_GUI.width, EQUIPMENT_SLOT_FRAME_GUI.height, 0.3, 0.5);
                    renderEquipIntroduced(guiGraphics, v2, v2IRatio.getVec2(), widthWinRatio(window, 0.07), 0xffffffff, finalEquip, instance.font, Component.translatable("snowball.tip"));
                    float percent = (float) (finalEquip.getMaxDamage() - finalEquip.getDamageValue()) / finalEquip.getMaxDamage();
                    renderProgressBar(guiGraphics, new V2I(v2IRatio.x - 4, v2IRatio.y + 23), new V2I(30, 6), 2, 0xffffffff, percent > 0.3 ? 0xff85e900 : 0xfffc3d49, percent);
                }));
            }
            equip = entity.getCore();
            if (equip != ItemStack.EMPTY) {
                ItemStack finalEquip = equip;
                list.add(new Pair<>(entityPosition.add(viewVector0Y.scale(0.3).add(0, 1.05, 0)), v2 -> renderEquipIntroduced(guiGraphics, v2, v2IRatio(window, EQUIPMENT_SLOT_FRAME_GUI.width, EQUIPMENT_SLOT_FRAME_GUI.height, 0.3, 0.7).getVec2(), widthWinRatio(window, 0.12), 0xffffffff, finalEquip, instance.font, Component.translatable("core.tip"))));
            }
            converter.convertAndConsume(list, guiGraphics.guiWidth(), guiGraphics.guiHeight());
            //显示模式
            byte locator = entity.getLocator();
            byte status = entity.getStatus();
            varObj.sLocatorComponent = Component.translatable(SnowGolemModeTweakerItem.locatorMap(locator));
            varObj.sStatusComponent = Component.translatable(SnowGolemModeTweakerItem.statusMap(status));
            BSFGuiTool.V2I locateV2I = GOLEM_LOCATOR_GUI.renderRatio(guiGraphics, window, 0.7, 0.5);
            locateV2I.set(locateV2I.x - 1, locateV2I.y - 1 + locator * 20);
            GOLEM_SELECTOR_GUI.render(guiGraphics, locateV2I.x, locateV2I.y);
            BSFGuiTool.V2I statusV2I = GOLEM_STATUS_GUI.renderRatio(guiGraphics, window, 0.7, 0.5, 60, 0);
            statusV2I.set(statusV2I.x - 1, statusV2I.y - 1 + status * 20);
            GOLEM_SELECTOR_GUI.render(guiGraphics, statusV2I.x, statusV2I.y);
            if (entity.getEnhance()) {
                ADVANCE_MODE_GUI.renderRatio(guiGraphics, window, 0.5, 0.8);
            }
            varObj.locateV2I = locateV2I;
            varObj.statusV2I = statusV2I;

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
        }
    }

    public static void pickEntityBSFDummy(GuiGraphics guiGraphics, Entity pickEntity) {
        if (pickEntity.getType().equals(EntityRegister.BSF_DUMMY.get())) {
            Minecraft instance = Minecraft.getInstance();
            Window window = instance.getWindow();
            BSFDummyEntity dummy = (BSFDummyEntity) pickEntity;
            V2I v2I = v2IRatio(window, 0.4, 0.5);
            String dpsStr = String.format(dummy.getDPS() < 10 ? "DPS: %.2f" : "DPS: %.3g", dummy.getDPS());
            guiGraphics.drawString(instance.font, dpsStr, v2I.x - instance.font.width(dpsStr), v2I.y - 5, 0xffffffff);
        }
    }

    public static void pickBlockEntityVendingMachine(GuiGraphics guiGraphics, CoordinateConverter converter, BlockEntity blockEntity, ItemStack mainHandItem, float partialTick) {
        if (blockEntity instanceof VendingMachineBlockEntity vendingMachine) {
            Minecraft instance = Minecraft.getInstance();
            Window window = instance.getWindow();
            //显示货物
            converter.convertAndConsume(new Pair<>(vendingMachine.getBlockPos().getCenter(), v2 -> {
                V2I v2IRatio = v2IRatio(window, EQUIPMENT_SLOT_FRAME_GUI.width, EQUIPMENT_SLOT_FRAME_GUI.height, 0.3, 0.4);
                renderEquipIntroduced(guiGraphics, v2, v2IRatio.getVec2(), widthWinRatio(window, 0.1), 0xffffffff, vendingMachine.getGoods(), instance.font, Component.translatable("goods.tip"));
            }), guiGraphics.guiWidth(), guiGraphics.guiHeight());
            //显示价格等级百分比条
            if (mainHandItem.getItem() instanceof ScoringDeviceItem) {
                V2I barFrame = new V2I(100, 12);
                int padding = 2;
                V2I barPos = new V2I(widthFrameCenter(window, barFrame.x), heightFrameRatio(window, barFrame.y, 0.1));
                int deviceMoney = mainHandItem.getOrDefault(DataComponentRegister.MONEY.get(), 0);
                deviceMoney = Math.max(deviceMoney, 0);
                float v = (float) deviceMoney / vendingMachine.getPrice();
                renderProgressBar(guiGraphics, barPos, barFrame, padding, 0xffffffff, 0xffffd96d, v > 1 ? 1 : v);
                Component moneyTransComponent = Component.translatable("scoring_device_money.tooltip", deviceMoney + "/" + vendingMachine.getPrice());
                guiGraphics.drawString(instance.font, moneyTransComponent, barPos.x + ((barFrame.x - instance.font.width(moneyTransComponent)) / 2), barPos.y + padding, 0xffffffff);
                barPos.y += 25;
                int deviceRank = mainHandItem.getOrDefault(DataComponentRegister.RANK.get(), 0);
                deviceRank = Math.max(deviceRank, 0);
                v = (float) deviceRank / vendingMachine.getMinRank();
                renderProgressBar(guiGraphics, barPos, barFrame, padding, 0xffffffff, 0xff84e800, v > 1 ? 1 : v);
                Component rankTransComponent = Component.translatable("scoring_device_rank.tooltip", deviceRank + "/" + vendingMachine.getMinRank());
                guiGraphics.drawString(instance.font, rankTransComponent, barPos.x + ((barFrame.x - instance.font.width(rankTransComponent)) / 2), barPos.y + padding, 0xffffffff);
            }
            //显示操作提示文字
            V2I v2I = v2IRatio(window, 0.6, 0.4);
            guiGraphics.drawString(instance.font, Component.translatable("vending_price.tip", vendingMachine.getPrice()), v2I.x, v2I.y, 0xffffffff);
            guiGraphics.drawString(instance.font, Component.translatable("vending_rank.tip", vendingMachine.getMinRank()), v2I.x, v2I.y + 10, 0xffffffff);
            guiGraphics.drawString(instance.font, Component.translatable("vending_buy.tip", instance.options.keyUse.getTranslatedKeyMessage()), v2I.x, v2I.y + 20, 0xffffffff);
            if (vendingMachine.isCanSell()) {
                guiGraphics.drawString(instance.font, Component.translatable("recyclable.tip"), v2I.x, v2I.y + 40, 0xff3574f0);
                guiGraphics.drawString(instance.font, Component.translatable("vending_recycle.tip", vendingMachine.getPrice()), v2I.x, v2I.y + 50, 0xffffffff);
                guiGraphics.drawString(instance.font, Component.translatable("vending_sell.tip", instance.options.keyShift.getTranslatedKeyMessage(), instance.options.keyUse.getTranslatedKeyMessage()), v2I.x, v2I.y + 60, 0xffffffff);
            }
            //显示商品名
            Component displayName = vendingMachine.getGoods().getDisplayName();
            v2I = v2IRatio(window, instance.font.width(displayName.getString()), 0, 0.5, 0.7);
            guiGraphics.drawString(instance.font, displayName, v2I.x, v2I.y, 0xff41a5ee);
        }
    }

    public static void pickBlockEntityZoneController(GuiGraphics guiGraphics, CoordinateConverter converter, BlockEntity blockEntity, float partialTick) {
        if (blockEntity instanceof RegionControllerBlockEntity zoneController) {
            Minecraft instance = Minecraft.getInstance();
            Window window = instance.getWindow();
            //显示队伍
            converter.convertAndConsume(new Pair<>(zoneController.getBlockPos().getCenter(), v2 -> {
                V2I v2IRatio = v2IRatio(window, EQUIPMENT_SLOT_FRAME_GUI.width, EQUIPMENT_SLOT_FRAME_GUI.height, 0.3, 0.3);
                byte teamId = zoneController.getTeamId();
                renderEquipIntroduced(guiGraphics, v2, v2IRatio.getVec2(), widthWinRatio(window, 0.1), DyeColor.byId(teamId).getTextColor() | 0xff000000, TeamLinkerItem.getItemStackById(teamId), instance.font, BSFColorUtil.getColorTransNameById(teamId));
            }), guiGraphics.guiWidth(), guiGraphics.guiHeight());
            //显示操作提示文字
            V2I v2I = v2IRatio(window, 0.6, 0.4);
            guiGraphics.drawString(instance.font, Component.translatable("region_controller_strength.tip", String.format("%.2f", zoneController.getCurrentStrength())), v2I.x, v2I.y, 0xffffffff);
            guiGraphics.drawString(instance.font, Component.translatable("region_controller_enter.tip", instance.options.keyUse.getTranslatedKeyMessage()), v2I.x, v2I.y + 10, 0xffffffff);
        }
    }

    public static void itemInHandSnowGolemModeTweaker(GuiGraphics guiGraphics, ItemStack mainHandItem, ItemStack offHandItem, VarObj varObj) {
        ItemStack tweaker = null;
        if (mainHandItem.getItem() instanceof SnowGolemModeTweakerItem) {
            tweaker = mainHandItem;
        } else if (offHandItem.getItem() instanceof SnowGolemModeTweakerItem) {
            tweaker = offHandItem;
        }
        if (tweaker != null) {
            Minecraft instance = Minecraft.getInstance();
            Window window = instance.getWindow();
            //显示模式调整器gui
            byte locator = tweaker.getOrDefault(DataComponentRegister.TWEAKER_TARGET_MODE, (byte) 0);
            varObj.tLocatorComponent = Component.translatable(SnowGolemModeTweakerItem.locatorMap(locator));
            byte status = tweaker.getOrDefault(DataComponentRegister.TWEAKER_STATUS_MODE, (byte) 0);
            varObj.tStatusComponent = Component.translatable(SnowGolemModeTweakerItem.statusMap(status));
            V2I locateV2IT = TWEAKER_LOCATOR_GUI.renderRatio(guiGraphics, window, 0.7, 0.5, 30, 0);
            locateV2IT.set(locateV2IT.x - 1, locateV2IT.y - 1 + locator * 20);
            TWEAKER_SELECTOR_GUI.render(guiGraphics, locateV2IT.x, locateV2IT.y);
            V2I statusV2IT = TWEAKER_STATUS_GUI.renderRatio(guiGraphics, window, 0.7, 0.5, 90, 0);
            statusV2IT.set(statusV2IT.x - 1, statusV2IT.y - 1 + status * 20);
            TWEAKER_SELECTOR_GUI.render(guiGraphics, statusV2IT.x, statusV2IT.y);
            V2I locateV2I = varObj.locateV2I;
            if (locateV2I != null && locateV2I.y != locateV2IT.y) {
                SETTER_ARROW_GUI.render(guiGraphics, locateV2I.x + 23, locateV2IT.y + 2);
            }
            V2I statusV2I = varObj.statusV2I;
            if (statusV2I != null && statusV2I.y != statusV2IT.y) {
                SETTER_ARROW_GUI.render(guiGraphics, statusV2I.x + 23, statusV2IT.y + 2);
            }
        }
    }

    public static void specialModeText(GuiGraphics guiGraphics, VarObj varObj) {
        Component sLocatorComponent = varObj.sLocatorComponent;
        Component tLocatorComponent = varObj.tLocatorComponent;
        Component sStatusComponent = varObj.sStatusComponent;
        Component tStatusComponent = varObj.tStatusComponent;
        if (!(sLocatorComponent == null && tLocatorComponent == null)) {
            Minecraft instance = Minecraft.getInstance();
            Window window = instance.getWindow();
            //显示模式调整文字
            Component lStr = Component.translatable("tweaker_target.tip", sLocatorComponent == null ? tLocatorComponent : tLocatorComponent == null || sLocatorComponent.equals(tLocatorComponent) ? sLocatorComponent : sLocatorComponent.getString() + " << " + tLocatorComponent.getString());
            Component sStr = Component.translatable("tweaker_status.tip", sStatusComponent == null ? tStatusComponent : tStatusComponent == null || sStatusComponent.equals(tStatusComponent) ? sStatusComponent : sStatusComponent.getString() + " << " + tStatusComponent.getString());
            V2I v2I = v2IRatio(window, 0.6, 0.75);
            guiGraphics.drawString(instance.font, lStr, v2I.x, v2I.y, 0xffffffff);
            guiGraphics.drawString(instance.font, sStr, v2I.x, v2I.y + 10, 0xffffffff);
        }
    }

    public static void specialScoreText(GuiGraphics guiGraphics) {
        if (ScoringGuiHandler.hourMeter > 0) {
            Minecraft instance = Minecraft.getInstance();
            Window window = instance.getWindow();
            V2I v2I = v2IRatio(window, 0.9, 0.4);
            Component scoreComponent;
            if (ScoringGuiHandler.money >= 0) {
                scoreComponent = Component.translatable("scoring_device_kill_bonus.tip", String.valueOf(ScoringGuiHandler.rank), String.valueOf(ScoringGuiHandler.money));
            } else {
                scoreComponent = Component.translatable("scoring_device_death_punishment.tip", String.valueOf(ScoringGuiHandler.money));
            }
            RenderSystem.enableBlend();
            guiGraphics.drawString(instance.font, scoreComponent, v2I.x - instance.font.width(scoreComponent), v2I.y, 0xffffff | ScoringGuiHandler.getBlend());
            RenderSystem.disableBlend();
        }
    }

    public static void specialWallhackUi(GuiGraphics guiGraphics, CoordinateConverter converter, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player.hasEffect(EffectRegister.WALLHACK)) {
            Window window = mc.getWindow();
            Level level = player.level();
            level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(64), p -> !TeamMembersPayload.isFriendly(p) && (p instanceof Enemy || p.getType().equals(EntityType.PLAYER) || p.getType().equals(EntityRegister.BSF_SNOW_GOLEM.get()))).forEach(livingEntity -> {
                Vec2 boxBottom = renderHackBox(guiGraphics, converter, window, livingEntity, 0xffff0000, partialTick);
                if (boxBottom != null) {
                    renderLineTool(guiGraphics, boxBottom, v2IRatio(window, 0.5, 1.1).getVec2(), 0.5f, 0xff0000ff, true, 0, 0xff0000ff);
                }
            });
        }
    }
}
