package dev.drakosha.dsportals.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.common.entity.DragonEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.drakosha.dsportals.mixin.EntityLevelAccessor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Dragon Survival draws each player dragon through a client-side DragonEntity and throws it away
 * when the player leaves a level. Walking through an Immersive Portals portal moves the player to
 * another client level (removal reason CHANGED_DIMENSION) without a loading screen, so the fresh
 * DragonEntity visibly restarted its animations from the default pose (a second, "shrinking" model).
 * The DragonEntity is now kept across dimension changes and moved along with its player.
 */
@Mixin(targets = "by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer", remap = false)
public abstract class ClientDragonRendererMixin {
    @Inject(method = "removeEntry", at = @At("HEAD"), cancellable = true, remap = false)
    private static void dsip$keepDragonAcrossDimensions(EntityLeaveLevelEvent event, CallbackInfo callback) {
        if (event.getEntity() instanceof Player player && player.getRemovalReason() == Entity.RemovalReason.CHANGED_DIMENSION) {
            callback.cancel();
        }
    }

    @ModifyReturnValue(method = "getOrCreateDragon", at = @At("RETURN"), remap = false)
    private static DragonEntity dsip$followPlayerLevel(DragonEntity dragon, @Local(argsOnly = true) Player player) {
        if (dragon != null && dragon.level() != player.level()) {
            ((EntityLevelAccessor) dragon).dsip$setLevel(player.level());
        }

        return dragon;
    }
}
