package com.linngdu664.bsf.item.weapon;

import com.linngdu664.bsf.block.CriticalSnow;
import com.linngdu664.bsf.block.LooseSnowBlock;
import com.linngdu664.bsf.client.screenshake.Easing;
import com.linngdu664.bsf.client.screenshake.ScreenshakeHandler;
import com.linngdu664.bsf.client.screenshake.ScreenshakeInstance;
import com.linngdu664.bsf.entity.snowball.util.ILaunchAdjustment;
import com.linngdu664.bsf.network.to_client.ForwardRaysParticlesPayload;
import com.linngdu664.bsf.network.to_client.ImplosionSnowballCannonParticlesPayload;
import com.linngdu664.bsf.network.to_client.ToggleMovingSoundPayload;
import com.linngdu664.bsf.network.to_client.packed_paras.ForwardRaysParticlesParas;
import com.linngdu664.bsf.particle.util.BSFParticleType;
import com.linngdu664.bsf.registry.EffectRegister;
import com.linngdu664.bsf.registry.SoundRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.linngdu664.bsf.event.ClientModEvents.CYCLE_MOVE_AMMO_NEXT;
import static com.linngdu664.bsf.event.ClientModEvents.CYCLE_MOVE_AMMO_PREV;
import java.util.List;

public class ImplosionSnowballCannonItem extends AbstractBSFWeaponItem {
    public static final int TYPE_FLAG = 64;
    public static final int DISTANCE = 24;
    public static final int RADIUS = 5;
    public static final double PUSH_POWER = 1;
    public static final double HURT_POWER = 1;
    public static final double RECOIL = 0.5;

    public ImplosionSnowballCannonItem() {
        super(1000, Rarity.EPIC, TYPE_FLAG);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer.hasEffect(EffectRegister.WEAPON_JAM)) {
            return InteractionResult.FAIL;
        }
        ItemStack stack = getAmmo(pPlayer, itemStack);
        if (stack != null || pPlayer.isCreative()) {
            Vec3 cameraVec = pPlayer.getViewVector(1);
            if (!pLevel.isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) pLevel;
                PacketDistributor.sendToPlayersInDimension((ServerLevel) pLevel, new ToggleMovingSoundPayload(pPlayer.getId(), SoundRegister.IMPLOSION_SNOWBALL_CANNON.get(), ToggleMovingSoundPayload.PLAY_ONCE));
                Vec3 eyePosition = pPlayer.getEyePosition();
                for (double l = 0; l < DISTANCE; l += 0.5) {
                    Vec3 paPos = eyePosition.add(cameraVec.scale(l));
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(pPlayer, new ImplosionSnowballCannonParticlesPayload(paPos.x, paPos.y, paPos.z, cameraVec.x, cameraVec.y, cameraVec.z));
                }
                AABB aabb = pPlayer.getBoundingBox().inflate(RADIUS).expandTowards(cameraVec.scale(DISTANCE + RADIUS));

                BlockPos.betweenClosedStream(aabb)
                        .filter(p -> serverLevel.getBlockState(p).getBlock() instanceof LooseSnowBlock || serverLevel.getBlockState(p).getBlock() instanceof CriticalSnow && BSFCommonUtil.pointOnTheFrontConeArea(pPlayer.getViewVector(1f), eyePosition, p.getCenter(), RADIUS, DISTANCE))
                        .forEach(p -> {
                            serverLevel.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
                            BlockState snow = Blocks.SNOW.defaultBlockState();
                            if (snow.canSurvive(pLevel, p) && !(serverLevel.getBlockState(p).getBlock() instanceof LooseSnowBlock)) {
                                serverLevel.setBlockAndUpdate(p, snow);
                            }
                            serverLevel.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.SNOW_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F / (serverLevel.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                            PacketDistributor.sendToPlayersTrackingEntityAndSelf(pPlayer, new ForwardRaysParticlesPayload(new ForwardRaysParticlesParas(new Vec3(p.getX(), p.getY(), p.getZ()), new Vec3(p.getX() + 1, p.getY() + 1, p.getZ() + 1), cameraVec, 0.2, 0.6, 10), BSFParticleType.SNOWFLAKE.ordinal()));
                        });

                List<Entity> list = serverLevel.getEntitiesOfClass(Entity.class, aabb, p -> !pPlayer.equals(p) && !p.isSpectator() && BSFCommonUtil.pointOnTheFrontConeArea(pPlayer.getViewVector(1f), eyePosition, p.getBoundingBox().getCenter(), RADIUS, DISTANCE));
                for (Entity entity : list) {
                    double d = BSFCommonUtil.vec3Projection(entity.getBoundingBox().getCenter().subtract(eyePosition), cameraVec);
                    if (d > 0) {
                        double basePower = Math.log(DISTANCE + 1 - d);
                        Vec3 pushVec = cameraVec.scale(basePower * PUSH_POWER);
                        if (entity instanceof LivingEntity) {
                            entity.hurt(serverLevel.damageSources().flyIntoWall(), (float) (basePower * HURT_POWER));
                        }
                        entity.push(pushVec.x, pushVec.y, pushVec.z);
                        if (entity instanceof ServerPlayer serverPlayer) {
                            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity));
                        }
                    }
                }
                itemStack.hurtAndBreak(1, pPlayer, pUsedHand);

                pPlayer.getCooldowns().addCooldown(itemStack, 60);
                if (stack != null) {
                    consumeAmmo(stack, pPlayer);
                }

                pPlayer.awardStat(Stats.ITEM_USED.get(this));
            } else {
                ScreenshakeHandler.addScreenshake((new ScreenshakeInstance(8)).setIntensity(1.5f).setEasing(Easing.BOUNCE_IN));
                pPlayer.push(-cameraVec.x * RECOIL, -cameraVec.y * RECOIL, -cameraVec.z * RECOIL);
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public ILaunchAdjustment getLaunchAdjustment(double damageDropRate, Item snowball) {
        return null;
    }

    @Override
    public boolean isAllowBulkedSnowball() {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("implosion_snowball_cannon.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("implosion_snowball_cannon1.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("guns1.tooltip").withStyle(ChatFormatting.GRAY));
        tooltipComponents.accept(Component.translatable("guns2.tooltip", CYCLE_MOVE_AMMO_PREV.getTranslatedKeyMessage(), CYCLE_MOVE_AMMO_NEXT.getTranslatedKeyMessage()).withStyle(ChatFormatting.DARK_GRAY));
    }

}

