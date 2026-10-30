package dev.drakosha.dsportals.mixin.client;

import by.dragonsurvivalteam.dragonsurvival.client.render.ClientDragonRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qouteall.imm_ptl.core.render.context_management.WorldRenderInfo;

/**
 * Dragon Survival draws the dragon body (minus the head) in first person from inside
 * LevelRenderer#renderLevel. Immersive Portals calls renderLevel again for every portal view;
 * when the view shows a different dimension the camera is not "detached", so Dragon Survival
 * would draw the local dragon's body into the other world at a wrong position.
 * The first-person body is suppressed while a portal view is being rendered
 * (the regular third-person-through-portal rendering of Immersive Portals is unaffected).
 */
@Mixin(value = LevelRenderer.class, priority = 900)
public abstract class FirstPersonBodyMixin {
    @Unique
    private static int dsip$portalDepth = 0;

    @Unique
    private static Boolean dsip$savedFirstPerson = null;

    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void dsip$beforeRenderLevel(CallbackInfo callback) {
        if (WorldRenderInfo.isRendering()) {
            if (dsip$portalDepth++ == 0) {
                dsip$savedFirstPerson = ClientDragonRenderer.renderInFirstPerson;
                ClientDragonRenderer.renderInFirstPerson = false;
            }
        } else if (dsip$portalDepth != 0) {
            // A nested render threw before reaching RETURN: recover the user's setting
            dsip$restore();
        }
    }

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void dsip$afterRenderLevel(CallbackInfo callback) {
        if (dsip$portalDepth > 0 && WorldRenderInfo.isRendering()) {
            if (--dsip$portalDepth == 0) {
                dsip$restore();
            }
        }
    }

    @Unique
    private static void dsip$restore() {
        if (dsip$savedFirstPerson != null) {
            ClientDragonRenderer.renderInFirstPerson = dsip$savedFirstPerson;
        }

        dsip$savedFirstPerson = null;
        dsip$portalDepth = 0;
    }
}
