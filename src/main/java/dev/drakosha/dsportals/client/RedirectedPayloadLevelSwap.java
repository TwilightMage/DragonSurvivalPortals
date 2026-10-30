package dev.drakosha.dsportals.client;

import dev.drakosha.dsportals.mixin.EntityLevelAccessor;
import dev.drakosha.dsportals.mixin.client.AbstractClientPlayerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;

/**
 * Immersive Portals handles a redirected packet by temporarily switching Minecraft.level to the
 * packet's dimension, but it deliberately leaves the local player's level untouched.
 * Mod payload handlers resolve their targets through the local player instead: NeoForge's
 * AdvancedAddEntityPayload / SyncAttachmentsPayload and every Dragon Survival sync packet use
 * player.level(), Create's contraption/block entity packets use player.clientLevel. So they look in
 * the wrong dimension. While a redirected *mod payload* is being handled we point both of the local
 * player's level fields at the switched world, then restore them.
 */
public final class RedirectedPayloadLevelSwap {
    private RedirectedPayloadLevelSwap() {
    }

    /** Kept outside the mixin so no lambda has to be merged into the target class. */
    public static Runnable wrap(Runnable task) {
        return () -> runWithPlayerInCurrentLevel(task);
    }

    public static void runWithPlayerInCurrentLevel(Runnable task) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel switchedLevel = minecraft.level;

        if (player == null || switchedLevel == null) {
            task.run();
            return;
        }

        Level originalLevel = player.level();
        ClientLevel originalClientLevel = player.clientLevel;

        if (originalLevel == switchedLevel && originalClientLevel == switchedLevel) {
            task.run();
            return;
        }

        EntityLevelAccessor levelAccessor = (EntityLevelAccessor) (Object) player;
        AbstractClientPlayerAccessor clientLevelAccessor = (AbstractClientPlayerAccessor) (Object) player;
        levelAccessor.dsip$setLevel(switchedLevel);
        clientLevelAccessor.dsip$setClientLevel(switchedLevel);

        try {
            task.run();
        } finally {
            // Only restore if the handler did not legitimately move the player somewhere else
            if (player.level() == switchedLevel) {
                levelAccessor.dsip$setLevel(originalLevel);
            }

            if (player.clientLevel == switchedLevel) {
                clientLevelAccessor.dsip$setClientLevel(originalClientLevel);
            }
        }
    }
}
