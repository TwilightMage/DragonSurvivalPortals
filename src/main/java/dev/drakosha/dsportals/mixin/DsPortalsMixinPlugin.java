package dev.drakosha.dsportals.mixin;

import dev.drakosha.dsportals.DsPortalsConfig;
import dev.drakosha.dsportals.DsPortalsConfig.Feature;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Skips mixins whose feature is switched off in the config (DsPortalsConfig), the general Immersive
 * Portals fixes when Immersive Portals Improvements (which carries the same fixes) is installed, or
 * all of them, for testing.
 */
public final class DsPortalsMixinPlugin implements IMixinConfigPlugin {
    private static final String MIXIN_PACKAGE = "dev.drakosha.dsportals.mixin.";
    private static final String DISABLE_PROPERTY = "ds_immersive_portals.disable";

    /** Mixin (relative to the mixin package) -> the feature it belongs to. Not listed: always applied (accessors). */
    private static final Map<String, Feature> FEATURES = Map.ofEntries(
        Map.entry("client.PacketRedirectionClientMixin", Feature.DRAGON_SYNC),
        Map.entry("DragonSizeHandlerMixin", Feature.DRAGON_HITBOX),
        Map.entry("client.FirstPersonBodyMixin", Feature.DRAGON_RENDERING),
        Map.entry("client.ClientDragonRendererMixin", Feature.DRAGON_RENDERING),
        Map.entry("client.CrossPortalEntityRendererDragonMixin", Feature.DRAGON_RENDERING),
        Map.entry("breath.DragonBreathTargetMixin", Feature.DRAGON_BREATH),
        Map.entry("breath.client.ClientProxyBreathMixin", Feature.DRAGON_BREATH),
        Map.entry("breath.client.ParticleEngineBreathMixin", Feature.DRAGON_BREATH),
        Map.entry("client.CrossPortalViewRenderingMixin", Feature.THIRD_PERSON_CAMERA),
        Map.entry("client.IrisShaderPackMixin", Feature.IRIS_PROJECTIONS)
    );

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Test switch (gradle -PdisableMod): the mod loads but changes nothing
        if (Boolean.getBoolean(DISABLE_PROPERTY)) {
            return false;
        }

        Feature feature = FEATURES.get(mixinClassName.substring(MIXIN_PACKAGE.length()));

        if (feature == null) {
            return true;
        }

        return DsPortalsConfig.isApplied(feature);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
