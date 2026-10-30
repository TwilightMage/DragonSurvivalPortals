package dev.drakosha.dsportals.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import qouteall.imm_ptl.core.compat.iris_compatibility.IrisInterface;

/**
 * Immersive Portals turned all cross-portal entity rendering off as soon as Iris was installed,
 * though Iris without a shader pack renders like vanilla: now only while
 * a shader pack is on.
 */
@Mixin(targets = "qouteall.imm_ptl.core.render.CrossPortalEntityRenderer", remap = false)
public abstract class IrisShaderPackMixin {
    @WrapOperation(
        method = "isCrossPortalRenderingEnabled",
        at = @At(
            value = "INVOKE",
            target = "Lqouteall/imm_ptl/core/compat/iris_compatibility/IrisInterface$Invoker;isIrisPresent()Z"
        ),
        remap = false
    )
    private static boolean dsip$onlyOffWithShaderPack(IrisInterface.Invoker iris, Operation<Boolean> original) {
        return iris.isShaders();
    }
}
