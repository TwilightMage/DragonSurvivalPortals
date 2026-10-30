package dev.drakosha.dsportals.mixin.breath.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.drakosha.dsportals.client.BreathParticlePortals;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Dragon Survival's breath particles are followed through portals, see BreathParticlePortals. */
@Mixin(targets = "by.dragonsurvivalteam.dragonsurvival.network.client.ClientProxy", remap = false)
public abstract class ClientProxyBreathMixin {
    @WrapOperation(
        method = "spawnParticle",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
        ),
        remap = false
    )
    private static void dsip$followThroughPortals(Level level, ParticleOptions particle, double x, double y, double z,
                                                double xd, double yd, double zd, Operation<Void> original) {
        BreathParticlePortals.spawning(level, particle, x, y, z, () -> original.call(level, particle, x, y, z, xd, yd, zd));
    }
}
