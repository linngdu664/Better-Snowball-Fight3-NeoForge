package com.linngdu664.bsf.particle;

import com.linngdu664.bsf.item.weapon.ImplosionSnowballCannonItem;
import com.linngdu664.bsf.util.BSFCommonUtil;
import com.linngdu664.bsf.util.SphereAxisRotationHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ImplosionSnowballCannonParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final SphereAxisRotationHelper rotationHelper;
    private float speed;
    private Vec3 movingStep;

    protected ImplosionSnowballCannonParticle(ClientLevel pLevel, Vec3 center, Vec3 offset, Vec3 axis, float r, float g, float b, SpriteSet pSprites) {
        super(pLevel, center.x + offset.x, center.y + offset.y, center.z + offset.z);
        this.hasPhysics = false;
        this.gravity = 0F;
        this.friction = 0.9F;
        this.sprites = pSprites;
        this.speed = (float) BSFCommonUtil.randDoubleWithInfer(this.random, 0.2, 1);
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.quadSize = 0.4F * (this.random.nextFloat() * this.random.nextFloat() * 1.0F + 4.5F);
        this.lifetime = 40;
        this.setSpriteFromAge(pSprites);
        this.rotationHelper = new SphereAxisRotationHelper(offset, axis);
        this.movingStep = axis.scale(BSFCommonUtil.randDoubleWithInfer(this.random, 0.2, 0.6));
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            Vec3 posDiff = rotationHelper.getDeltaMovement(speed).add(movingStep);
            this.move(posDiff.x, posDiff.y, posDiff.z);
            this.speed *= this.friction;
            this.movingStep = this.movingStep.scale(this.friction);
        }
        this.setSpriteFromAge(this.sprites);
        scale(0.92f);
    }


    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprite) {
            this.sprite = pSprite;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            RandomSource randomSource = pLevel.getRandom();
            float f = randomSource.nextFloat() * 0.6F + 0.4F;
            double theta = BSFCommonUtil.randDouble(randomSource, 0, 2 * Mth.PI);
            double phi = Math.acos(BSFCommonUtil.randDouble(randomSource, -1, 1)) - Mth.HALF_PI;
            return new ImplosionSnowballCannonParticle(pLevel, new Vec3(pX, pY, pZ), BSFCommonUtil.radRotationToVector(ImplosionSnowballCannonItem.RADIUS, theta, phi), new Vec3(pXSpeed, pYSpeed, pZSpeed).normalize(), f * 0.7F, f * 0.7F, f * 0.9F, this.sprite);
        }
    }

}
