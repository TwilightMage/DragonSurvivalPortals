package dev.drakosha.dsportals.mixin.breath.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.drakosha.dsportals.client.BreathParticlePortals;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Hands breath particles to BreathParticlePortals when created and after each of their ticks. */
@Mixin(ParticleEngine.class)
public abstract class ParticleEngineBreathMixin {
    @Inject(method = "add", at = @At("HEAD"))
    private void dsip$trackBreathParticle(Particle particle, CallbackInfo callback) {
        BreathParticlePortals.onAdded(particle);
    }

    @WrapOperation(
        method = "tickParticle",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;tick()V")
    )
    private void dsip$crossPortals(Particle particle, Operation<Void> original) {
        ParticleAccessor accessor = (ParticleAccessor) particle;
        Vec3 before = new Vec3(accessor.dsip$getX(), accessor.dsip$getY(), accessor.dsip$getZ());
        original.call(particle);
        BreathParticlePortals.afterTick(particle, before);
    }
}
