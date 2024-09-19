package com.linngdu664.bsf.item.weapon;

import com.linngdu664.bsf.client.screenshake.Easing;
import com.linngdu664.bsf.client.screenshake.ScreenshakeHandler;
import com.linngdu664.bsf.client.screenshake.ScreenshakeInstance;
import com.linngdu664.bsf.entity.snowball.AbstractBSFSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.entity.snowball.util.LaunchFrom;
import com.linngdu664.bsf.item.component.ItemData;
import com.linngdu664.bsf.item.snowball.AbstractBSFSnowballItem;
import com.linngdu664.bsf.item.tank.SnowballTankItem;
import com.linngdu664.bsf.network.to_client.ForwardConeParticlesPayload;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.particle.util.ForwardConeParticlesParas;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EffectRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import com.linngdu664.bsf.registry.SoundRegister;
import com.linngdu664.bsf.util.BSFEnchantmentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;

import static com.linngdu664.bsf.event.ClientModEvents.CYCLE_MOVE_AMMO_NEXT;
import static com.linngdu664.bsf.event.ClientModEvents.CYCLE_MOVE_AMMO_PREV;

public class SnowballShotgunItem extends AbstractBSFWeaponItem {
    public static final int TYPE_FLAG = 8;
    private boolean isShiftHold = false;
    private boolean isChangeToThrust = false;

    public SnowballShotgunItem() {
        super(1145, Rarity.EPIC, TYPE_FLAG);
    }

    @Override
    public ILaunchAdjustment getLaunchAdjustment(double damageDropRate, Item snowball) {
        return new ILaunchAdjustment() {
            @Override
            public double adjustPunch(double punch) {
                return punch + 1.51;
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
                return LaunchFrom.SHOTGUN;
            }
        };
    }

    @Override
    public boolean isAllowBulkedSnowball() {
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        int enchantmentLevel = EnchantmentHelper.getTagEnchantmentLevel(BSFEnchantmentHelper.getEnchantmentHolder(player, BSFEnchantmentHelper.SNOW_GOLEM_EXCLUSIVE), stack);
        if (enchantmentLevel > 0 || player.hasEffect(EffectRegister.WEAPON_JAM)) {
            return InteractionResultHolder.fail(stack);
        }
        double pushRank = 0.24;
        // add push or summon projectile
        int i;
        for (i = 0; i < 4; i++) {
            ItemStack itemStack = getAmmo(player, stack);
            if (itemStack == null || !player.isShiftKeyDown() && (itemStack.getItem().equals(ItemRegister.THRUST_SNOWBALL.get()) || itemStack.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item().equals(ItemRegister.THRUST_SNOWBALL.get()))) {
                break;
            }
            if (!player.isShiftKeyDown()) {
                AbstractBSFSnowballEntity snowballEntity = ItemToEntity(itemStack, player, level, getLaunchAdjustment(1, itemStack.getItem()));
                BSFShootFromRotation(snowballEntity, player.getXRot(), player.getYRot(), 2.0F, 10.0F);
                level.addFreshEntity(snowballEntity);
            } else {
                Item item = itemStack.getItem();
                if (item instanceof SnowballTankItem) {
                    item = itemStack.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item();
                }
                pushRank += ((AbstractBSFSnowballItem) item).getShotgunPushRank();
            }
            consumeAmmo(itemStack, player);
        }
        if (i == 0) {
            return InteractionResultHolder.pass(stack);
        }
        // finally push player
        Vec3 cameraVec = Vec3.directionFromRotation(player.getXRot(), player.getYRot());
        if (level.isClientSide) {
            player.push(-pushRank * cameraVec.x, -pushRank * cameraVec.y, -pushRank * cameraVec.z);
            ScreenshakeHandler.addScreenshake((new ScreenshakeInstance(3)).setIntensity(0.8f).setEasing(Easing.ELASTIC_IN));
        } else {
            if (player.isShiftKeyDown()) {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new ForwardConeParticlesPayload(new ForwardConeParticlesParas(player.getEyePosition(), cameraVec, 4.5F, 45, 0.5F, 0.1), BSFParticleType.SNOWFLAKE.ordinal()));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegister.SHOTGUN_FIRE_1.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            } else {
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new ForwardConeParticlesPayload(new ForwardConeParticlesParas(player.getEyePosition(), cameraVec, 4.5F, 45, 1.5F, 0.1), BSFParticleType.SNOWFLAKE.ordinal()));
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegister.SHOTGUN_FIRE_2.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
            }
        }
        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        player.getCooldowns().addCooldown(this, player.isShiftKeyDown() ? 30 : 20);
        player.awardStat(Stats.ITEM_USED.get(this));
//            Vec3 cameraVec = Vec3.directionFromRotation(player.getXRot(), player.getYRot());
//            if (!player.isShiftKeyDown()) {
//                if (level.isClientSide) {
//                    player.push(-0.24 * cameraVec.x, -0.24 * cameraVec.y, -0.24 * cameraVec.z);
//                } else {
//                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new ForwardConeParticlesPayload(new ForwardConeParticlesParas(player.getEyePosition(), cameraVec, 4.5F, 45, 1.5F, 0.1), BSFParticleType.SNOWFLAKE.ordinal()));
//                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegister.SHOTGUN_FIRE_2.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
//                }
//                player.getCooldowns().addCooldown(this, 20);
//            } else {
//                if (level.isClientSide) {
//                    player.push(-pushRank * cameraVec.x, -pushRank * cameraVec.y, -pushRank * cameraVec.z);
//                } else {
//                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new ForwardConeParticlesPayload(new ForwardConeParticlesParas(player.getEyePosition(), cameraVec, 4.5F, 45, 0.5F, 0.1), BSFParticleType.SNOWFLAKE.ordinal()));
//                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegister.SHOTGUN_FIRE_1.get(), SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
//                }
//                player.getCooldowns().addCooldown(this, 30);
//            }
//            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
//            player.awardStat(Stats.ITEM_USED.get(this));
//            if (level.isClientSide) {
//                ScreenshakeHandler.addScreenshake((new ScreenshakeInstance(3)).setIntensity(0.8f).setEasing(Easing.ELASTIC_IN));
//            }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    protected void modifyOrder(Player player, LinkedHashSet<Item> launchOrder) {
        if (player.isShiftKeyDown()) {
            if (isShiftHold) {
                return;
            }
            // try switching to thrust
            if (launchOrder.contains(ItemRegister.THRUST_SNOWBALL.get()) && !launchOrder.getFirst().equals(ItemRegister.THRUST_SNOWBALL.get())) {
                launchOrder.remove(ItemRegister.THRUST_SNOWBALL.get());
                launchOrder.addFirst(ItemRegister.THRUST_SNOWBALL.get());
                isChangeToThrust = true;
            } else {
                isChangeToThrust = false;
            }
            isShiftHold = true;
        } else {
            if (!isShiftHold) {
                return;
            }
            // try recovering
            if (isChangeToThrust && launchOrder.getFirst().equals(ItemRegister.THRUST_SNOWBALL.get())) {
                launchOrder.removeFirst();
                launchOrder.addLast(ItemRegister.THRUST_SNOWBALL.get());
            }
            isShiftHold = false;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_shotgun1.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_shotgun2.tooltip", null, new Object[]{Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage()})).withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("snowball_shotgun3.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("guns1.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("guns2.tooltip", null, new Object[]{CYCLE_MOVE_AMMO_PREV.getTranslatedKeyMessage(), CYCLE_MOVE_AMMO_NEXT.getTranslatedKeyMessage()})).withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return HumanoidModel.ArmPose.valueOf("BSF_WEAPON");
            }
        });
    }
}
