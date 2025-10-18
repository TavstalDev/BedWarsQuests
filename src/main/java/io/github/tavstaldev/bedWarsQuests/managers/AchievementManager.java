package io.github.tavstaldev.bedWarsQuests.managers;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.Achievement;
import io.github.tavstaldev.bedWarsQuests.models.criterias.*;
import io.github.tavstaldev.bedWarsQuests.utils.AchievementUtils;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import org.bukkit.plugin.PluginBase;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The AchievementManager class is responsible for managing achievements in the plugin.
 * It provides functionality to load, register, retrieve, and save achievements.
 */
public class AchievementManager {
    // Logger instance for logging messages related to the AchievementManager.
    private final PluginLogger _logger = BedWarsQuests.Logger().withModule(AchievementManager.class);

    // File where achievement data is stored.
    private final File dataFile;

    // List of all loaded achievements.
    private final List<Achievement> achievements;

    /**
     * Constructs an AchievementManager instance and loads achievements from the data file.
     *
     * @param plugin The plugin instance used to determine the data folder location.
     */
    public AchievementManager(PluginBase plugin) {
        this.dataFile = Paths.get(plugin.getDataFolder().getPath(), "achievements.yml").toFile();
        this.achievements = AchievementUtils.loadAchievements(dataFile, "achievements.yml");
    }

    /**
     * Registers a new achievement by adding it to the list of achievements.
     *
     * @param achievement The achievement to be registered.
     */
    public void registerAchievement(Achievement achievement) {
        achievements.add(achievement);
    }

    /**
     * Retrieves a list of all achievements.
     *
     * @return A new list containing all achievements.
     */
    public List<Achievement> getAchievements() {
        return new ArrayList<>(achievements);
    }

    /**
     * Retrieves a list of achievements that are triggered by the specified trigger.
     *
     * @param trigger The trigger to filter achievements by.
     * @return A list of achievements matching the specified trigger.
     */
    public List<Achievement> getAchievementsByTrigger(String trigger) {
        List<Achievement> result = new ArrayList<>();
        for (Achievement achievement : achievements) {
            if (achievement.Triggers.contains(trigger)) {
                result.add(achievement);
            }
        }
        return result;
    }

    /**
     * Retrieves an achievement by its name.
     *
     * @param name The name of the achievement to retrieve.
     * @return The achievement with the specified name, or null if not found.
     */
    public Achievement getAchievementByName(String name) {
        for (Achievement achievement : achievements) {
            if (achievement.Name.equalsIgnoreCase(name)) {
                return achievement;
            }
        }
        return null;
    }

    /**
     * Retrieves an achievement by its unique ID.
     *
     * @param id The ID of the achievement to retrieve.
     * @return The achievement with the specified ID, or null if not found.
     */
    public Achievement getAchievementById(String id) {
        for (Achievement achievement : achievements) {
            if (achievement.Id.equalsIgnoreCase(id)) {
                return achievement;
            }
        }
        return null;
    }

    /**
     * Saves the current list of achievements to the data file.
     */
    public void save() {
        AchievementUtils.saveAchievements(dataFile, achievements);
    }
}
