package io.github.tavstaldev.bedWarsQuests.utils;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import org.bukkit.Material;

/**
 * Utility class for handling icon-related operations, such as retrieving
 * materials by name or from configuration.
 */
public class IconUtils {

    /**
     * Retrieves a Material based on its name. If the name is null, empty, or
     * does not match any valid Material, a default Material (STONE) is returned.
     *
     * @param materialName The name of the material to retrieve.
     * @return The corresponding Material, or STONE if the name is invalid.
     */
    public static Material getMaterial(String materialName) {
        if (materialName == null || materialName.isEmpty()) {
            return Material.STONE; // Default material if name is null or empty
        }
        Material material = Material.matchMaterial(materialName);
        if (material == null) {
            material = Material.STONE; // Default material if not found
        }
        return material;
    }

    /**
     * Retrieves a Material based on a configuration key. The key is used to
     * fetch the material name from the configuration, and the material is
     * resolved using the {@link #getMaterial(String)} method.
     *
     * @param configKey The key in the configuration to fetch the material name.
     * @return The corresponding Material, or STONE if the key is invalid or
     *         the material name is not found.
     */
    public static Material getMaterialFromConfig(String configKey) {
        String materialName = BedWarsQuests.Config().getString(configKey);
        return getMaterial(materialName);
    }
}