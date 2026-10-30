package dev.drakosha.dsportals;

import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Feature switches in config/ds_immersive_portals.toml. They are read while mixins are being
 * applied (DsPortalsMixinPlugin), before the game starts: a disabled feature is not patched into
 * anything at all, so the game also starts when an update of a patched mod broke that feature.
 * Changes need a restart. Missing switches are added with their default (on) and their description.
 */
public final class DsPortalsConfig {
    public enum Feature {
        DRAGON_SYNC("dragon_sync",
            "Mod packets about entities seen in another dimension through a portal are handled in that dimension.",
            "Fixes dragons seen through a portal looking like humans (no size, wings, flight) and dragon projectiles crashing or vanishing when crossing.",
            "Done by Immersive Portals Improvements instead when it is installed.",
            "Пакеты модов о сущностях, видимых через портал в другом измерении, обрабатываются в том измерении.",
            "Чинит: драконы за порталом выглядят людьми (без размера, крыльев, полёта), снаряды драконов падают/исчезают при пересечении.",
            "Если установлен Immersive Portals Improvements, это делает он."),
        DRAGON_HITBOX("dragon_hitbox",
            "Dragon Survival's \"does the dragon fit\" check uses the part of the hitbox in front of the portal.",
            "Fixes dragons being forced to crouch/crawl or getting stuck when walking into a portal.",
            "Проверка DS «помещается ли дракон» учитывает только часть хитбокса перед порталом.",
            "Чинит: дракон приседает/ползёт или застревает, заходя в портал."),
        DRAGON_RENDERING("dragon_rendering",
            "Dragons are drawn correctly at portals: the whole model (not just the small hitbox) is cut at the portal and shown on the other side,",
            "the first-person dragon body is not drawn into portal views, and the dragon model is not recreated on every crossing.",
            "Драконы корректно рисуются у портала: вся модель (а не маленький хитбокс) режется порталом и видна с другой стороны,",
            "тело дракона от первого лица не рисуется в видах через портал, модель не пересоздаётся при каждом проходе."),
        DRAGON_BREATH("dragon_breath",
            "Dragon breath (any Dragon Survival breath ability) goes through portals: it affects what is seen through the portal",
            "on the other side and its particles fly out of the portal; what is behind the portal in the dragon's own world is not hit.",
            "Fixes breath stopping at a portal and hitting blocks/mobs hidden behind the portal instead.",
            "Дыхание дракона (любое дыхание DS) проходит сквозь портал: действует на то, что видно через портал на той стороне,",
            "частицы вылетают из портала; то, что за порталом в мире дракона, не задевается.",
            "Чинит: дыхание обрывается у портала и задевает блоки/мобов, спрятанных за порталом."),
        THIRD_PERSON_CAMERA("third_person_camera",
            "The third-person camera behind a portal keeps the distance Dragon Survival sets for big dragons.",
            "Fixes the camera jumping close to the dragon when it passes behind a portal.",
            "Done by Immersive Portals Improvements instead when it is installed.",
            "Камера от третьего лица за порталом сохраняет расстояние, которое DS задаёт для больших драконов.",
            "Чинит: камера прыгает к дракону, когда оказывается за порталом.",
            "Если установлен Immersive Portals Improvements, это делает он."),
        IRIS_PROJECTIONS("iris_projections",
            "Immersive Portals turns off drawing entities that stick through portals whenever Iris is installed; this keeps it on unless a shader pack is active.",
            "Without it the dragon rendering fixes are invisible when Iris is installed.",
            "Done by Immersive Portals Improvements instead when it is installed.",
            "Immersive Portals выключает отрисовку сущностей, торчащих сквозь портал, если просто установлен Iris; здесь она выключается только при включённом шейдерпаке.",
            "Без этого при установленном Iris не видны исправления отрисовки драконов.",
            "Если установлен Immersive Portals Improvements, это делает он.");

        public final String key;
        private final String[] description;

        Feature(String key, String... description) {
            this.key = key;
            this.description = description;
        }
    }

    /** General Immersive Portals fixes that Immersive Portals Improvements applies itself (twice would add up). */
    private static final Set<Feature> SHARED_WITH_IMPROVEMENTS = Set.of(Feature.DRAGON_SYNC, Feature.THIRD_PERSON_CAMERA, Feature.IRIS_PROJECTIONS);
    private static final String IMPROVEMENTS_MOD_ID = "immersive_portals_improvements";

    private static final String FILE_NAME = "ds_immersive_portals.toml";
    private static Map<Feature, Boolean> values;

    private DsPortalsConfig() {
    }

    public static synchronized boolean isEnabled(Feature feature) {
        if (values == null) {
            values = load(FMLPaths.CONFIGDIR.get().resolve(FILE_NAME));
        }

        return values.get(feature);
    }

    /** Whether this mod does the feature: switched on, and not left to Immersive Portals Improvements. */
    public static boolean isApplied(Feature feature) {
        if (SHARED_WITH_IMPROVEMENTS.contains(feature)) {
            LoadingModList modList = LoadingModList.get();

            if (modList != null && modList.getModFileById(IMPROVEMENTS_MOD_ID) != null) {
                return false;
            }
        }

        return isEnabled(feature);
    }

    private static Map<Feature, Boolean> load(Path file) {
        Map<Feature, Boolean> result = new EnumMap<>(Feature.class);
        boolean complete = true;

        try {
            List<String> lines = Files.exists(file) ? Files.readAllLines(file, StandardCharsets.UTF_8) : List.of();

            for (Feature feature : Feature.values()) {
                Boolean value = readValue(lines, feature.key);
                complete &= value != null;
                result.put(feature, value == null || value);
            }

            if (!complete) {
                write(file, result);
            }
        } catch (IOException e) {
            // unreadable/unwritable config: everything on, as without the file
            for (Feature feature : Feature.values()) {
                result.putIfAbsent(feature, true);
            }
        }

        return result;
    }

    private static Boolean readValue(List<String> lines, String key) {
        for (String line : lines) {
            String text = line.strip();
            int comment = text.indexOf('#');

            if (comment >= 0) {
                text = text.substring(0, comment).strip();
            }

            int equals = text.indexOf('=');

            if (equals > 0 && text.substring(0, equals).strip().equals(key)) {
                String value = text.substring(equals + 1).strip();

                if (value.equals("true") || value.equals("false")) {
                    return Boolean.parseBoolean(value);
                }
            }
        }

        return null;
    }

    private static void write(Path file, Map<Feature, Boolean> values) throws IOException {
        List<String> lines = new ArrayList<>(List.of(
            "# Dragon Survival x Immersive Portals - feature switches / выключатели исправлений",
            "# false = the patch is not applied at all (also when an update of a patched mod broke it). Restart the game after changing.",
            "# false = патч вообще не встраивается (в том числе если обновление мода его сломало). После изменения перезапустите игру.",
            ""
        ));

        for (Feature feature : Feature.values()) {
            for (String description : feature.description) {
                lines.add("# " + description);
            }
            lines.add(feature.key + " = " + values.get(feature));
            lines.add("");
        }

        Files.createDirectories(file.getParent());
        Files.write(file, lines, StandardCharsets.UTF_8);
    }
}
