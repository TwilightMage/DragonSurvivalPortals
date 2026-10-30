package dev.drakosha.dsportals.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.drakosha.dsportals.client.RedirectedPayloadLevelSwap;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Hooks Immersive Portals' handling of a redirected packet. Vanilla packets are left alone
 * (Immersive Portals already handles them via the switched ClientPacketListener level);
 * modded payloads additionally get the local player's level pointed at the redirected dimension.
 */
@Mixin(targets = "qouteall.imm_ptl.core.network.PacketRedirectionClient", remap = false)
public abstract class PacketRedirectionClientMixin {
    @WrapOperation(
        method = {"handleRedirectedPacket", "old_handleRedirectedPacket"},
        at = @At(
            value = "INVOKE",
            target = "Lqouteall/imm_ptl/core/ClientWorldLoader;withSwitchedWorldFailSoft(Lnet/minecraft/resources/ResourceKey;Ljava/lang/Runnable;)V"
        ),
        remap = false
    )
    private static void dsip$swapPlayerLevelForModdedPayloads(
        ResourceKey<Level> dimension, Runnable task, Operation<Void> original,
        @Local(argsOnly = true) Packet<?> packet
    ) {
        if (packet instanceof ClientboundCustomPayloadPacket) {
            original.call(dimension, RedirectedPayloadLevelSwap.wrap(task));
        } else {
            original.call(dimension, task);
        }
    }
}
