package dev.drakosha.dsportals.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public interface EntityLevelAccessor {
    /** Raw field write: Entity#setLevel is not public and we must not trigger any side effects. */
    @Mutable
    @Accessor("level")
    void dsip$setLevel(Level level);
}
