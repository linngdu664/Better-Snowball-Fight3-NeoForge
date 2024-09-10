package com.linngdu664.bsf.item.tool;

import com.linngdu664.bsf.block.entity.VendingMachineEntity;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class ScoringDevice extends Item {
    public ScoringDevice(Properties properties) {
        super(properties.stacksTo(1).component(DataComponentRegister.RANK.get(), 0).component(DataComponentRegister.MONEY.get(), 0));
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
                        List<ItemStack> stacks = BSFCommonUtil.findInventoryItemStacks(player, goods.getItem());
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
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int rank = stack.getOrDefault(DataComponentRegister.RANK.get(), 0);
        int money = stack.getOrDefault(DataComponentRegister.MONEY.get(), 0);
        tooltipComponents.add(Component.translatable("scoring_device_rank.tooltip", String.valueOf(rank)));
        tooltipComponents.add(Component.translatable("scoring_device_money.tooltip", String.valueOf(money)));
    }
}
