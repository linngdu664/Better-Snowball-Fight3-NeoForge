package com.linngdu664.bsf.item.weapon;

import com.linngdu664.bsf.entity.snowball.AbstractBSFSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.entity.snowball.util.LaunchFrom;
import com.linngdu664.bsf.item.component.ItemData;
import com.linngdu664.bsf.item.snowball.AbstractBSFSnowballItem;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EffectRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import com.linngdu664.bsf.registry.SoundRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.linngdu664.bsf.event.ClientModEvents.CYCLE_MOVE_AMMO_NEXT;
import static com.linngdu664.bsf.event.ClientModEvents.CYCLE_MOVE_AMMO_PREV;

public class SnowballMachineGunItem extends AbstractBSFWeaponItem {
    public static final int TYPE_FLAG = 4;
    private double recoil;
    private ItemStack ammo;
    private boolean isExplosive;

    public SnowballMachineGunItem() {
        super(1919, Rarity.EPIC, TYPE_FLAG);
    }

    @Override
    public ILaunchAdjustment getLaunchAdjustment(double damageDropRate, Item snowball) {
        return new ILaunchAdjustment() {
            @Override
            public double adjustPunch(double punch) {
                return punch + 1.2;
            }

            @Override
            public int adjustWeaknessTicks(int weaknessTicks) {
                return weaknessTicks;
            }

            @Override
            public int adjustFrozenTicks(int frozenTicks) {
                return frozenTicks;
            }

            @Override
            public float adjustDamage(float damage) {
                return damage;
            }

            @Override
            public float adjustBlazeDamage(float blazeDamage) {
                return blazeDamage;
            }

            @Override
            public LaunchFrom getLaunchFrom() {
                return LaunchFrom.MACHINE_GUN;
            }
        };
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        ammo = getAmmo(pPlayer, stack);
        if (ammo != null && !pPlayer.hasEffect(EffectRegister.WEAPON_JAM) && !stack.getOrDefault(DataComponentRegister.MACHINE_GUN_IS_COOL_DOWN, false)) {
            Item ammoItem = ammo.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item();
            recoil = ((AbstractBSFSnowballItem) ammoItem).getMachineGunRecoil();
            isExplosive = ammoItem.equals(ItemRegister.EXPLOSIVE_SNOWBALL.get()) || ammoItem.equals(ItemRegister.EXPLOSIVE_MONSTER_TRACKING_SNOWBALL.get()) || ammoItem.equals(ItemRegister.EXPLOSIVE_PLAYER_TRACKING_SNOWBALL.get());
            int timer = stack.getOrDefault(DataComponentRegister.MACHINE_GUN_TIMER, 0);
            if (isExplosive) {
                stack.set(DataComponentRegister.MACHINE_GUN_TIMER, (timer / 6 + 1) * 6);     // ceil to multiple of 6
            } else {
                stack.set(DataComponentRegister.MACHINE_GUN_TIMER, (timer / 3 + 1) * 3);     // ceil to multiple of 3
            }
            pPlayer.startUsingItem(pUsedHand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void onUseTick(@NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, @NotNull ItemStack pStack, int pRemainingUseDuration) {
        if (pLivingEntity instanceof Player player) {
            int timer = pStack.getOrDefault(DataComponentRegister.MACHINE_GUN_TIMER, 0);
            if (timer >= 360) {
                player.playSound(SoundRegister.MACHINE_GUN_COOLING.get(), 3.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                pStack.set(DataComponentRegister.MACHINE_GUN_IS_COOL_DOWN, true);
                this.releaseUsing(pStack, pLevel, player, pRemainingUseDuration);
                return;
            } else if (ammo == null || ammo.isEmpty() || !ammo.has(DataComponentRegister.AMMO_ITEM) || player.hasEffect(EffectRegister.WEAPON_JAM)) {
                this.releaseUsing(pStack, pLevel, player, pRemainingUseDuration);
                return;
            }
            float pitch = player.getXRot();
            float yaw = player.getYRot();
            if (timer % 9 == 0 && (!isExplosive || timer % 36 == 0)) {
                Vec3 cameraVec = Vec3.directionFromRotation(pitch, yaw);
                if (pLevel.isClientSide()) {
                    // add push
                    player.push(-cameraVec.x * recoil * 0.25, -cameraVec.y * recoil * 0.25, -cameraVec.z * recoil * 0.25);
                } else {
                    AbstractBSFSnowballEntity snowballEntity = ItemToEntity(ammo, player, pLevel, getLaunchAdjustment(1, ammo.getItem()));
                    BSFShootFromRotation(snowballEntity, pitch, yaw, 2.6F, 1.0F);
                    pLevel.addFreshEntity(snowballEntity);
                    pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegister.SNOWBALL_MACHINE_GUN_SHOOT.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                    // add particles
                    ((ServerLevel) pLevel).sendParticles(ParticleTypes.SNOWFLAKE, player.getX() + cameraVec.x, player.getEyeY() + cameraVec.y, player.getZ() + cameraVec.z, 4, 0, 0, 0, 0.32);
                    // handle ammo consume and damage weapon.
                    consumeAmmo(ammo, player);
                    pStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                }
            }
            // set pitch according to recoil.
            if (pitch > -90.0F && pLevel.isClientSide() && (!isExplosive || timer % 36 < 18)) {
                player.setXRot(pitch - (float) recoil);
            }
            pStack.set(DataComponentRegister.MACHINE_GUN_TIMER, isExplosive ? timer + 6 : timer + 3);
        }
    }

    @Override
    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, int pTimeCharged) {
        if (pLivingEntity instanceof Player player) {
            player.stopUsingItem();
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (pEntity instanceof Player player && !pStack.equals(player.getUseItem())) {
            int timer = pStack.getOrDefault(DataComponentRegister.MACHINE_GUN_TIMER, 0);
            if (timer > 0) {
                if (timer > 2) {
                    pStack.set(DataComponentRegister.MACHINE_GUN_TIMER, timer - 2);
                } else {
                    pStack.set(DataComponentRegister.MACHINE_GUN_TIMER, 0);
                    pStack.set(DataComponentRegister.MACHINE_GUN_IS_COOL_DOWN, false);
                }
            }
        }
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public boolean isAllowBulkedSnowball() {
        return false;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack pStack, LivingEntity livingEntity) {
        return 72000;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.getItem().equals(newStack.getItem());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_machine_gun1.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_machine_gun2.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("guns2.tooltip", null, new Object[]{CYCLE_MOVE_AMMO_PREV.getTranslatedKeyMessage(), CYCLE_MOVE_AMMO_NEXT.getTranslatedKeyMessage()})).withStyle(ChatFormatting.DARK_GRAY));
    }
}
