package com.linngdu664.bsf.item.snowball;

import com.linngdu664.bsf.entity.snowball.AbstractBSFSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.entity.snowball.util.LaunchFrom;
import com.linngdu664.bsf.item.component.ItemData;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.item.tank.SnowballTankItem;
import com.linngdu664.bsf.item.weapon.SnowballCannonItem;
import com.linngdu664.bsf.item.weapon.SnowballMachineGunItem;
import com.linngdu664.bsf.item.weapon.SnowballShotgunItem;
import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public abstract class AbstractBSFSnowballItem extends Item {
    public static final int HAND_TYPE_FLAG = 1;
    private final SnowballProperties snowballProperties;

    public AbstractBSFSnowballItem(Rarity rarity, SnowballProperties snowballProperties) {
        super(new Properties().stacksTo(16).rarity(rarity));
        this.snowballProperties = snowballProperties;
    }

    public ILaunchAdjustment getLaunchAdjustment(float playerBadEffectRate) {
        return new ILaunchAdjustment() {
            @Override
            public double adjustPunch(double punch) {
                return punch;
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
                return damage * playerBadEffectRate;
            }

            @Override
            public float adjustBlazeDamage(float blazeDamage) {
                return blazeDamage * playerBadEffectRate;
            }

            @Override
            public LaunchFrom getLaunchFrom() {
                return LaunchFrom.HAND;
            }
        };
    }

    /**
     * Handle the storage of the snowballs.
     *
     * @param pPlayer The player who uses snowball.
     * @return If the method stores snowballs in the tank successfully, it will return true, else return false.
     */
    public boolean storageInTank(Player pPlayer) {
        ItemStack offhand = pPlayer.getOffhandItem();
        ItemStack mainHand = pPlayer.getMainHandItem();
        int count = mainHand.getCount();
        if (!(offhand.getItem() instanceof SnowballTankItem)) {
            return false;
        }
        Item item = offhand.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item();
        int offHandDamage = offhand.getDamageValue();
        int offHandMaxDamage = offhand.getMaxDamage();
        RegionData mainHandRegion = mainHand.get(DataComponentRegister.REGION);
        RegionData offHandRegion = offhand.get(DataComponentRegister.REGION);
        if (!(this.equals(item) && offHandDamage != 0 && Objects.equals(mainHandRegion, offHandRegion)) && offHandDamage != offHandMaxDamage) {
            return false;
        }
        if (offHandDamage == offHandMaxDamage) {
            offhand.set(DataComponentRegister.AMMO_ITEM, new ItemData(this));
            if (mainHandRegion != null) {
                offhand.set(DataComponentRegister.REGION, mainHandRegion);
            } else {
                offhand.remove(DataComponentRegister.REGION);
            }
        }
        if (offHandDamage < count) {
            if (!pPlayer.getAbilities().instabuild) {
                mainHand.shrink(offHandDamage);
            }
            offhand.setDamageValue(0);
        } else {
            if (!pPlayer.getAbilities().instabuild) {
                mainHand.shrink(count);
            }
            offhand.setDamageValue(offHandDamage - count);
        }
        return true;
    }

    public InteractionResultHolder<ItemStack> throwOrStorage(Player pPlayer, Level pLevel, InteractionHand pUsedHand, float velocity, int coolDown) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (!storageInTank(pPlayer)) {
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!pLevel.isClientSide) {
                AbstractBSFSnowballEntity snowballEntity = getCorrespondingEntity(pLevel, pPlayer, getLaunchAdjustment(getSnowballDamageRate(pPlayer)), itemStack.get(DataComponentRegister.REGION.get()));
                snowballEntity.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, velocity * getSnowballSlowdownRate(pPlayer), 1.0F);
                pLevel.addFreshEntity(snowballEntity);
            }
            if (!pPlayer.getAbilities().instabuild) {
                itemStack.shrink(1);
                if (coolDown != 0) {
                    pPlayer.getCooldowns().addCooldown(this, coolDown);
                }
            }
        }
        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
    }

    // 1.005^(-ticks)
    public float getSnowballSlowdownRate(Player player) {
        return (float) Math.exp(-0.005 * player.getTicksFrozen());
    }

    public float getSnowballDamageRate(Player player) {
        float reDamageRate = 1;
        if (player.hasEffect(MobEffects.WEAKNESS)) {
            reDamageRate -= switch (player.getEffect(MobEffects.WEAKNESS).getAmplifier()) {
                case 0 -> 0.25f;
                case 1 -> 0.5f;
                default -> 0.75f;
            };
        }
        if (player.hasEffect(MobEffects.DAMAGE_BOOST)) {
            if (player.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier() == 0) {
                reDamageRate += 0.15F;
            } else {
                reDamageRate += 0.3F;
            }
        }
        return reDamageRate;
    }

    public void generateWeaponTips(List<Component> pTooltipComponents) {
        int typeFlag = getTypeFlag();
        if ((typeFlag & HAND_TYPE_FLAG) == 0) {
            pTooltipComponents.add(Component.translatable("lunch_no_hand.tooltip").withStyle(ChatFormatting.DARK_RED));
        } else {
            pTooltipComponents.add(Component.translatable("lunch_yes_hand.tooltip").withStyle(ChatFormatting.DARK_GREEN));
        }
        if ((typeFlag & SnowballCannonItem.TYPE_FLAG) == 0) {
            pTooltipComponents.add(Component.translatable("lunch_no_cannon.tooltip").withStyle(ChatFormatting.DARK_RED));
        } else {
            pTooltipComponents.add(Component.translatable("lunch_yes_cannon.tooltip").withStyle(ChatFormatting.DARK_GREEN));
        }
        if ((typeFlag & SnowballMachineGunItem.TYPE_FLAG) == 0) {
            pTooltipComponents.add(Component.translatable("lunch_no_machine_gun.tooltip").withStyle(ChatFormatting.DARK_RED));
        } else {
            pTooltipComponents.add(Component.translatable("lunch_yes_machine_gun.tooltip").withStyle(ChatFormatting.DARK_GREEN));
        }
        if ((typeFlag & SnowballShotgunItem.TYPE_FLAG) == 0) {
            pTooltipComponents.add(Component.translatable("lunch_no_shotgun.tooltip").withStyle(ChatFormatting.DARK_RED));
        } else {
            pTooltipComponents.add(Component.translatable("lunch_yes_shotgun.tooltip").withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    public void addUsageTips(List<Component> pTooltipComponents) {
    }

    public void addMainTips(List<Component> pTooltipComponents) {
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        generateWeaponTips(tooltipComponents);
        addMainTips(tooltipComponents);
        addUsageTips(tooltipComponents);
    }

    /**
     * You must override this fucking method if you want to launch the snowball by weapons.
     *
     * @param level        Level.
     * @param livingEntity The entity who throws/launches the snowball.
     * @return The corresponding entity.
     */
    public AbstractBSFSnowballEntity getCorrespondingEntity(Level level, LivingEntity livingEntity, ILaunchAdjustment launchAdjustment, RegionData region) {
        return null;
    }

    public final int getIdForTank() {
        return snowballProperties.idForTank;
    }

    public final int getTypeFlag() {
        return snowballProperties.allowLaunchTypeFlag;
    }

    public final double getMachineGunRecoil() {
        return snowballProperties.machineGunRecoil;
//        return 0.075;
    }

    public final double getShotgunPushRank() {
        return snowballProperties.shotgunPushRank;
//        return 0.1;
    }

    public static class SnowballProperties {
        int idForTank;
        int allowLaunchTypeFlag;
        double machineGunRecoil;
        double shotgunPushRank;

        public SnowballProperties() {
            idForTank = -1;
            allowLaunchTypeFlag = 0;
            machineGunRecoil = 0.075;
            shotgunPushRank = 0.1;
        }

        public SnowballProperties idForTank(int idForTank) {
            this.idForTank = idForTank;
            return this;
        }

        public SnowballProperties allowLaunchTypeFlag(int allowLaunchTypeFlag) {
            this.allowLaunchTypeFlag = allowLaunchTypeFlag;
            return this;
        }

        public SnowballProperties machineGunRecoil(double machineGunRecoil) {
            this.machineGunRecoil = machineGunRecoil;
            return this;
        }

        public SnowballProperties shotgunPushRank(double shotgunPushRank) {
            this.shotgunPushRank = shotgunPushRank;
            return this;
        }
    }
}
