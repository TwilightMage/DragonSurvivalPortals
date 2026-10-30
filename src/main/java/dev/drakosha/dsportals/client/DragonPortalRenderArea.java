package dev.drakosha.dsportals.client;

import by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import qouteall.imm_ptl.core.IPMcHelper;
import qouteall.imm_ptl.core.collision.CollisionHelper;
import qouteall.imm_ptl.core.collision.PortalCollisionEntry;
import qouteall.imm_ptl.core.collision.PortalCollisionHandler;
import qouteall.imm_ptl.core.ducks.IEEntity;

import java.util.WeakHashMap;

/**
 * Immersive Portals cuts an entity at a portal and draws the rest on the other side only for the
 * portals its hitbox touches. A dragon's model (neck, wings, tail) reaches several blocks past its
 * hitbox, so those parts vanished into the portal. For rendering only, a dragon is treated as
 * touching every portal its model area reaches; collision and teleportation keep the real hitbox.
 */
public final class DragonPortalRenderArea {
    private static final WeakHashMap<Entity, Cached> CACHE = new WeakHashMap<>();

    private record Cached(long time, @Nullable PortalCollisionHandler collisions) {
    }

    private DragonPortalRenderArea() {
    }

    public static boolean isDragon(Entity entity) {
        return entity instanceof Player && DragonStateProvider.isDragon(entity);
    }

    /**
     * Roughly where the dragon model reaches: the hitbox is about player sized, while neck, head,
     * tail and wings reach a few blocks further, growing with the dragon's scale attribute.
     */
    public static AABB modelArea(Entity dragon, AABB hitbox) {
        double scale = dragon instanceof Player player ? player.getScale() : 1;
        // Along the body: the tail of an adult reaches about 4 blocks behind the hitbox, neck and head ~2.5 ahead;
        // across it ~2 (DS itself culls with 10 * scale). A box in every direction cut dragons standing BESIDE a
        // portal at the extension of its plane (the tail of a dragon 3 blocks east of a portal, user report).
        float yaw = dragon instanceof LivingEntity living ? living.yBodyRot : dragon.getYRot();
        double ahead = 2.5 * scale;
        double behind = 4.0 * scale;
        double across = 2.0 * scale;
        Vec3 forward = new Vec3(-Math.sin(Math.toRadians(yaw)), 0, Math.cos(Math.toRadians(yaw)));
        Vec3 side = new Vec3(forward.z, 0, -forward.x);
        Vec3 center = hitbox.getCenter();
        AABB area = hitbox;

        for (Vec3 corner : new Vec3[] {
            center.add(forward.scale(ahead)).add(side.scale(across)), center.add(forward.scale(ahead)).subtract(side.scale(across)),
            center.subtract(forward.scale(behind)).add(side.scale(across)), center.subtract(forward.scale(behind)).subtract(side.scale(across))}) {
            area = area.minmax(new AABB(corner, corner));
        }

        return new AABB(area.minX, hitbox.minY - 0.25 * scale, area.minZ, area.maxX, hitbox.maxY + 2.0 * scale, area.maxZ);
    }

    /** The hitbox, but along the axis nearest to the portal normal the area's extent (an axis-aligned portal; else the hitbox). */
    private static AABB acrossPlaneLikeHitbox(AABB area, AABB hitbox, Vec3 normal) {
        Direction axis = Direction.getNearest(normal.x, normal.y, normal.z);

        if (Math.abs(Vec3.atLowerCornerOf(axis.getNormal()).dot(normal)) < 0.99) {
            return hitbox;
        }

        return switch (axis.getAxis()) {
            case X -> new AABB(area.minX, hitbox.minY, hitbox.minZ, area.maxX, hitbox.maxY, hitbox.maxZ);
            case Y -> new AABB(hitbox.minX, area.minY, hitbox.minZ, hitbox.maxX, area.maxY, hitbox.maxZ);
            case Z -> new AABB(hitbox.minX, hitbox.minY, area.minZ, hitbox.maxX, hitbox.maxY, area.maxZ);
        };
    }

    /** The portals the dragon's hitbox or model touches (computed once per tick), or null when none. */
    public static @Nullable PortalCollisionHandler collisionsForRendering(Entity dragon) {
        long time = dragon.level().getGameTime();
        Cached cached = CACHE.get(dragon);

        if (cached != null && cached.time() == time) {
            return cached.collisions();
        }

        PortalCollisionHandler result = new PortalCollisionHandler();
        PortalCollisionHandler real = ((IEEntity) dragon).ip_getPortalCollisionHandler();

        if (real != null) {
            result.portalCollisions.addAll(real.portalCollisions);
        }

        AABB area = modelArea(dragon, dragon.getBoundingBox());
        Vec3 eyePos = dragon.getEyePosition();
        int radius = (int) Math.ceil(Math.max(area.getXsize(), Math.max(area.getYsize(), area.getZsize())));

        AABB hitbox = dragon.getBoundingBox();
        double scale = dragon instanceof Player player ? player.getScale() : 1;

        IPMcHelper.traverseNearbyPortals(dragon.level(), dragon.position(), radius, portal -> {
            boolean known = result.portalCollisions.stream().anyMatch(entry -> entry.portal == portal);

            if (known) {
                return;
            }

            // Across the plane only the hitbox (plus half a block): Immersive Portals clips a colliding entity's model
            // by the whole plane, so a dragon standing BESIDE the portal whose model area reached into the portal's
            // rectangle lost everything behind the plane's extension (user report, 22 such collisions in the log).
            // Along the normal the model area, so a head poking through the portal is still projected.
            AABB test = acrossPlaneLikeHitbox(area, hitbox.inflate(0.5 * scale), portal.getNormal());

            if (portal.getBoundingBox().intersects(test) && CollisionHelper.mayEntityCollideWithPortal(dragon, portal, eyePos, test)) {
                result.portalCollisions.add(new PortalCollisionEntry(portal, time));
            }
        });

        PortalCollisionHandler collisions = result.portalCollisions.isEmpty() ? null : result;
        CACHE.put(dragon, new Cached(time, collisions));
        return collisions;
    }
}
