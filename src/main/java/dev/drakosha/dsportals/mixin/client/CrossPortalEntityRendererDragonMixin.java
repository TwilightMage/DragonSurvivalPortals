package dev.drakosha.dsportals.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.drakosha.dsportals.client.DragonPortalRenderArea;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.ClientWorldLoader;
import qouteall.imm_ptl.core.render.CrossPortalEntityRenderer;
import qouteall.imm_ptl.core.collision.PortalCollisionHandler;
import qouteall.imm_ptl.core.ducks.IEEntity;

/** Cross-portal rendering of dragons uses their model area instead of the hitbox, see DragonPortalRenderArea. */
@Mixin(targets = "qouteall.imm_ptl.core.render.CrossPortalEntityRenderer", remap = false)
public abstract class CrossPortalEntityRendererDragonMixin {
    // Which entities get cut at the plane and drawn on the other side at all
    @WrapOperation(
        method = "onEntityTickClient",
        at = @At(value = "INVOKE", target = "Lqouteall/imm_ptl/core/ducks/IEEntity;ip_isCollidingWithPortal()Z"),
        remap = false
    )
    private static boolean dsip$dragonModelTouchesPortal(IEEntity entity, Operation<Boolean> original) {
        if (original.call(entity)) {
            return true;
        }

        Entity self = (Entity) entity;
        return DragonPortalRenderArea.isDragon(self) && DragonPortalRenderArea.collisionsForRendering(self) != null;
    }

    // Immersive Portals only collects entities whose hitbox touches a portal; add dragons whose model does
    @Inject(method = "onClientTick", at = @At("TAIL"), remap = false)
    private static void dsip$collectDragonsNearPortals(CallbackInfo callback) {
        // the client ticks before Immersive Portals has set up its worlds when joining (crashed the client)
        if (!ClientWorldLoader.getIsInitialized()) {
            return;
        }

        for (ClientLevel level : ClientWorldLoader.getClientWorlds()) {
            for (AbstractClientPlayer player : level.players()) {
                if (DragonPortalRenderArea.isDragon(player) && DragonPortalRenderArea.collisionsForRendering(player) != null) {
                    CrossPortalEntityRenderer.onEntityTickClient(player);
                }
            }
        }
    }

    // Which portals to cut at (main view) and to draw the other side through
    @WrapOperation(
        method = {"beforeRenderingEntity", "renderEntityProjections"},
        at = @At(value = "INVOKE", target = "Lqouteall/imm_ptl/core/ducks/IEEntity;ip_getPortalCollisionHandler()Lqouteall/imm_ptl/core/collision/PortalCollisionHandler;"),
        remap = false
    )
    private static PortalCollisionHandler dsip$dragonModelPortals(IEEntity entity, Operation<PortalCollisionHandler> original) {
        Entity self = (Entity) entity;

        if (DragonPortalRenderArea.isDragon(self)) {
            PortalCollisionHandler collisions = DragonPortalRenderArea.collisionsForRendering(self);

            if (collisions != null) {
                return collisions;
            }
        }

        return original.call(entity);
    }

    // Inside a portal view the other side is only drawn when the (moved) box reaches through the portal
    @WrapOperation(
        method = "renderEntity",
        at = @At(value = "INVOKE", target = "Lqouteall/imm_ptl/core/McHelper;getBoundingBoxWithMovedPosition(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;"),
        remap = false
    )
    private static AABB dsip$dragonModelBox(Entity entity, Vec3 position, Operation<AABB> original) {
        AABB box = original.call(entity, position);

        if (!DragonPortalRenderArea.isDragon(entity)) {
            return box;
        }

        return DragonPortalRenderArea.modelArea(entity, box);
    }
}
