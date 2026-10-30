package dev.drakosha.dsportals.client;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Mods (Dragon Survival for dragons) change the third-person camera distance through
 * CalculateDetachedCameraDistanceEvent. When the camera ends up behind a portal, Immersive Portals
 * places it again at the vanilla distance (4 blocks times the entity scale), so a dragon's camera
 * jumped closer. The event's result is remembered here and applied to that distance.
 */
public final class ThirdPersonCameraDistance {
    // The event starts from 4 blocks; Camera#setup multiplies its result by the entity scale afterwards
    private static final double VANILLA_EVENT_DISTANCE = 4.0;

    private static double multiplier = 1;

    private ThirdPersonCameraDistance() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, ThirdPersonCameraDistance::onCalculateDistance);
    }

    /** How much other mods changed the detached camera distance compared to vanilla. */
    public static double multiplier() {
        return multiplier;
    }

    private static void onCalculateDistance(CalculateDetachedCameraDistanceEvent event) {
        multiplier = event.getDistance() / VANILLA_EVENT_DISTANCE;
    }
}
