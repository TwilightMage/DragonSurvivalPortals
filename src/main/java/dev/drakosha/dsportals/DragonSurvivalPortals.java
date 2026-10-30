package dev.drakosha.dsportals;

import dev.drakosha.dsportals.DsPortalsConfig.Feature;
import dev.drakosha.dsportals.client.ThirdPersonCameraDistance;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Dragon Survival x Immersive Portals: makes Dragon Survival dragons work with Immersive Portals.
 *
 * All the actual work is done by mixins (see ds_immersive_portals.mixins.json), each feature can be
 * switched off in config/ds_immersive_portals.toml (DsPortalsConfig):
 *  - PacketRedirectionClientMixin: mod payloads that Immersive Portals redirects to another dimension
 *    see that dimension through player.level() (Dragon Survival sync of dragons seen through portals,
 *    NeoForge spawn data of dragon projectiles crossing portals).
 *  - DragonSizeHandlerMixin: Dragon Survival's pose-fit check respects Immersive Portals' portal-clipped
 *    collision box (dragons no longer get forced to crouch/crawl at portals).
 *  - Rendering: the whole dragon model is cut at portals and drawn on the other side
 *    (DragonPortalRenderArea), the first-person body is not drawn into portal views, the client dragon
 *    model survives crossings, Iris without a shader pack keeps cross-portal rendering on.
 *  - Breath through portals (BreathThroughPortals, BreathParticlePortals).
 *  - The third-person camera behind a portal keeps Dragon Survival's distance (ThirdPersonCameraDistance).
 */
@Mod(DragonSurvivalPortals.MOD_ID)
public final class DragonSurvivalPortals {
    public static final String MOD_ID = "ds_immersive_portals";

    public DragonSurvivalPortals() {
        if (FMLEnvironment.dist.isClient() && DsPortalsConfig.isApplied(Feature.THIRD_PERSON_CAMERA)) {
            ThirdPersonCameraDistance.register();
        }
    }
}
