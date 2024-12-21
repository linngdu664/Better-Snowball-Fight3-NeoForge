package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EntityRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SnowGolemContainer extends Item {
    public SnowGolemContainer() {
        super(new Properties().rarity(Rarity.UNCOMMON).stacksTo(1));
    }

    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        ItemStack itemStack = pContext.getItemInHand();
        Player player = pContext.getPlayer();
        if (itemStack.has(DataComponentRegister.SNOW_GOLEM_DATA)) {
            if (!level.isClientSide) {
                BSFSnowGolemEntity snowGolem = EntityRegister.BSF_SNOW_GOLEM.get().create(level);
                snowGolem.load(itemStack.get(DataComponentRegister.SNOW_GOLEM_DATA));
                BlockPos blockPos = pContext.getClickedPos();
                snowGolem.moveTo(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, 0.0F, 0.0F);
                snowGolem.setOwnerUUID(player.getUUID());
                level.addFreshEntity(snowGolem);
                itemStack.remove(DataComponentRegister.SNOW_GOLEM_DATA);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOW_PLACE, SoundSource.NEUTRAL, 1.0F, 1.0F);
                player.awardStat(Stats.ITEM_USED.get(this));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("snow_golem_container.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
