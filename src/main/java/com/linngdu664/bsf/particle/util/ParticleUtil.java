package com.linngdu664.bsf.particle.util;

import com.linngdu664.bsf.network.to_client.packed_paras.ForwardConeParticlesParas;
import com.linngdu664.bsf.network.to_client.packed_paras.ForwardRaysParticlesParas;
import com.linngdu664.bsf.registry.ParticleRegister;
import com.linngdu664.bsf.util.BSFCommonUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class ParticleUtil {
    /**
     * This method uses the parametric equation of circles, in which the distance from plane to origin is 8m and the
     * normal vector is sightVec, to spray cone-shaped particles forward, and the maximum radius needs to be specified.
     * It can only work in the client side. Please use "ForwardConeParticlesSender" to send particles in the server side.
     *
     * @param pLevel          Entity's Level.
     * @param particleOptions The type of the particle.
     * @param entity          Entity.
     * @param sightVec        The entity's sight vector.
     * @param r               The max radius.
     * @param aStep           Angle step length in degree.
     * @param rStep           Radius step length.
     * @param loweredVision   The negative y offset of the spawning point.
     */
    public static void spawnForwardConeParticles(Level pLevel, Entity entity, Vec3 sightVec, ParticleOptions particleOptions, float r, float aStep, float rStep, double loweredVision) {
        // particleOptions=ParticleTypes.SNOWFLAKE r=4.5 deg=30 d=0.5f
        spawnForwardConeParticles(pLevel, particleOptions, entity.getX(), entity.getEyeY(), entity.getZ(), sightVec, r, aStep, rStep, loweredVision);
    }

    public static void spawnForwardConeParticles(Level level, ParticleOptions particleOptions, ForwardConeParticlesParas paras) {
        spawnForwardConeParticles(level, particleOptions, paras.eyePos().x, paras.eyePos().y, paras.eyePos().z, paras.sightVec(), paras.r(), paras.aStep(), paras.rStep(), paras.loweredVision());
    }

    public static void spawnForwardConeParticles(Level pLevel, ParticleOptions particleOptions, double eX, double eY, double eZ, Vec3 sightVec, float r, float aStep, float rStep, double loweredVision) {
        // particleOptions=ParticleTypes.SNOWFLAKE r=4.5 deg=30 d=0.5f
        Vec3 vecA = sightVec.cross(new Vec3(0, 1, 0)).normalize();
        if (vecA == Vec3.ZERO) {
            vecA = sightVec.cross(new Vec3(1, 0, 0)).normalize();
        }
        Vec3 vecB = sightVec.cross(vecA).normalize();
        for (float ri = 0.5F; ri <= r; ri += rStep) {
            float rand = pLevel.getRandom().nextFloat() * Mth.DEG_TO_RAD * aStep;
            for (float theta = rand; theta < Mth.TWO_PI + rand; theta += Mth.DEG_TO_RAD * aStep) {
                double x = 8.0F * sightVec.x + ri * (Mth.cos(theta) * vecA.x + Mth.sin(theta) * vecB.x);
                double y = 8.0F * sightVec.y + ri * (Mth.cos(theta) * vecA.y + Mth.sin(theta) * vecB.y);
                double z = 8.0F * sightVec.z + ri * (Mth.cos(theta) * vecA.z + Mth.sin(theta) * vecB.z);
                double inverseL = Mth.invSqrt(BSFCommonUtil.lengthSqr(x, y, z));
                double rand1 = Math.sqrt(pLevel.getRandom().nextDouble() * 0.9 + 0.1);
                pLevel.addParticle(particleOptions, eX, eY - loweredVision, eZ, x * inverseL * rand1, y * inverseL * rand1, z * inverseL * rand1);
            }
        }
    }

    public static void spawnForwardRaysParticles(Level pLevel, ParticleOptions particleOptions, ForwardRaysParticlesParas paras) {
        spawnForwardRaysParticles(pLevel, particleOptions, paras.pos1(), paras.pos2(), paras.vec(), paras.vMin(), paras.vMax(), paras.num());
    }

    /**
     * This method can randomly generate particles with different speeds in the same direction in the cuboid area.
     * It can only work in the client side. Please use "ForwardRaysParticlesToClient" to send particles in the server side.
     *
     * @param pLevel          Entity's Level.
     * @param particleOptions The type of the particle.
     * @param pos1            Cuboid vertex 1
     * @param pos2            Cuboid vertex 2
     * @param vec             Velocity vector
     * @param vMin            Speed minimum
     * @param vMax            Speed maximum
     * @param num             Number of particles
     */
    public static void spawnForwardRaysParticles(Level pLevel, ParticleOptions particleOptions, Vec3 pos1, Vec3 pos2, Vec3 vec, double vMin, double vMax, int num) {
        double dx, dy, dz;
        RandomSource randomSource = pLevel.getRandom();
        for (int i = 0; i < num; i++) {
            dx = BSFCommonUtil.randDoubleWithInfer(randomSource, pos1.x, pos2.x);
            dy = BSFCommonUtil.randDoubleWithInfer(randomSource, pos1.y, pos2.y);
            dz = BSFCommonUtil.randDoubleWithInfer(randomSource, pos1.z, pos2.z);
            Vec3 v = vec.normalize().scale(BSFCommonUtil.randDouble(randomSource, vMin, vMax));
            pLevel.addParticle(particleOptions, dx, dy, dz, v.x, v.y, v.z);
        }
    }

    /**
     * @param pLevel          Entity's Level.
     * @param particleOptions The type of the particle.
     * @param pos1            Cuboid vertex 1
     * @param pos2            Cuboid vertex 2
     * @param vec             Velocity vector
     * @param inertia         inertia vector
     * @param vMin            Speed minimum
     * @param vMax            Speed maximum
     * @param num             Number of particles
     */
    public static void spawnForwardRaysParticles(Level pLevel, ParticleOptions particleOptions, Vec3 pos1, Vec3 pos2, Vec3 vec, Vec3 inertia, double vMin, double vMax, int num) {
        double dx, dy, dz;
        RandomSource randomSource = pLevel.getRandom();
        for (int i = 0; i < num; i++) {
            dx = BSFCommonUtil.randDoubleWithInfer(randomSource, pos1.x, pos2.x);
            dy = BSFCommonUtil.randDoubleWithInfer(randomSource, pos1.y, pos2.y);
            dz = BSFCommonUtil.randDoubleWithInfer(randomSource, pos1.z, pos2.z);
            Vec3 v = vec.normalize().scale(BSFCommonUtil.randDouble(randomSource, vMin, vMax)).add(inertia);
            pLevel.addParticle(particleOptions, dx, dy, dz, v.x, v.y, v.z);
        }
    }

    public static void spawnSphereGatherParticles(Level level, ParticleOptions particleOptions, Vec3 pos, double range, int num, double v) {
        for (int i = 0; i < num; i++) {
            RandomSource randomSource = level.getRandom();
            double theta = BSFCommonUtil.randDouble(randomSource, 0, 2 * Mth.PI);
            double phi = Math.acos(BSFCommonUtil.randDouble(randomSource, -1, 1)) - Mth.HALF_PI;
            Vec3 direction = BSFCommonUtil.radRotationToVector(1, theta, phi);
            Vec3 pos1 = direction.scale(-range).add(pos);
            Vec3 v1 = direction.scale(v);
            level.addParticle(particleOptions, pos1.x, pos1.y, pos1.z, v1.x, v1.y, v1.z);
        }
    }

    public static void spawnSphereDiffusionParticles(Level level, ParticleOptions particleOptions, Vec3 pos, int num, double v) {
        for (int i = 0; i < num; i++) {
            RandomSource randomSource = level.getRandom();
            double theta = BSFCommonUtil.randDouble(randomSource, 0, 2 * Mth.PI);
            double phi = Math.acos(BSFCommonUtil.randDouble(randomSource, -1, 1)) - Mth.HALF_PI;
            Vec3 v1 = BSFCommonUtil.radRotationToVector(v, theta, phi);
            level.addParticle(particleOptions, pos.x, pos.y, pos.z, v1.x, v1.y, v1.z);
        }
    }

    public static void spawnSubspaceSnowballParticles(Level level, ParticleOptions particleOptions, Vec3 pos, double range, int num) {
        for (int i = 0; i < num; i++) {
            RandomSource randomSource = level.getRandom();
            double theta = BSFCommonUtil.randDouble(randomSource, 0, 2 * Mth.PI);
            double phi = Math.acos(BSFCommonUtil.randDouble(randomSource, -1, 1)) - Mth.HALF_PI;
            Vec3 direction = BSFCommonUtil.radRotationToVector(1, theta, phi).normalize();
            Vec3 pos1 = direction.scale(-range).add(pos);
            level.addParticle(particleOptions, pos1.x, pos1.y, pos1.z, pos.x, pos.y, pos.z);
        }
    }

    public static void spawnVectorInversionParticles(Level level, ParticleOptions particleOptions, Vec3 pos, double range, int num, double v) {
        for (int i = 0; i < num; i++) {
            level.addParticle(particleOptions, pos.x, pos.y, pos.z, 0, Mth.PI / 2, v);
        }
    }

    public static void spawnSnowParticles(Level level, Vec3 pos, double height, int num) {
        RandomSource random = level.getRandom();
        for (int i = 0; i < num; i++) {
            level.addParticle(ParticleRegister.SPAWN_SNOW.get(), pos.x, pos.y + BSFCommonUtil.randDouble(random, 0, height), pos.z, 0, 0, 0);
        }
    }

    public static Color hsvColor(int hue, int saturation, int brightness) {
        int rgb = Mth.hsvToRgb((float) hue / 360, (float) saturation / 100, (float) brightness / 100);
        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, (rgb) & 0xFF);
    }
}
