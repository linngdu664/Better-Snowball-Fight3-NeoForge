package com.linngdu664.bsf.item.weapon;

import com.linngdu664.bsf.entity.snowball.AbstractBSFSnowballEntity;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.item.component.ItemData;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.item.snowball.AbstractBSFSnowballItem;
import com.linngdu664.bsf.item.tank.SnowballTankItem;
import com.linngdu664.bsf.network.to_server.AmmoTypePayload;
import com.linngdu664.bsf.registry.DataComponentRegister;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

public abstract class AbstractBSFWeaponItem extends Item {
    private final int typeFlag;
    private final LinkedHashSet<Item> launchOrder = new LinkedHashSet<>();   // client only
    private ItemStack prevAmmoItemStack = Items.AIR.getDefaultInstance();      // client only
    private ItemStack currentAmmoItemStack = Items.AIR.getDefaultInstance();   // client only
    private ItemStack nextAmmoItemStack = Items.AIR.getDefaultInstance();      // client only

    public AbstractBSFWeaponItem(int durability, Rarity rarity, int flag) {
        super(new Properties().stacksTo(1).durability(durability).rarity(rarity));
        this.typeFlag = flag;
    }

    public abstract ILaunchAdjustment getLaunchAdjustment(double damageDropRate, Item snowball);

    public abstract boolean isAllowBulkedSnowball();

    //Rewrite vanilla "shootFromRotation" method to remove the influence of player's velocity.
    protected void BSFShootFromRotation(Projectile projectile, float pX, float pY, float pVelocity, float pInaccuracy) {
        float f = -Mth.sin(pY * Mth.DEG_TO_RAD) * Mth.cos(pX * Mth.DEG_TO_RAD);
        float f1 = -Mth.sin(pX * Mth.DEG_TO_RAD);
        float f2 = Mth.cos(pY * Mth.DEG_TO_RAD) * Mth.cos(pX * Mth.DEG_TO_RAD);
        projectile.shoot(f, f1, f2, pVelocity, pInaccuracy);
    }

    protected void consumeAmmo(ItemStack itemStack, Player player) {
        if (!player.getAbilities().instabuild) {
            if (itemStack.getItem() instanceof SnowballTankItem) {
                if (!itemStack.has(DataComponents.UNBREAKABLE)) {
                    itemStack.setDamageValue(itemStack.getDamageValue() + 1);
                    if (itemStack.getDamageValue() == itemStack.getMaxDamage()) {
                        itemStack.remove(DataComponentRegister.AMMO_ITEM);
                    }
                }
            } else {
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    player.getInventory().removeItem(itemStack);
                }
            }
        }
    }

    protected AbstractBSFSnowballEntity ItemToEntity(ItemStack itemStack, Player player, Level level, ILaunchAdjustment launchAdjustment) {
        Item item = itemStack.getItem();
        RegionData region = itemStack.get(DataComponentRegister.REGION.get());
        if (item instanceof SnowballTankItem) {
            item = itemStack.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item();
        }
        if (item instanceof AbstractBSFSnowballItem snowball) {
            return snowball.getCorrespondingEntity(level, player, launchAdjustment, region);
        }
        return null;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack pStack, ItemStack pRepairCandidate) {
        return pRepairCandidate.is(Items.IRON_INGOT);
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return 25;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pLevel.isClientSide && pEntity instanceof Player player && (this.equals(player.getMainHandItem().getItem()) || this.equals(player.getOffhandItem().getItem()))) {
            Inventory inventory = player.getInventory();
            int k = inventory.getContainerSize();
            HashMap<Item, Integer> hashMap = new HashMap<>();
            for (int i = 0; i < k; i++) {
                ItemStack itemStack = inventory.getItem(i);
                Item item = itemStack.getItem();
                if (item instanceof SnowballTankItem && itemStack.has(DataComponentRegister.AMMO_ITEM)) {
                    AbstractBSFSnowballItem snowball = (AbstractBSFSnowballItem) itemStack.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item();
                    if ((typeFlag & snowball.getTypeFlag()) != 0) {
                        hashMap.put(snowball, hashMap.getOrDefault(snowball, 0) + itemStack.getMaxDamage() - itemStack.getDamageValue());
                    }
                } else if (isAllowBulkedSnowball() && item instanceof AbstractBSFSnowballItem snowball && (typeFlag & snowball.getTypeFlag()) != 0) {
                    hashMap.put(snowball, hashMap.getOrDefault(snowball, 0) + itemStack.getCount());
                }
            }
            launchOrder.addAll(hashMap.keySet());
            launchOrder.removeIf(item -> !hashMap.containsKey(item));
            modifyOrder(player, launchOrder);
            if (launchOrder.isEmpty()) {
                prevAmmoItemStack = Items.AIR.getDefaultInstance();
                currentAmmoItemStack = Items.AIR.getDefaultInstance();
                nextAmmoItemStack = Items.AIR.getDefaultInstance();
            } else if (launchOrder.size() == 1) {
                prevAmmoItemStack = Items.AIR.getDefaultInstance();
                currentAmmoItemStack = new ItemStack(launchOrder.getFirst(), hashMap.get(launchOrder.getFirst()));
                nextAmmoItemStack = Items.AIR.getDefaultInstance();
            } else {
                prevAmmoItemStack = new ItemStack(launchOrder.getLast(), hashMap.get(launchOrder.getLast()));
                currentAmmoItemStack = new ItemStack(launchOrder.getFirst(), hashMap.get(launchOrder.getFirst()));
                Iterator<Item> iterator = launchOrder.iterator();
                iterator.next();
                Item nextItem = iterator.next();
                nextAmmoItemStack = new ItemStack(nextItem, hashMap.get(nextItem));
            }
            Item newItem = currentAmmoItemStack.getItem();
            if (!newItem.equals(pStack.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item())) {
                PacketDistributor.sendToServer(new AmmoTypePayload(newItem, pSlotId));
            }
        }
    }

    protected void modifyOrder(Player player, LinkedHashSet<Item> launchOrder) {

    }

    public ItemStack getAmmo(Player player, ItemStack weaponItemStack) {
        Item ammoItem = weaponItemStack.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item();
        if (ammoItem == Items.AIR) {
            return null;
        }
        Inventory inventory = player.getInventory();
        int k = inventory.getContainerSize();
        ItemStack ammoItemStack = null;
        for (int i = 0; i < k; i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack.getItem() instanceof SnowballTankItem && itemStack.getOrDefault(DataComponentRegister.AMMO_ITEM, ItemData.EMPTY).item().equals(ammoItem) && (ammoItemStack == null || ammoItemStack.getDamageValue() < itemStack.getDamageValue())) {
                ammoItemStack = itemStack;
            }
        }
        if (ammoItemStack != null) {
            return ammoItemStack;
        }
        if (isAllowBulkedSnowball()) {
            for (int i = 0; i < k; i++) {
                ItemStack itemStack = inventory.getItem(i);
                if (itemStack.getItem() instanceof AbstractBSFSnowballItem snowball && snowball.equals(ammoItem)) {
                    return itemStack;
                }
            }
        }
        return null;
    }

    public LinkedHashSet<Item> getLaunchOrder() {
        return launchOrder;
    }

    public ItemStack getPrevAmmoItemStack() {
        return prevAmmoItemStack;
    }

    public ItemStack getCurrentAmmoItemStack() {
        return currentAmmoItemStack;
    }

    public ItemStack getNextAmmoItemStack() {
        return nextAmmoItemStack;
    }

    public int getTypeFlag() {
        return typeFlag;
    }
}
