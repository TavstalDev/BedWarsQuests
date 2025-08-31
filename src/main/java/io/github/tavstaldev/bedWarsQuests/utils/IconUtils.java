package io.github.tavstaldev.bedWarsQuests.utils;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import org.bukkit.Material;

public class IconUtils {

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

    public static Material getMaterialFromConfig(String configKey) {
        // Assuming you have a method to get the material name from your config
        String materialName = BedWarsQuests.Config().getString(configKey);
        return getMaterial(materialName);
    }
}
