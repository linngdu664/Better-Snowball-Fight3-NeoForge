package com.linngdu664.bsf.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.HugeExplosionParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImpulseParticle extends HugeExplosionParticle {
    protected ImpulseParticle(ClientLevel level, double x, double y, double z, double quadSizeMultiplier, SpriteSet sprites) {
        super(level, x, y, z, quadSizeMultiplier, sprites);
        this.lifetime = 5;
        this.quadSize = 3F;
        this.setSpriteFromAge(sprites);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ImpulseParticle(level, x, y, z, xSpeed, this.sprites);
        }
    }
}