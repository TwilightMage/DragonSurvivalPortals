package dev.drakosha.dsportals.mixin.breath.client;

import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface ParticleAccessor {
    @Accessor("x")
    double dsip$getX();

    @Accessor("y")
    double dsip$getY();

    @Accessor("z")
    double dsip$getZ();

    @Accessor("xd")
    double dsip$getXd();

    @Accessor("yd")
    double dsip$getYd();

    @Accessor("zd")
    double dsip$getZd();

    @Accessor("age")
    int dsip$getAge();

    @Accessor("age")
    void dsip$setAge(int age);

    @Accessor("lifetime")
    int dsip$getLifetime();

    @Accessor("lifetime")
    void dsip$setLifetime(int lifetime);

    /** Removes the particle without Particle#remove (which some particles override to spawn others). */
    @Accessor("removed")
    void dsip$setRemoved(boolean removed);
}
