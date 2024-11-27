package com.linngdu664.bsf.network.to_server;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.block.entity.RegionControllerBlockEntity;
import com.linngdu664.bsf.network.to_server.packed_paras.UpdateRegionControllerParas;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateRegionControllerPayload(UpdateRegionControllerParas paras) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateRegionControllerPayload> TYPE = new CustomPacketPayload.Type<>(Main.makeResLoc("update_region_controller"));
    public static final StreamCodec<ByteBuf, UpdateRegionControllerPayload> STREAM_CODEC = StreamCodec.composite(
            UpdateRegionControllerParas.STREAM_CODEC, UpdateRegionControllerPayload::paras,
            UpdateRegionControllerPayload::new
    );

    public static void handleDataInServer(UpdateRegionControllerPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Level level = player.level();
            UpdateRegionControllerParas paras = payload.paras();
            BlockPos blockPos = paras.blockPos();
            if (level.hasChunkAt(blockPos) && level.getBlockEntity(blockPos) instanceof RegionControllerBlockEntity be) {
                be.setSpawnBlock(BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(paras.spawnBlock())));
                be.setPlayerMultiplier(paras.playerMultiplier());
                be.setGolemMultiplier(paras.golemMultiplier());
                be.setDiversity(paras.diversity());
                be.setRankOffset(paras.rankOffset());
                be.setFastestStrength(paras.fastestStrength());
                be.setSlowestStrength(paras.slowestStrength());
                be.setEnemyTeamNum(paras.enemyTeamNum());
                be.setMaxGolem(paras.maxGolem());       // 这些数值不需要同步到客户端，因此不需要更新blockstate
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
