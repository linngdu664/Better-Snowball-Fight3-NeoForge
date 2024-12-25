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
    private BlockPos controllerBlockPos;
    // below are all client side
    private int timer;
    private float currentStrength;
    private int playerNum;
    private byte teamId;

    public RegionControllerViewBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegister.REGION_CONTROLLER_VIEW_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (!level.isClientSide || !(blockEntity instanceof RegionControllerViewBlockEntity be) || be.controllerBlockPos == null) {
            return;
        }
        if (be.timer < 20) {
            be.timer++;
            return;
        }
        if (level.getBlockEntity(be.controllerBlockPos) instanceof RegionControllerBlockEntity controllerBE) {
            be.currentStrength = controllerBE.getCurrentStrength();
            be.playerNum = controllerBE.getPlayerNum();
            be.teamId = controllerBE.getTeamId();
        }
        be.timer = 0;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        controllerBlockPos = BlockPos.of(tag.getLong("Bind"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (controllerBlockPos != null) {
            tag.putLong("Bind", controllerBlockPos.asLong());
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        // send these data to client
        CompoundTag tag = super.getUpdateTag(registries);
        if (controllerBlockPos != null) {
            tag.putLong("Bind", controllerBlockPos.asLong());
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        // in client these fields are valid
        super.handleUpdateTag(tag, lookupProvider);
        controllerBlockPos = BlockPos.of(tag.getLong("Bind"));
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void setControllerBlockPos(BlockPos blockPos) {
        this.controllerBlockPos = blockPos;
        setChanged();
    }

    public BlockPos getControllerBlockPos() {
        return controllerBlockPos;
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
