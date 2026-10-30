package dev.drakosha.dsportals.mixin.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractClientPlayer.class)
public interface AbstractClientPlayerAccessor {
    /** Create (and others) resolve client-side targets through this field instead of Entity#level. */
    @Mutable
    @Accessor("clientLevel")
    void dsip$setClientLevel(ClientLevel level);
}
