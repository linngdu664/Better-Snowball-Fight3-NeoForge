package com.linngdu664.bsf.criterion_trigger;

import com.linngdu664.bsf.entity.snowball.AbstractBSFSnowballEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class SnowballDamageTrigger extends SimpleCriterionTrigger<SnowballDamageTrigger.TriggerInstance> {
    @Override
    public Codec<SnowballDamageTrigger.TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, AbstractBSFSnowballEntity snowball, float amountDealt) {
        this.trigger(player, p -> p.matches(snowball, amountDealt));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<String> snowballName, Optional<MinMaxBounds.Doubles> bound) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<SnowballDamageTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(p -> p.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                Codec.STRING.optionalFieldOf("snowball").forGetter(TriggerInstance::snowballName),
                MinMaxBounds.Doubles.CODEC.optionalFieldOf("dealt").forGetter(TriggerInstance::bound)
        ).apply(p, TriggerInstance::new));

        public boolean matches(AbstractBSFSnowballEntity snowball, float amountDealt) {
            return snowballName.map(s -> bound.map(doubles -> s.equals(BuiltInRegistries.ENTITY_TYPE.getKey(snowball.getType()).toString()) && doubles.matches(amountDealt)).orElseGet(() -> s.equals(BuiltInRegistries.ENTITY_TYPE.getKey(snowball.getType()).toString()))).orElseGet(() -> bound.map(doubles -> doubles.matches(amountDealt)).orElse(true));
        }
    }
}
