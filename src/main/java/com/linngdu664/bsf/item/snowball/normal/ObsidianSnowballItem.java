package com.linngdu664.bsf.item.snowball.normal;

import com.linngdu664.bsf.entity.snowball.AbstractBSFSnowballEntity;
import com.linngdu664.bsf.entity.snowball.nomal.ObsidianSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.item.snowball.AbstractBSFSnowballItem;
import com.linngdu664.bsf.item.weapon.SnowballCannonItem;
import com.linngdu664.bsf.item.weapon.SnowballMachineGunItem;
import com.linngdu664.bsf.item.weapon.SnowballShotgunItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ObsidianSnowballItem extends AbstractBSFSnowballItem {
    public ObsidianSnowballItem() {
        super(Rarity.COMMON, new SnowballProperties()
                .idForTank(7)
                .allowLaunchTypeFlag(AbstractBSFSnowballItem.HAND_TYPE_FLAG | SnowballCannonItem.TYPE_FLAG | SnowballShotgunItem.TYPE_FLAG | SnowballMachineGunItem.TYPE_FLAG)
                .machineGunRecoil(0.17)
                .shotgunPushRank(0.18)
        );
        DispenserBlock.registerProjectileBehavior(this);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        return throwOrStorage(pPlayer, pLevel, pUsedHand, 0.7F, 0);
    }

    @Override
    public AbstractBSFSnowballEntity getCorrespondingEntity(Level level, LivingEntity livingEntity, ILaunchAdjustment launchAdjustment) {
        return new ObsidianSnowballEntity(livingEntity, level, launchAdjustment);
    }

    @Override
    public @NotNull Projectile asProjectile(@NotNull Level level, @NotNull Position position, @NotNull ItemStack itemStack, @NotNull Direction direction) {
        ObsidianSnowballEntity snowball = new ObsidianSnowballEntity(level, position.x(), position.y(), position.z());
        snowball.setItem(itemStack);
        return snowball;
    }

//    @Override
//    public int getTypeFlag() {
//        return AbstractBSFSnowballItem.HAND_TYPE_FLAG | SnowballCannonItem.TYPE_FLAG | SnowballShotgunItem.TYPE_FLAG | SnowballMachineGunItem.TYPE_FLAG;
//    }
//
//    @Override
//    public double getMachineGunRecoil() {
//        return 0.17;
//    }
//
//    @Override
//    public double getShotgunPushRank() {
//        return 0.18;
//    }

    @Override
    public void addLastTips(List<Component> pTooltipComponents) {
        pTooltipComponents.add(MutableComponent.create(new TranslatableContents("obsidian_snowball.tooltip", null, new Object[0])).withStyle(ChatFormatting.GRAY));
    }
}
