package com.linngdu664.bsf.block.entity;

import com.linngdu664.bsf.registry.BlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class VendingMachineBlockEntity extends BlockEntity {
    // all these fields are sync to client
    private ItemStack goods = Items.AIR.getDefaultInstance();
    private int minRank;
    private int price;
    private boolean canSell;

    public VendingMachineBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegister.VENDING_MACHINE.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        goods = input.read("Goods", ItemStack.OPTIONAL_CODEC).orElse(Items.AIR.getDefaultInstance());
        minRank = input.getIntOr("MinRank", 0);
        price = input.getIntOr("Price", 0);
        canSell = input.getBooleanOr("CanSell", false);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("Goods", ItemStack.OPTIONAL_CODEC, goods);
        output.putInt("MinRank", minRank);
        output.putInt("Price", price);
        output.putBoolean("CanSell", canSell);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.store("Goods", ItemStack.OPTIONAL_CODEC, registries.createSerializationContext(NbtOps.INSTANCE), goods);
        tag.putInt("MinRank", minRank);
        tag.putInt("Price", price);
        tag.putBoolean("CanSell", canSell);
        return tag;
    }

    @Override
    public void handleUpdateTag(ValueInput tag) {
        super.handleUpdateTag(tag);
        goods = tag.read("Goods", ItemStack.OPTIONAL_CODEC).orElse(Items.AIR.getDefaultInstance());
        minRank = tag.getIntOr("MinRank", 0);
        price = tag.getIntOr("Price", 0);
        canSell = tag.getBooleanOr("CanSell", false);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ItemStack getGoods() {
        return goods.copy();
    }

    public void setGoods(ItemStack goods) {
        this.goods = goods.copy();
    }

    public int getMinRank() {
        return minRank;
    }

    public void setMinRank(int minRank) {
        this.minRank = minRank;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isCanSell() {
        return canSell;
    }

    public void setCanSell(boolean canSell) {
        this.canSell = canSell;
    }
}
