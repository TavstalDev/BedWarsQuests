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

public class ObjectiveManager {
    private final PluginLogger _logger = BedWarsQuests.Logger().WithModule(ObjectiveManager.class);
    private final File dataFile;
    private final List<Achievement> objectives;

    public ObjectiveManager(PluginBase plugin) {
        this.dataFile = Paths.get(plugin.getDataFolder().getPath(), "objectives.yml").toFile();
        this.objectives = AchievementUtils.loadAchievements(dataFile, "objectives.yml");
    }

    public void registerObjective(Achievement achievement) {
        objectives.add(achievement);
    }

    public List<Achievement> getObjectives() {
        return new ArrayList<>(objectives);
    }

    public List<Achievement> getObjectivesByTrigger(String trigger) {
        List<Achievement> result = new ArrayList<>();
        for (Achievement achievement : objectives) {
            if (achievement.Triggers.contains(trigger)) {
                result.add(achievement);
            }
        }
        return result;
    }

    public Achievement getObjectiveByName(String name) {
        for (Achievement achievement : objectives) {
            if (achievement.Name.equalsIgnoreCase(name)) {
                return achievement;
            }
        }
        return null;
    }

    public Achievement getObjectiveById(String id) {
        for (Achievement achievement : objectives) {
            if (achievement.Id.equalsIgnoreCase(id)) {
                return achievement;
            }
        }
        return null;
    }

    public void save() {
        AchievementUtils.saveAchievements(dataFile, objectives);
    }
}
