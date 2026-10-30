package dev.drakosha.dsportals.breath;

import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.DragonAbilityInstance;
import by.dragonsurvivalteam.dragonsurvival.registry.dragon.ability.targeting.DragonBreathTarget;
import dev.drakosha.dsportals.mixin.EntityLevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import qouteall.imm_ptl.core.IPGlobal;
import qouteall.imm_ptl.core.IPMcHelper;
import qouteall.imm_ptl.core.collision.CollisionHelper;
import qouteall.imm_ptl.core.portal.Mirror;
import qouteall.imm_ptl.core.portal.Portal;

import java.util.List;
import java.util.function.Predicate;

/**
 * Dragon Survival's breath (DragonBreathTarget) hits the blocks and entities in a box in front of
 * the dragon, in the dragon's world only. At a portal that meant: nothing on the other side, but
 * the blocks and entities behind the portal in the dragon's own world (hidden by the portal) were
 * burnt. Now the part of the breath that goes into a portal is applied on the other side instead:
 * - in the dragon's world, what is behind a portal plane and seen through that portal from the
 *   dragon's eyes is left alone;
 * - for each portal the breath box reaches, the box is moved through the portal and the breath is
 *   applied in the destination world (the dragon's level is switched to it for the duration, since
 *   DS's effects use the dragon's level), to what is seen through the portal only.
 * Same logic as DS 2.0.71 DragonBreathTarget#apply otherwise.
 */
public final class BreathThroughPortals {
    private BreathThroughPortals() {
    }

    public static void apply(DragonBreathTarget breath, ServerPlayer dragon, DragonAbilityInstance ability) {
        AABB area = breath.calculateBreathArea(dragon, ability);
        Vec3 eye = dragon.getEyePosition();
        List<Portal> portals = IPMcHelper.getNearbyPortalList(dragon, IPGlobal.maxNormalPortalRadius,
            portal -> !(portal instanceof Mirror) && portal.isVisible() && portal.isInFrontOfPortal(eye) && portal.getBoundingBox().inflate(0.5).intersects(area));

        applyIn(breath, dragon, ability, area, point -> portals.stream().noneMatch(portal -> isSeenThrough(portal, eye, point)));

        for (Portal portal : portals) {
            if (!(portal.getDestWorld() instanceof ServerLevel destination)) {
                continue;
            }

            Level original = dragon.level();
            ((EntityLevelAccessor) dragon).dsip$setLevel(destination);

            try {
                applyIn(breath, dragon, ability, CollisionHelper.transformBox(portal, area),
                    point -> isSeenThrough(portal, eye, portal.inverseTransformPoint(point)));
            } finally {
                ((EntityLevelAccessor) dragon).dsip$setLevel(original);
            }
        }
    }

    /** point (in the portal's world) is behind the portal plane and the line from the eye to it goes through the portal. */
    private static boolean isSeenThrough(Portal portal, Vec3 eye, Vec3 point) {
        return portal.getDistanceToPlane(point) < 0 && portal.rayTrace(eye, point) != null;
    }

    private static void applyIn(DragonBreathTarget breath, ServerPlayer dragon, DragonAbilityInstance ability, AABB area, Predicate<Vec3> reaches) {
        breath.target().ifLeft(blockTarget -> {
            // DS: used by 'BlockGetter#clip' to determine the direction
            Direction direction = Direction.getNearest(dragon.getEyePosition());

            BlockPos.betweenClosedStream(area).forEach(position -> {
                if (reaches.test(Vec3.atCenterOf(position)) && blockTarget.matches(dragon, position)) {
                    blockTarget.effects().forEach(effect -> effect.apply(dragon, ability, position, direction));
                }
            });
        }).ifRight(entityTarget -> dragon.serverLevel().getEntities(EntityTypeTest.forClass(Entity.class), area,
            entity -> reaches.test(entity.getBoundingBox().getCenter())
                && entityTarget.targetingMode().isEntityRelevant(dragon, entity, entityTarget.isHarmful())
                && entityTarget.matches(dragon, entity, entity.position())
        ).forEach(entity -> entityTarget.effects().forEach(effect -> effect.apply(dragon, ability, entity))));
    }
}
