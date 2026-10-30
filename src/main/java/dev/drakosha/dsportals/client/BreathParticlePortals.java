package dev.drakosha.dsportals.client;

import dev.drakosha.dsportals.mixin.breath.client.ParticleAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import qouteall.imm_ptl.core.ClientWorldLoader;
import qouteall.imm_ptl.core.IPGlobal;
import qouteall.imm_ptl.core.IPMcHelper;
import qouteall.imm_ptl.core.portal.Mirror;
import qouteall.imm_ptl.core.portal.Portal;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Dragon Survival's breath particles are spawned at the dragon's mouth in the dragon's world and
 * fly on in that world: at a portal they went on behind it (hidden by the portal, or visible from
 * the side) instead of coming out on the other side. Breath particles spawned near portals are now
 * followed; the tick one passes through a portal it is removed and continues on the other side as a
 * copy (same particle type, moved through the portal, same speed, age and lifetime). It is removed
 * without Particle#remove: DS's fire particles leave smoke there, which hung at the portal plane.
 */
public final class BreathParticlePortals {
    private record Spawning(ParticleOptions options, List<Portal> portals) {
    }

    private static @Nullable Spawning spawning;
    private static final Map<Particle, Spawning> FOLLOWED = new WeakHashMap<>();

    private BreathParticlePortals() {
    }

    /** Around Dragon Survival spawning one breath particle at (x, y, z) in level. */
    public static void spawning(Level level, ParticleOptions options, double x, double y, double z, Runnable spawn) {
        Vec3 origin = new Vec3(x, y, z);
        List<Portal> portals = IPMcHelper.getNearbyPortalList(level, origin, IPGlobal.maxNormalPortalRadius,
            portal -> !(portal instanceof Mirror) && portal.isVisible() && portal.isInFrontOfPortal(origin));

        if (portals.isEmpty()) {
            spawn.run();
            return;
        }

        spawning = new Spawning(options, portals);

        try {
            spawn.run();
        } finally {
            spawning = null;
        }
    }

    public static void onAdded(Particle particle) {
        if (spawning != null) {
            FOLLOWED.put(particle, spawning);
        }
    }

    public static void afterTick(Particle particle, Vec3 before) {
        Spawning followed = FOLLOWED.get(particle);

        if (followed == null || !particle.isAlive()) {
            return;
        }

        ParticleAccessor accessor = (ParticleAccessor) particle;
        Vec3 after = new Vec3(accessor.dsip$getX(), accessor.dsip$getY(), accessor.dsip$getZ());

        for (Portal portal : followed.portals()) {
            if (!portal.isRemoved() && portal.isInFrontOfPortal(before) && !portal.isInFrontOfPortal(after)
                && portal.rayTrace(before, after) != null) {
                FOLLOWED.remove(particle);
                accessor.dsip$setRemoved(true);
                continueOnOtherSide(portal, followed.options(), accessor, after);
                return;
            }
        }
    }

    private static void continueOnOtherSide(Portal portal, ParticleOptions options, ParticleAccessor source, Vec3 position) {
        ClientLevel destination = ClientWorldLoader.getWorld(portal.getDestDim());

        if (destination == null) {
            return;
        }

        Vec3 at = portal.transformPoint(position);
        Vec3 velocity = portal.transformLocalVec(new Vec3(source.dsip$getXd(), source.dsip$getYd(), source.dsip$getZd()));

        // created while Immersive Portals has switched to the destination world, so the particle belongs to it
        Particle copy = ClientWorldLoader.withSwitchedWorld(destination, () -> Minecraft.getInstance().particleEngine
            .createParticle(options, at.x, at.y, at.z, velocity.x, velocity.y, velocity.z));

        if (copy != null) {
            ((ParticleAccessor) copy).dsip$setLifetime(source.dsip$getLifetime());
            ((ParticleAccessor) copy).dsip$setAge(source.dsip$getAge());
        }
    }
}
