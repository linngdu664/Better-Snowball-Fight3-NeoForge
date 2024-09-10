package com.linngdu664.bsf.block.entity;

import com.linngdu664.bsf.registry.BlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class VendingMachineEntity extends BlockEntity {
    // all these fields are sync to client
    private ItemStack goods = Items.AIR.getDefaultInstance();
    private int minRank;
    private int price;
    private boolean canSell;

    public VendingMachineEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegister.VENDING_MACHINE_ENTITY.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        goods = ItemStack.parseOptional(registries, tag.getCompound("Goods"));
        minRank = tag.getInt("MinRank");
        price = tag.getInt("Price");
        canSell = tag.getBoolean("CanSell");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Goods", goods.saveOptional(registries));
        tag.putInt("MinRank", minRank);
        tag.putInt("Price", price);
        tag.putBoolean("CanSell", canSell);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.put("Goods", goods.saveOptional(registries));
        tag.putInt("MinRank", minRank);
        tag.putInt("Price", price);
        tag.putBoolean("CanSell", canSell);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        goods = ItemStack.parseOptional(lookupProvider, tag.getCompound("Goods"));
        minRank = tag.getInt("MinRank");
        price = tag.getInt("Price");
        canSell = tag.getBoolean("CanSell");
    }

    public ItemStack getGoods() {
        return goods.copy();
    }

    public void setGoods(ItemStack goods) {
        this.goods = goods.copy();
        setChanged();
    }

    public int getMinRank() {
        return minRank;
    }

    public void setMinRank(int minRank) {
        this.minRank = minRank;
        setChanged();
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
        setChanged();
    }

    public boolean isCanSell() {
        return canSell;
    }

    public void setCanSell(boolean canSell) {
        this.canSell = canSell;
        setChanged();
    }
}
