package com.linngdu664.bsf.item.minigame_tool;

import com.linngdu664.bsf.block.entity.VendingMachineBlockEntity;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import com.linngdu664.bsf.network.to_client.CurrentTeamPayload;
import com.linngdu664.bsf.network.to_client.ForwardRaysParticlesPayload;
import com.linngdu664.bsf.network.to_client.packed_paras.ForwardRaysParticlesParas;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.SoundRegister;
import com.linngdu664.bsf.util.BSFColorUtil;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.List;

public class ScoringDeviceItem extends Item {
    public ScoringDeviceItem() {
        super(com.linngdu664.bsf.Main.itemProperties().rarity(Rarity.EPIC).stacksTo(1).component(DataComponentRegister.RANK.get(), 0).component(DataComponentRegister.MONEY.get(), 0));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (level.getBlockEntity(context.getClickedPos()) instanceof VendingMachineBlockEntity be) {
            if (!level.isClientSide()) {
                int money = stack.getOrDefault(DataComponentRegister.MONEY.get(), 0);
                ItemStack goods = be.getGoods();
                if (player.isShiftKeyDown()) {
                    if (be.isCanSell()) {
                        List<ItemStack> stacks = BSFCommonUtil.findInventoryItemStacks(player, p -> {
                            if (!goods.getItem().equals(p.getItem())) {
                                return false;
                            }
                            for (TypedDataComponent<?> component : goods.getComponents()) {
                                if (!p.has(component.type())) {
                                    return false;
                                }
                                if (!component.value().equals(p.get(component.type()))) {
                                    return false;
                                }
                            }
                            return true;
                        });
                        if (stacks.isEmpty()) {
                            player.sendSystemMessage(Component.translatable("scoring_device_no_item_to_sell.tip"));
                        } else {
                            // Refund succeeded.
                            int addMoney = 0;
                            for (ItemStack stack1 : stacks) {
                                addMoney += stack1.getCount() * be.getPrice();
                            }
                            stack.set(DataComponentRegister.MONEY.get(), money + addMoney);
                            for (ItemStack stack1 : stacks) {
                                stack1.setCount(0);
                            }
                            player.sendSystemMessage(Component.translatable("scoring_device_sell_success.tip", String.valueOf(addMoney)));
                        }
                    } else {
                        player.sendSystemMessage(Component.translatable("scoring_device_cannot_sell.tip"));
                    }
                } else {
                    if (be.getMinRank() > stack.getOrDefault(DataComponentRegister.RANK.get(), 0)) {
                        // Rank is too low to buy.
                        player.sendSystemMessage(Component.translatable("scoring_device_rank_low.tip"));
                    } else if (be.getPrice() > money) {
                        // Not enough money to buy.
                        player.sendSystemMessage(Component.translatable("scoring_device_no_money.tip"));
                    } else {
                        player.getInventory().placeItemBackInInventory(goods);
                        stack.set(DataComponentRegister.MONEY.get(), money - be.getPrice());
//                        player.sendSystemMessage(Component.translatable("scoring_device_buy_success.tip"));
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
    public @NotNull InteractionResult use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (itemStack.getOrDefault(DataComponentRegister.RANK.get(), 0) < 0) {
            pPlayer.startUsingItem(pUsedHand);
            return InteractionResult.CONSUME;
        }
        RegionData region = itemStack.getOrDefault(DataComponentRegister.REGION.get(), RegionData.EMPTY);
        byte team = itemStack.getOrDefault(DataComponentRegister.TEAM.get(), (byte) 0);
        Predicate<ItemStack> predicate = p -> {
            if (p.isEmpty()) {
                return false;
            }
            if (!p.has(DataComponentRegister.REGION.get())) {
                return true;
            }
            return !p.get(DataComponentRegister.REGION.get()).equals(region);
        };
        if (pLevel.isClientSide()) {
            if (CurrentTeamPayload.currentTeam != team || BSFCommonUtil.findInventoryItemStack(pPlayer, predicate) != null) {
                return InteractionResult.FAIL;
            }
        } else {
            BSFTeamSavedData savedData = pLevel.getServer().overworld().getDataStorage().computeIfAbsent(BSFTeamSavedData.TYPE);
            if (savedData.getTeam(pPlayer.getUUID()) != team) {
                pPlayer.sendSystemMessage(Component.translatable("scoring_device_tp_failed1_0.tip", BSFColorUtil.getColorTransNameById(team)));
                return InteractionResult.FAIL;
            }
            if (BSFCommonUtil.findInventoryItemStack(pPlayer, predicate) != null) {
                pPlayer.sendSystemMessage(Component.translatable("scoring_device_tp_failed1_1.tip"));
                return InteractionResult.FAIL;
            }
        }
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide()) {
            Vec3 color = new Vec3(0.9, 0.9, 0.9);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(livingEntity, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(livingEntity.getPosition(1).add(-0.5, 0, -0.5), livingEntity.getPosition(1).add(0.5, 1, 0.5), color, color.length(), color.length(), 5), BSFParticleType.SPAWN_SNOW.ordinal()));
        }
        livingEntity.playSound(SoundEvents.AMETHYST_BLOCK_RESONATE, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
    }


    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        Vec3 tpPoint = stack.getOrDefault(DataComponentRegister.TP_POINT.get(), livingEntity.getPosition(1));
        livingEntity.absSnapTo(tpPoint.x, tpPoint.y, tpPoint.z, livingEntity.getYRot(), livingEntity.getXRot());
        livingEntity.playSound(SoundRegister.FORCE_EXECUTOR_START.get(), 3.0F, 1.0F);
        livingEntity.removeAllEffects();
        if (stack.getOrDefault(DataComponentRegister.RANK.get(), 0) < 0) {
            stack.shrink(1);
        }
        return stack;
    }

    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 40;
    }

    public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack stack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int rank = stack.getOrDefault(DataComponentRegister.RANK.get(), 0);
        int money = stack.getOrDefault(DataComponentRegister.MONEY.get(), 0);
        int team = stack.getOrDefault(DataComponentRegister.TEAM.get(), (byte) 0);
        RegionData region = stack.getOrDefault(DataComponentRegister.REGION.get(), RegionData.EMPTY);
        tooltipComponents.accept(Component.translatable("scoring_device.tooltip"));
        tooltipComponents.accept(Component.translatable("scoring_device1.tooltip"));
        tooltipComponents.accept(Component.translatable("scoring_device_team.tooltip", BSFColorUtil.getColorTransNameById(team)));
        tooltipComponents.accept(Component.translatable("scoring_device_rank.tooltip", String.valueOf(rank)));
        tooltipComponents.accept(Component.translatable("scoring_device_money.tooltip", String.valueOf(money)));
        tooltipComponents.accept(Component.translatable(
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

