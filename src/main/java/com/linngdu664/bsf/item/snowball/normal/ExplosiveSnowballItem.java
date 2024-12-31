package com.linngdu664.bsf.item.snowball.normal;

import com.linngdu664.bsf.entity.snowball.AbstractBSFSnowballEntity;
import com.linngdu664.bsf.entity.snowball.nomal.ExplosiveSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.item.snowball.AbstractBSFSnowballItem;
import com.linngdu664.bsf.item.weapon.SnowballCannonItem;
import com.linngdu664.bsf.item.weapon.SnowballMachineGunItem;
import com.linngdu664.bsf.item.weapon.SnowballShotgunItem;
import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExplosiveSnowballItem extends AbstractBSFSnowballItem implements ProjectileItem {
    public ExplosiveSnowballItem() {
        super(Rarity.UNCOMMON, new SnowballProperties()
                .idForTank(8)
                .allowLaunchTypeFlag(AbstractBSFSnowballItem.HAND_TYPE_FLAG | SnowballCannonItem.TYPE_FLAG | SnowballShotgunItem.TYPE_FLAG | SnowballMachineGunItem.TYPE_FLAG)
                .machineGunRecoil(0.12)
                .shotgunPushRank(0.42)
        );
        DispenserBlock.registerProjectileBehavior(this);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        return throwOrStorage(pPlayer, pLevel, pUsedHand, 0.9F, 0);
    }

    @Override
    public AbstractBSFSnowballEntity getCorrespondingEntity(Level level, LivingEntity livingEntity, ILaunchAdjustment launchAdjustment, RegionData region) {
        return new ExplosiveSnowballEntity(livingEntity, level, launchAdjustment, region);
    }

    @Override
    public @NotNull Projectile asProjectile(@NotNull Level level, @NotNull Position position, @NotNull ItemStack itemStack, @NotNull Direction direction) {
        ExplosiveSnowballEntity snowball = new ExplosiveSnowballEntity(level, position.x(), position.y(), position.z(), itemStack.get(DataComponentRegister.REGION));
        snowball.setItem(itemStack);
        return snowball;
    }

    @Override
    public void addMainTips(List<Component> pTooltipComponents) {
        pTooltipComponents.add(Component.translatable("explosive_snowball.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
