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

public class AchievementManager {
    private final PluginLogger _logger = BedWarsQuests.Logger().WithModule(AchievementManager.class);
    private final File dataFile;
    private final List<Achievement> achievements;

    public AchievementManager(PluginBase plugin) {
        this.dataFile = Paths.get(plugin.getDataFolder().getPath(), "achievements.yml").toFile();
        this.achievements = AchievementUtils.loadAchievements(dataFile, "achievements.yml");
    }

    public void registerAchievement(Achievement achievement) {
        achievements.add(achievement);
    }

    public List<Achievement> getAchievements() {
        return new ArrayList<>(achievements);
    }

    public List<Achievement> getAchievementsByTrigger(String trigger) {
        List<Achievement> result = new ArrayList<>();
        for (Achievement achievement : achievements) {
            if (achievement.Triggers.contains(trigger)) {
                result.add(achievement);
            }
        }
        return result;
    }

    public Achievement getAchievementByName(String name) {
        for (Achievement achievement : achievements) {
            if (achievement.Name.equalsIgnoreCase(name)) {
                return achievement;
            }
        }
        return null;
    }

    public Achievement getAchievementById(String id) {
        for (Achievement achievement : achievements) {
            if (achievement.Id.equalsIgnoreCase(id)) {
                return achievement;
            }
        }
        return null;
    }

    public void save() {
        AchievementUtils.saveAchievements(dataFile, achievements);
    }
}
