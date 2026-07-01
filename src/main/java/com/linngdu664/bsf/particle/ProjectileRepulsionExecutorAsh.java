package com.linngdu664.bsf.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class ProjectileRepulsionExecutorAsh extends SingleQuadParticle {
    private final SpriteSet sprites;

    protected ProjectileRepulsionExecutorAsh(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pSprites.first());
        this.hasPhysics = false;
        this.gravity = 0F;
        this.friction = 0.9345794F;
        this.sprites = pSprites;
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.rCol = 0.8f;
        this.gCol = 0;
        this.bCol = 0.9f;
        this.quadSize = 0.12F * (this.random.nextFloat() * this.random.nextFloat() * 1.0F + 1.0F);
        this.lifetime = 59;
        this.setSpriteFromAge(pSprites);
    }

    @Override
    protected @NotNull Layer getLayer() {
        return Layer.OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        scale(1.0204f);
    }
    @Override
    public int getLightCoords(float partialTick) {
        return super.getLightCoords(partialTick)|112;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(@NotNull SimpleParticleType pType, @NotNull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, RandomSource random) {
            return new ProjectileRepulsionExecutorAsh(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
        }
    }
}
