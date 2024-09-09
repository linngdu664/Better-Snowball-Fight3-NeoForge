package com.linngdu664.bsf.item.weapon;

import com.linngdu664.bsf.entity.snowball.special.SculkSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EffectRegister;
import com.linngdu664.bsf.registry.SoundRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import static com.linngdu664.bsf.event.ClientModEvents.CYCLE_MOVE_AMMO_NEXT;
import static com.linngdu664.bsf.event.ClientModEvents.CYCLE_MOVE_AMMO_PREV;

public class SculkSnowballLauncherItem extends AbstractBSFWeaponItem {
    public static final int TYPE_FLAG = 16;

    public SculkSnowballLauncherItem() {
        super(8192, Rarity.UNCOMMON, TYPE_FLAG);
    }

    @Override
    public ILaunchAdjustment getLaunchAdjustment(double damageDropRate, Item snowball) {
        return null;
    }

    @Override
    public boolean isAllowBulkedSnowball() {
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer.hasEffect(EffectRegister.WEAPON_JAM)) {
            return InteractionResultHolder.fail(itemStack);
        }
        if (!pLevel.isClientSide) {
            ItemStack stack = getAmmo(pPlayer, itemStack);
            if (stack != null || pPlayer.isCreative()) {
                pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundRegister.SNOWBALL_CANNON_SHOOT.get(), SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
                if (!itemStack.has(DataComponentRegister.SCULK_SOUND_ID)) {
                    itemStack.set(DataComponentRegister.SCULK_SOUND_ID, -1);
                    itemStack.set(DataComponents.CUSTOM_NAME, MutableComponent.create(new TranslatableContents("item.bsf.sculk_snowball_launcher", null, new Object[]{}))
                            .append(": ").append(MutableComponent.create(new TranslatableContents("random_sound.tip", null, new Object[]{}))));
                }
                SculkSnowballEntity snowballEntity = new SculkSnowballEntity(pPlayer, pLevel, itemStack.getOrDefault(DataComponentRegister.SCULK_SOUND_ID, -1));
                snowballEntity.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 2.0F, 1.0F);
                pLevel.addFreshEntity(snowballEntity);
                itemStack.hurtAndBreak(1, pPlayer, LivingEntity.getSlotForHand(pUsedHand));
                if (stack != null) {
                    consumeAmmo(stack, pPlayer);
                }
                pPlayer.awardStat(Stats.ITEM_USED.get(this));
            }
        }
        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.getItem().equals(newStack.getItem());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("sculk_snowball_launcher.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("sculk_snowball_launcher1.tooltip", null, new Object[]{Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage()})).withStyle(ChatFormatting.DARK_GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("guns1.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(MutableComponent.create(new TranslatableContents("guns2.tooltip", null, new Object[]{CYCLE_MOVE_AMMO_PREV.getTranslatedKeyMessage(),CYCLE_MOVE_AMMO_NEXT.getTranslatedKeyMessage()})).withStyle(ChatFormatting.DARK_GRAY));
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
