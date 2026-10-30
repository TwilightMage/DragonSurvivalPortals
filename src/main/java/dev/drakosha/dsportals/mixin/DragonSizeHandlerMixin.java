package dev.drakosha.dsportals.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import qouteall.imm_ptl.core.ducks.IEEntity;

/**
 * Immersive Portals overwrites Player#canPlayerFitWithinBlocksAndEntitiesWhen so that the part of
 * the hitbox that is "behind" a portal is ignored. Dragon Survival then replaces the result for
 * dragons with DragonSizeHandler#canPoseFit, which tests the full dragon hitbox and therefore
 * collides with the blocks behind the portal (forced crouch/crawl, getting stuck in portals).
 * Here the dragon box is clipped the same way Immersive Portals clips the vanilla one.
 */
@Mixin(targets = "by.dragonsurvivalteam.dragonsurvival.common.handlers.DragonSizeHandler", remap = false)
public abstract class DragonSizeHandlerMixin {
    @WrapOperation(
        method = "canPoseFit",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/phys/AABB;)Z"
        ),
        remap = false
    )
    private static boolean dsip$respectPortalClipping(
        Level level, AABB box, Operation<Boolean> original,
        @Local(argsOnly = true) Player player
    ) {
        AABB activeBox = ((IEEntity) (Object) player).ip_getActiveCollisionBox(box);

        if (activeBox == null) {
            // The whole box is on the other side of a portal: nothing to collide with here
            return true;
        }

        return original.call(level, activeBox);
    }
}
