package com.linngdu664.bsf.event;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.config.ServerConfig;
import com.linngdu664.bsf.entity.BSFSnowGolemEntity;
import com.linngdu664.bsf.item.component.RegionData;
import com.linngdu664.bsf.item.misc.SnowFallBootsItem;
import com.linngdu664.bsf.item.snowball.normal.SmoothSnowballItem;
import com.linngdu664.bsf.item.tank.SnowballTankItem;
import com.linngdu664.bsf.network.to_client.CurrentTeamPayload;
import com.linngdu664.bsf.network.to_client.TeamMembersPayload;
import com.linngdu664.bsf.network.to_client.UpdateScorePayload;
import com.linngdu664.bsf.registry.DataComponentRegister;
import com.linngdu664.bsf.registry.EffectRegister;
import com.linngdu664.bsf.registry.ItemRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import com.linngdu664.bsf.util.BSFEnchantmentHelper;
import com.linngdu664.bsf.misc.BSFTeamSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GamePlayEvents {
    public static final int CAPTURE_POINTS = 10;
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity deathEntity = event.getEntity();
        if (!deathEntity.level().isClientSide) {
            DamageSource source = event.getSource();
            Entity killerEntity = source.getEntity();
            BSFTeamSavedData savedData = deathEntity.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
            if (killerEntity instanceof ServerPlayer killerPlayer) {
                if (deathEntity instanceof ServerPlayer deathPlayer) {
                    ItemStack device = BSFCommonUtil.findInventoryItemStack(deathPlayer, p -> p.getItem().equals(ItemRegister.SCORING_DEVICE.get()) && p.getOrDefault(DataComponentRegister.RANK.get(), 0) >= 0);
                    ItemStack device1 = BSFCommonUtil.findInventoryItemStack(killerPlayer, p -> p.getItem().equals(ItemRegister.SCORING_DEVICE.get()) && p.getOrDefault(DataComponentRegister.RANK.get(), 0) >= 0);
                    if (device != null && device1 != null && !savedData.isSameTeam(killerPlayer, deathEntity)) {
                        RegionData region = device.getOrDefault(DataComponentRegister.REGION.get(), RegionData.EMPTY);
                        RegionData region1 = device1.getOrDefault(DataComponentRegister.REGION.get(), RegionData.EMPTY);
                        if (region1.equals(region) && region.inRegion(killerPlayer.position()) && region.inRegion(deathPlayer.position())) {
                            int deathPlayerPoint = device.getOrDefault(DataComponentRegister.MONEY.get(), 0);
                            int getPoints = deathPlayerPoint - CAPTURE_POINTS > 0 ? CAPTURE_POINTS : deathPlayerPoint;
                            device.set(DataComponentRegister.MONEY.get(), deathPlayerPoint - getPoints);
                            PacketDistributor.sendToPlayer(deathPlayer, new UpdateScorePayload(getPoints));
                            deathPlayer.displayClientMessage(Component.translatable("scoring_device_death_punishment.tip", getPoints), false);
                            int killerPlayerPoint = device1.getOrDefault(DataComponentRegister.MONEY.get(), 0);
                            device1.set(DataComponentRegister.MONEY.get(), killerPlayerPoint + getPoints);
                            device1.set(DataComponentRegister.RANK.get(), killerPlayerPoint + getPoints);
                            PacketDistributor.sendToPlayer(killerPlayer, new UpdateScorePayload(getPoints));
                            killerPlayer.displayClientMessage(Component.translatable("scoring_device_kill_bonus.tip", getPoints), false);
                        }
                    }
                } else if (deathEntity instanceof BSFSnowGolemEntity deathGolem) {
                    ItemStack device = BSFCommonUtil.findInventoryItemStack(killerPlayer, p -> p.getItem().equals(ItemRegister.SCORING_DEVICE.get()) && p.getOrDefault(DataComponentRegister.RANK.get(), 0) >= 0);
                    if (device != null && (deathGolem.getFixedTeamId() >= 0 && deathGolem.getFixedTeamId() != savedData.getTeam(killerPlayer.getUUID()) || deathGolem.getFixedTeamId() < 0 && !savedData.isSameTeam(killerPlayer, deathGolem.getOwner())) && device.getOrDefault(DataComponentRegister.REGION.get(), RegionData.EMPTY).inRegion(killerPlayer.position())) {
                        int killerPlayerPoint = device.getOrDefault(DataComponentRegister.MONEY.get(), 0);
                        int getPoints = deathGolem.getRank();
                        device.set(DataComponentRegister.MONEY.get(), killerPlayerPoint + getPoints);
                        device.set(DataComponentRegister.RANK.get(), killerPlayerPoint + getPoints);
                        PacketDistributor.sendToPlayer(killerPlayer, new UpdateScorePayload(getPoints));
                        killerPlayer.displayClientMessage(Component.translatable("scoring_device_kill_bonus.tip", getPoints), false);
                    }
                }
            } else if (killerEntity instanceof BSFSnowGolemEntity killerGolem && deathEntity instanceof ServerPlayer deathPlayer) {
                ItemStack device = BSFCommonUtil.findInventoryItemStack(deathPlayer, p -> p.getItem().equals(ItemRegister.SCORING_DEVICE.get()) && p.getOrDefault(DataComponentRegister.RANK.get(), 0) >= 0);
                if (device != null && (killerGolem.getFixedTeamId() >= 0 && killerGolem.getFixedTeamId() != savedData.getTeam(deathPlayer.getUUID()) || killerGolem.getFixedTeamId() < 0 && !savedData.isSameTeam(killerGolem.getOwner(), deathPlayer)) && device.getOrDefault(DataComponentRegister.REGION.get(), RegionData.EMPTY).inRegion(deathPlayer.position())) {
                    int deathPlayerPoint = device.getOrDefault(DataComponentRegister.MONEY.get(), 0);
                    int getPoints = deathPlayerPoint - CAPTURE_POINTS > 0 ? CAPTURE_POINTS : deathPlayerPoint;
                    device.set(DataComponentRegister.MONEY.get(), deathPlayerPoint - getPoints);
                    PacketDistributor.sendToPlayer(deathPlayer, new UpdateScorePayload(getPoints));
                    deathPlayer.displayClientMessage(Component.translatable("scoring_device_death_punishment.tip", getPoints), false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Pre event) {
//        if (event.getEntity() instanceof Player player1 && event.getSource().getEntity() instanceof Player player2) {
//            BSFTeamSavedData savedData = player1.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
//            int id = savedData.getTeam(player1.getUUID());
//            String msgId = event.getSource().getMsgId();
//            if (id >= 0 && id == savedData.getTeam(player2.getUUID()) && msgId.equals("thrown") && !ServerConfig.ENABLE_FRIENDLY_FIRE.getConfigValue()) {
//                event.setNewDamage(0);
//            }
//        }
        Entity targetEntity = event.getEntity();
        DamageSource damageSource = event.getSource();
        if (!targetEntity.level().isClientSide && damageSource.is(DamageTypes.THROWN) && !ServerConfig.ENABLE_FRIENDLY_FIRE.getConfigValue()) {
            BSFTeamSavedData savedData = targetEntity.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
            Entity killerEntity = damageSource.getEntity();
            if (killerEntity instanceof Player killerPlayer && (targetEntity instanceof Player targetPlayer && savedData.isSameTeam(killerPlayer, targetPlayer) || targetEntity instanceof BSFSnowGolemEntity targetGolem && (targetGolem.getFixedTeamId() >= 0 && savedData.getTeam(killerPlayer.getUUID()) == targetGolem.getFixedTeamId() || savedData.isSameTeam(killerPlayer, targetGolem.getOwner())))
                    || killerEntity instanceof BSFSnowGolemEntity killerGolem && (targetEntity instanceof Player targetPlayer && (killerGolem.getFixedTeamId() >= 0 && savedData.getTeam(targetPlayer.getUUID()) == killerGolem.getFixedTeamId() || savedData.isSameTeam(killerGolem.getOwner(), targetPlayer))
                    || targetEntity instanceof BSFSnowGolemEntity targetGolem && (killerGolem.getFixedTeamId() >= 0 && (targetGolem.getFixedTeamId() >= 0 && killerGolem.getFixedTeamId() == targetGolem.getFixedTeamId() || killerGolem.getFixedTeamId() == savedData.getTeam(targetGolem.getOwnerUUID())) || killerGolem.getFixedTeamId() < 0 && (targetGolem.getFixedTeamId() >= 0 && savedData.getTeam(killerGolem.getOwnerUUID()) == targetGolem.getFixedTeamId() || savedData.isSameTeam(killerGolem.getOwner(), targetGolem.getOwner()))))) {
                event.setNewDamage(0);
            }
//            if (killerEntity instanceof Player killerPlayer) {
//                if (targetEntity instanceof Player targetPlayer) {
//                    if (savedData.isSameTeam(killerPlayer, targetPlayer)) {
//                        event.setNewDamage(0);
//                    }
//                } else if (targetEntity instanceof BSFSnowGolemEntity targetGolem) {
//                    if (targetGolem.getFixedTeamId() >= 0 && savedData.getTeam(killerPlayer.getUUID()) == targetGolem.getFixedTeamId() || savedData.isSameTeam(killerPlayer, targetGolem.getOwner())) {
//                        event.setNewDamage(0);
//                    }
//                }
//            } else if (killerEntity instanceof BSFSnowGolemEntity killerGolem) {
//                if (targetEntity instanceof Player targetPlayer) {
//                    if (killerGolem.getFixedTeamId() >= 0 && savedData.getTeam(targetPlayer.getUUID()) == killerGolem.getFixedTeamId() || savedData.isSameTeam(killerGolem.getOwner(), targetPlayer)) {
//                        event.setNewDamage(0);
//                    }
//                } else if (targetEntity instanceof BSFSnowGolemEntity targetGolem) {
//                    if (killerGolem.getFixedTeamId() >= 0) {
//                        if (targetGolem.getFixedTeamId() >= 0 && killerGolem.getFixedTeamId() == targetGolem.getFixedTeamId() || killerGolem.getFixedTeamId() == savedData.getTeam(targetGolem.getOwnerUUID())) {
//                            event.setNewDamage(0);
//                        }
//                    } else {
//                        if (targetGolem.getFixedTeamId() >= 0 && savedData.getTeam(killerGolem.getOwnerUUID()) == targetGolem.getFixedTeamId() || savedData.isSameTeam(killerGolem.getOwner(), targetGolem.getOwner())) {
//                            event.setNewDamage(0);
//                        }
//                    }
//                }
//            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        BSFTeamSavedData savedData = player.getServer().overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(BSFTeamSavedData::new, BSFTeamSavedData::new), "bsf_team");
        PacketDistributor.sendToPlayer(player, new CurrentTeamPayload((byte) savedData.getTeam(player.getUUID())));
        PacketDistributor.sendToPlayer(player, new TeamMembersPayload(savedData.getMembers(savedData.getTeam(player.getUUID()))));
    }

    @SubscribeEvent
    public static void onLivingUseItemTick(LivingEntityUseItemEvent.Tick event) {
        LivingEntity livingEntity = event.getEntity();
        ItemStack itemStack = event.getItem();
        if (itemStack.has(DataComponentRegister.REGION.get()) && !itemStack.getItem().equals(ItemRegister.SCORING_DEVICE.get())) {
            RegionData region = itemStack.get(DataComponentRegister.REGION.get());
            if (!region.inRegion(event.getEntity().position())) {
                event.setCanceled(true);
                return;
            }
        }
        if (EnchantmentHelper.getTagEnchantmentLevel(BSFEnchantmentHelper.getEnchantmentHolder(livingEntity, BSFEnchantmentHelper.FLOATING_SHOOTING), itemStack) > 0) {
            double vy = livingEntity.getDeltaMovement().y;
            if (vy < 0) {
                livingEntity.resetFallDistance();
                livingEntity.push(0, -0.25 * vy, 0);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.has(DataComponentRegister.REGION.get()) && !itemStack.getItem().equals(ItemRegister.SCORING_DEVICE.get())) {
            RegionData region = itemStack.get(DataComponentRegister.REGION.get());
            if (!region.inRegion(event.getEntity().position())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.has(DataComponentRegister.REGION.get()) && !itemStack.getItem().equals(ItemRegister.REGION_TOOL.get())) {
            RegionData region = itemStack.get(DataComponentRegister.REGION.get());
            if (!region.inRegion(event.getHitVec().getLocation())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (!itemStack.getItem().equals(ItemRegister.REGION_TOOL.get()) && !itemStack.getItem().equals(ItemRegister.SCORING_DEVICE.get()) && itemStack.has(DataComponentRegister.REGION.get())) {
            RegionData region = event.getItemStack().get(DataComponentRegister.REGION.get());
            event.getToolTip().add(Component.translatable(
                    "region_limit.tooltip",
                    String.valueOf(region.start().getX()),
                    String.valueOf(region.start().getY()),
                    String.valueOf(region.start().getZ()),
                    String.valueOf(region.end().getX()),
                    String.valueOf(region.end().getY()),
                    String.valueOf(region.end().getZ())
            ).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();
        Entity entity = event.getTarget();
        Level level = player.level();
        if (!level.isClientSide && !player.isSpectator() && entity instanceof LivingEntity target) {
            Item item = player.getMainHandItem().getItem();
            if (item instanceof SolidBucketItem) {
                if (!(target instanceof BSFSnowGolemEntity) && !(target instanceof SnowGolem)) {
                    if (target.getTicksFrozen() < 240) {
                        target.setTicksFrozen(240);
                    }
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 150, 1));
                }
                target.addEffect(new MobEffectInstance(EffectRegister.WEAPON_JAM, 80, 0));
                ((ServerLevel) level).sendParticles(ParticleTypes.ITEM_SNOWBALL, target.getX(), target.getEyeY(), target.getZ(), 16, 0, 0, 0, 0);
                ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getEyeY(), target.getZ(), 16, 0, 0, 0, 0.04);
                if (target instanceof Blaze) {
                    target.hurt(level.damageSources().playerAttack(player), 8);
                }
                if (!player.getAbilities().instabuild) {
                    player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                    player.getInventory().placeItemBackInInventory(new ItemStack(Items.BUCKET), true);
                }
            } else if (item instanceof SnowballItem || item instanceof SmoothSnowballItem) {
                if (!(target instanceof BSFSnowGolemEntity) && !(target instanceof SnowGolem)) {
                    if (target.getTicksFrozen() < 180) {
                        target.setTicksFrozen(180);
                    }
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30, 1));
                }
                target.addEffect(new MobEffectInstance(EffectRegister.WEAPON_JAM, 40, 0));
                if (!player.getAbilities().instabuild) {
                    player.getItemInHand(InteractionHand.MAIN_HAND).shrink(1);
                }
                ((ServerLevel) level).sendParticles(ParticleTypes.ITEM_SNOWBALL, target.getX(), target.getEyeY(), target.getZ(), 8, 0, 0, 0, 0);
                ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, target.getX(), target.getEyeY(), target.getZ(), 8, 0, 0, 0, 0.04);
                if (target instanceof Blaze) {
                    target.hurt(level.damageSources().playerAttack(player), 4);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack itemStack = event.getCrafting();
        if (itemStack.getItem() instanceof SnowballTankItem) {
            itemStack.setDamageValue(itemStack.getMaxDamage());
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            Level level = player.level();
            ItemStack shoes = player.getItemBySlot(EquipmentSlot.FEET);
            if (!level.isClientSide && shoes.getItem() instanceof SnowFallBootsItem) {
                int i = Mth.floor(player.getX());
                int j = Mth.floor(player.getY());
                int k = Mth.floor(player.getZ());
                Block block1 = level.getBlockState(new BlockPos(i, j, k)).getBlock();
                //Block block2 = level.getBlockState(new BlockPos(i, j - 1, k)).getBlock();
                if (level.getBlockState(new BlockPos(i, j, k)).is(BlockTags.SNOW) || level.getBlockState(new BlockPos(i, j - 1, k)).is(BlockTags.SNOW) || snowAroundPlayer(level, player, block1)) {
                    event.setDamageMultiplier(0);
                    float h = event.getDistance();
                    ((ServerLevel) level).sendParticles(ParticleTypes.SNOWFLAKE, player.getX(), player.getY(), player.getZ(), (int) h * 8, 0, 0, 0, h * 0.01);
                    shoes.hurtAndBreak((int) Math.ceil((h - 3) * 0.25), player, EquipmentSlot.FEET);
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOW_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
                    int enchantmentLevel = EnchantmentHelper.getTagEnchantmentLevel(BSFEnchantmentHelper.getEnchantmentHolder(player, BSFEnchantmentHelper.KINETIC_ENERGY_STORAGE), shoes);
                    if (enchantmentLevel > 0 && h > 5) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, (int) h * 6, enchantmentLevel - 1));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(ResourceLocation.withDefaultNamespace("chests/shipwreck_treasure")) || event.getName().equals(ResourceLocation.withDefaultNamespace("chests/igloo_chest"))) {
            LootTable lootTable = event.getTable();
            lootTable.addPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .setBonusRolls(ConstantValue.exactly(0.0F))
                    .add(LootItem.lootTableItem(ItemRegister.SNOWBALL_CANNON_UPGRADE_SMITHING_TEMPLATE.get()))
                    .build());
            event.setTable(lootTable);
        } else if (event.getName().equals(ResourceLocation.withDefaultNamespace("chests/pillager_outpost"))) {
            LootTable lootTable = event.getTable();
            lootTable.addPool(LootPool.lootPool()
                    .setRolls(BinomialDistributionGenerator.binomial(2, 0.4F))
                    .setBonusRolls(ConstantValue.exactly(0.0F))
                    .add(LootItem.lootTableItem(ItemRegister.SNOWBALL_CANNON_UPGRADE_SMITHING_TEMPLATE.get()))
                    .build());
            event.setTable(lootTable);
        }
    }

    private static final AttributeModifier SKATES_SPEED_BUFF = new AttributeModifier(Main.makeResLoc("skates_speed"), 0.15, AttributeModifier.Operation.ADD_VALUE);
    private static final AttributeModifier SKATES_SPEED_DEBUFF = new AttributeModifier(Main.makeResLoc("skates_speed"), -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        ItemStack shoes = player.getItemBySlot(EquipmentSlot.FEET);
        AttributeMap attributes = player.getAttributes();
        if (!shoes.isEmpty() && shoes.getItem().equals(ItemRegister.ICE_SKATES_ITEM.get()) && player.isSprinting() && player.onGround()) {
            Level level = player.level();
            BlockPos pos = player.blockPosition().below();
            if (level.getBlockState(pos).is(BlockTags.ICE)) {
                level.addParticle(ParticleTypes.SNOWFLAKE, player.getX(), player.getEyeY() - 1.4, player.getZ(), 0, 0, 0);
                attributes.getInstance(Attributes.MOVEMENT_SPEED).addOrReplacePermanentModifier(SKATES_SPEED_BUFF);
            } else {
                attributes.getInstance(Attributes.MOVEMENT_SPEED).addOrReplacePermanentModifier(SKATES_SPEED_DEBUFF);
            }
        } else {
            attributes.getInstance(Attributes.MOVEMENT_SPEED).removeModifier(Main.makeResLoc("skates_speed"));
        }
    }

    private static boolean snowAroundPlayer(Level level, Player player, Block block1) {
        int x = Mth.floor(player.getX());
        int y = Mth.floor(player.getY());
        int z = Mth.floor(player.getZ());
        if (block1.equals(Blocks.AIR)) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    BlockPos pos1 = new BlockPos(x + i, y, z + j);
                    BlockPos pos2 = new BlockPos(x + i, y - 1, z + j);
                    if (level.getBlockState(pos1).is(BlockTags.SNOW) || level.getBlockState(pos2).is(BlockTags.SNOW)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
