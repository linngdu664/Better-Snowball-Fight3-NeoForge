package com.linngdu664.bsf.block.entity;

import com.linngdu664.bsf.registry.BlockEntityRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RegionControllerViewBlockEntity extends BlockEntity {
    private RegionControllerBlockEntity controllerBE;
    // below are all client side
    private int timer;
    private float currentStrength;
    private int playerNum;
    private byte teamId;

    public RegionControllerViewBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegister.REGION_CONTROLLER_VIEW_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (!level.isClientSide || !(blockEntity instanceof RegionControllerViewBlockEntity be) || be.controllerBE == null) {
            return;
        }
        if (be.timer < 20) {
            be.timer++;
            return;
        }
        be.currentStrength = be.controllerBE.getCurrentStrength();
        be.playerNum = be.controllerBE.getPlayerNum();
        be.teamId = be.controllerBE.getTeamId();
        be.timer = 0;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (level.getBlockEntity(BlockPos.of(tag.getLong("Bind"))) instanceof RegionControllerBlockEntity be) {
            controllerBE = be;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong("Bind", controllerBE.getBlockPos().asLong());
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        // send these data to client
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putLong("Bind", controllerBE.getBlockPos().asLong());
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        // in client these fields are valid
        super.handleUpdateTag(tag, lookupProvider);
        if (level.getBlockEntity(BlockPos.of(tag.getLong("Bind"))) instanceof RegionControllerBlockEntity be) {
            controllerBE = be;
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void setControllerBE(BlockPos blockPos) {
        if (level.getBlockEntity(blockPos) instanceof RegionControllerBlockEntity be) {
            this.controllerBE = be;
            setChanged();
        }
    }

    public BlockPos getControllerBEBlockPos() {
        if (controllerBE == null) {
            return null;
        }
        return controllerBE.getBlockPos();
    }

    public float getCurrentStrength() {
        return currentStrength;
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public byte getTeamId() {
        return teamId;
    }
}
