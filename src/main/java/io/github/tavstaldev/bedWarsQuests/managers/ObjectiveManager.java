package io.github.tavstaldev.bedWarsQuests.managers;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.Achievement;
import io.github.tavstaldev.bedWarsQuests.utils.AchievementUtils;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import org.bukkit.plugin.PluginBase;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The ObjectiveManager class is responsible for managing objectives in the plugin.
 * It provides functionality to load, register, retrieve, and save objectives.
 */
public class ObjectiveManager {
    // Logger instance for logging messages related to the ObjectiveManager.
    private final PluginLogger _logger = BedWarsQuests.Logger().withModule(ObjectiveManager.class);

    // File where objective data is stored.
    private final File dataFile;

    // List of all loaded objectives.
    private final List<Achievement> objectives;

    /**
     * Constructs an ObjectiveManager instance and loads objectives from the data file.
     *
     * @param plugin The plugin instance used to determine the data folder location.
     */
    public ObjectiveManager(PluginBase plugin) {
        this.dataFile = Paths.get(plugin.getDataFolder().getPath(), "objectives.yml").toFile();
        this.objectives = AchievementUtils.loadAchievements(dataFile, "objectives.yml");
    }

    /**
     * Registers a new objective by adding it to the list of objectives.
     *
     * @param achievement The objective to be registered.
     */
    public void registerObjective(Achievement achievement) {
        objectives.add(achievement);
    }

    /**
     * Retrieves a list of all objectives.
     *
     * @return A new list containing all objectives.
     */
    public List<Achievement> getObjectives() {
        return new ArrayList<>(objectives);
    }

    /**
     * Retrieves a list of objectives that are triggered by the specified trigger.
     *
     * @param trigger The trigger to filter objectives by.
     * @return A list of objectives matching the specified trigger.
     */
    public List<Achievement> getObjectivesByTrigger(String trigger) {
        List<Achievement> result = new ArrayList<>();
        for (Achievement achievement : objectives) {
            if (achievement.Triggers.contains(trigger)) {
                result.add(achievement);
            }
        }
        return result;
    }

    /**
     * Retrieves an objective by its name.
     *
     * @param name The name of the objective to retrieve.
     * @return The objective with the specified name, or null if not found.
     */
    public Achievement getObjectiveByName(String name) {
        for (Achievement achievement : objectives) {
            if (achievement.Name.equalsIgnoreCase(name)) {
                return achievement;
            }
        }
        return null;
    }

    /**
     * Retrieves an objective by its unique ID.
     *
     * @param id The ID of the objective to retrieve.
     * @return The objective with the specified ID, or null if not found.
     */
    public Achievement getObjectiveById(String id) {
        for (Achievement achievement : objectives) {
            if (achievement.Id.equalsIgnoreCase(id)) {
                return achievement;
            }
        }
        return null;
    }

    /**
     * Saves the current list of objectives to the data file.
     */
    public void save() {
        AchievementUtils.saveAchievements(dataFile, objectives);
    }
}