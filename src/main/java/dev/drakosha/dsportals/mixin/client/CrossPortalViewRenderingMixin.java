package dev.drakosha.dsportals.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.drakosha.dsportals.client.ThirdPersonCameraDistance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** The third-person camera behind a portal keeps the distance other mods gave it, see ThirdPersonCameraDistance. */
@Mixin(targets = "qouteall.imm_ptl.core.render.CrossPortalViewRendering", remap = false)
public abstract class CrossPortalViewRenderingMixin {
    @ModifyReturnValue(method = "getThirdPersonMaxDistance", at = @At("RETURN"), remap = false)
    private static double dsip$keepModdedCameraDistance(double distance) {
        return distance * ThirdPersonCameraDistance.multiplier();
    }
}
