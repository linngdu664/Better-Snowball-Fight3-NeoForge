package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.block.entity.VendingMachineEntity;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import com.linngdu664.bsf.network.to_client.CurrentTeamPayload;
import com.linngdu664.bsf.network.to_client.ForwardRaysParticlesPayload;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.particle.util.ForwardRaysParticlesParas;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.SoundRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class ScoringDevice extends Item {
    public ScoringDevice(Properties properties) {
        super(properties.rarity(Rarity.EPIC).stacksTo(1).component(DataComponentRegister.RANK.get(), 0).component(DataComponentRegister.MONEY.get(), 0));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (level.getBlockEntity(context.getClickedPos()) instanceof VendingMachineEntity be) {
            if (!level.isClientSide) {
                int money = stack.getOrDefault(DataComponentRegister.MONEY.get(), 0);
                ItemStack goods = be.getGoods();
                if (player.isShiftKeyDown()) {
                    if (be.isCanSell()) {
                        List<ItemStack> stacks = BSFCommonUtil.findInventoryItemStacks(player, p -> goods.getItem().equals(p.getItem()));
                        if (stacks.isEmpty()) {
                            player.displayClientMessage(Component.translatable("scoring_device_no_item_to_sell.tip"), false);
                        } else {
                            // 退款成功
                            int addMoney = 0;
                            if (goods.isDamageableItem()) {
                                int maxDamage = goods.getMaxDamage();
                                for (ItemStack stack1 : stacks) {
                                    addMoney += (int) ((float) (maxDamage - stack1.getOrDefault(DataComponents.DAMAGE, 0)) / (float) maxDamage * (float) be.getPrice());
                                }
                            } else {
                                for (ItemStack stack1 : stacks) {
                                    addMoney += stack1.getCount() * be.getPrice();
                                }
                            }
                            stack.set(DataComponentRegister.MONEY.get(), money + addMoney);
                            Inventory inventory = player.getInventory();
                            for (int i = 0, k = inventory.getContainerSize(); i < k; i++) {
                                if (inventory.getItem(i).getItem().equals(goods.getItem())) {
                                    inventory.setItem(i, ItemStack.EMPTY);
                                }
                            }
                            player.displayClientMessage(Component.translatable("scoring_device_sell_success.tip", String.valueOf(addMoney)), false);
                        }
                    } else {
                        player.displayClientMessage(Component.translatable("scoring_device_cannot_sell.tip"), false);
                    }
                } else {
                    if (be.getMinRank() > stack.getOrDefault(DataComponentRegister.RANK.get(), 0)) {
                        // 等级过低，无法购买
                        player.displayClientMessage(Component.translatable("scoring_device_rank_low.tip"), false);
                    } else if (be.getPrice() > money) {
                        // 钱不够，无法购买
                        player.displayClientMessage(Component.translatable("scoring_device_no_money.tip"), false);
                    } else {
                        player.getInventory().placeItemBackInInventory(goods);
                        stack.set(DataComponentRegister.MONEY.get(), money - be.getPrice());
                        player.displayClientMessage(Component.translatable("scoring_device_buy_success.tip"), false);
                    }
                }
                Vec3 soundPos = be.getBlockPos().getCenter();
                level.playSound(null, soundPos.x, soundPos.y, soundPos.z, SoundEvents.DISPENSER_DISPENSE, SoundSource.PLAYERS, 6.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        RegionData region = itemStack.getOrDefault(DataComponentRegister.REGION.get(), RegionData.EMPTY);
        int rank = itemStack.getOrDefault(DataComponentRegister.RANK.get(), 0);
        Predicate<ItemStack> predicate = p -> {
            if (p.isEmpty()) {
                return false;
            }
            if (!p.has(DataComponentRegister.REGION.get())) {
                return true;
            }
            return !p.get(DataComponentRegister.REGION.get()).equals(region);
        };
        if (pLevel.isClientSide) {
            if (CurrentTeamPayload.currentTeam < 0 || BSFCommonUtil.findInventoryItemStack(pPlayer, predicate) != null) {
                return InteractionResultHolder.fail(itemStack);
            }
            if (rank < 0 && !region.inRegion(pPlayer.position())) {
                return InteractionResultHolder.fail(itemStack);
            }
        } else {
            BSFTeamSavedData savedData = pLevel.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
            if (rank < 0 && !region.inRegion(pPlayer.position())) {
                pPlayer.displayClientMessage(Component.translatable("scoring_device_tp_failed2.tip"), false);
                return InteractionResultHolder.fail(itemStack);
            }
            if (savedData.getTeam(pPlayer.getUUID()) < 0) {
                pPlayer.displayClientMessage(Component.translatable("scoring_device_tp_failed1_0.tip"), false);
                return InteractionResultHolder.fail(itemStack);
            }
            if (BSFCommonUtil.findInventoryItemStack(pPlayer, predicate) != null) {
                pPlayer.displayClientMessage(Component.translatable("scoring_device_tp_failed1_1.tip"), false);
                return InteractionResultHolder.fail(itemStack);
            }

        }
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide) {
            Vec3 color = new Vec3(0.9, 0.9, 0.9);
            PacketDistributor.sendToPlayersTrackingEntity(livingEntity, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(livingEntity.getPosition(1).add(-0.5, 0, -0.5), livingEntity.getPosition(1).add(0.5, 1, 0.5), color, color.length(), color.length(), 5), BSFParticleType.SPAWN_SNOW.ordinal()));
            livingEntity.playSound(SoundRegister.FREEZING.get(), 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
        }
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        if (count == 0) {
            Vec3 tpPoint = stack.getOrDefault(DataComponentRegister.TP_POINT.get(), entity.getPosition(1));
            entity.moveTo(tpPoint);
            entity.playSound(SoundRegister.FORCE_EXECUTOR_START.get(), 3.0F, 1.0F);
        }
    }

    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 40;
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int rank = stack.getOrDefault(DataComponentRegister.RANK.get(), 0);
        int money = stack.getOrDefault(DataComponentRegister.MONEY.get(), 0);
        RegionData region = stack.getOrDefault(DataComponentRegister.REGION.get(), RegionData.EMPTY);
        tooltipComponents.add(Component.translatable("scoring_device.tooltip"));
        tooltipComponents.add(Component.translatable("scoring_device1.tooltip"));
        tooltipComponents.add(Component.translatable("scoring_device_rank.tooltip", String.valueOf(rank)));
        tooltipComponents.add(Component.translatable("scoring_device_money.tooltip", String.valueOf(money)));
        tooltipComponents.add(Component.translatable(
                "scoring_device_region.tooltip",
                region.start().getX(),
                region.start().getY(),
                region.start().getZ(),
                region.end().getX(),
                region.end().getY(),
                region.end().getZ()
        ));
    }
}
