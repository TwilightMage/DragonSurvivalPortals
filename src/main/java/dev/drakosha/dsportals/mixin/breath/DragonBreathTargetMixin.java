package dev.drakosha.dsportals.mixin.breath;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.DragonBreathTarget;
import dev.drakosha.dsportals.breath.BreathThroughPortals;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Dragon breath through portals (and not behind them), see BreathThroughPortals. */
@Mixin(value = DragonBreathTarget.class, remap = false)
public abstract class DragonBreathTargetMixin {
    @Inject(method = "apply", at = @At("HEAD"), cancellable = true, remap = false)
    private void dsip$breathThroughPortals(ServerPlayer dragon, DragonAbilityInstance ability, CallbackInfo callback) {
        BreathThroughPortals.apply((DragonBreathTarget) (Object) this, dragon, ability);
        callback.cancel();
    }
}
