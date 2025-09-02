package io.github.tavstaldev.bedWarsQuests.utils;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.Achievement;
import io.github.tavstaldev.bedWarsQuests.models.AchievementCriteria;
import io.github.tavstaldev.bedWarsQuests.models.RewardAction;
import io.github.tavstaldev.bedWarsQuests.models.criterias.*;
import io.github.tavstaldev.bedWarsQuests.models.rewards.CoinsReward;
import io.github.tavstaldev.bedWarsQuests.models.rewards.CommandReward;
import io.github.tavstaldev.bedWarsQuests.models.rewards.VaultReward;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.minecorelib.utils.TypeUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AchievementUtils {
    private static final PluginLogger _logger = BedWarsQuests.Logger().WithModule(AchievementUtils.class);

    public static List<Achievement> loadAchievements(File dataFile, String resourceName) {
        List<Achievement> achievements = new ArrayList<>();
        String fileName = dataFile.getName();
        if (!dataFile.exists()) {
            InputStream inputStream = BedWarsQuests.Instance.getResource(resourceName);
            if (inputStream == null) {
                _logger.Error("Could not find default "+resourceName+" in plugin resources.");
                return achievements;
            }

            try {
                Files.copy(inputStream, dataFile.toPath());
                _logger.Info("Default "+ resourceName +" copied to plugin data folder.");
            } catch (Exception ex) {
                _logger.Error("Failed to copy default "+resourceName+": " + ex.getMessage());
                return achievements;
            }
        }

        try {
            FileInputStream achievementsStream = new FileInputStream(dataFile);
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Forces multi-line formatting
            dumperOptions.setIndent(2);
            Yaml yaml = new Yaml(dumperOptions);
            Object yamlObj = yaml.load(achievementsStream);

            if (!(yamlObj instanceof Map)) {
                _logger.Error("Invalid format in "+fileName+": Root element is not a map.");
                return achievements;
            }

            Map<String, Object> yamlMap = TypeUtils.castAsMap(yamlObj, _logger);
            if (yamlMap == null) {
                _logger.Error("Invalid format in "+fileName+": Unable to cast root element to map.");
                return achievements;
            }

            if (!yamlMap.containsKey("achievements")) {
                _logger.Error("Invalid format in "+fileName+": Missing 'achievements' key.");
                return achievements;
            }

            Object achievementsObj = yamlMap.get("achievements");
            if (!(achievementsObj instanceof Map))
            {
                _logger.Error("Invalid format in "+fileName+": 'achievements' is not a map.");
                return achievements;
            }

            Map<String, Object> achievementsMap = TypeUtils.castAsMap(achievementsObj, _logger);
            if (achievementsMap == null) {
                _logger.Error("Invalid format in "+fileName+": Unable to cast 'achievements' to map.");
                return achievements;
            }

            for (String key : achievementsMap.keySet()) {
                Object achievementDataObj = achievementsMap.get(key);
                if (!(achievementDataObj instanceof Map)) {
                    _logger.Error("Invalid format for achievement '" + key + "': Not a map.");
                    continue;
                }

                Map<String, Object> achievementData = TypeUtils.castAsMap(achievementDataObj, _logger);
                if (achievementData == null) {
                    _logger.Error("Invalid format for achievement '" + key + "': Unable to cast to map.");
                    continue;
                }

                String name = (String) achievementData.get("name");
                String description = (String) achievementData.get("description");
                List<String> triggers = TypeUtils.castAsList(achievementData.get("triggers"), _logger);
                if (triggers == null) {
                    _logger.Error("Invalid or missing 'triggers' for achievement '" + key + "'.");
                    continue;
                }
                AchievementCriteria criteria;
                //#region Criteria
                if (!achievementData.containsKey("criteria")) {
                    _logger.Error("Achievement '" + key + "' is missing 'criteria'.");
                    continue;
                }


                Map<String, Object> criteriaData = TypeUtils.castAsMap(achievementData.get("criteria"), _logger);
                if (criteriaData == null) {
                    _logger.Error("Invalid format for criteria in achievement '" + key + "': Unable to cast to map.");
                    continue;
                }
                String type = (String) criteriaData.get("type");
                switch (type) {
                    case "bed_break": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new BedBreakCriteria(count);
                        break;
                    }
                    case "death": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new DeathCriteria(count);
                        break;
                    }
                    case "kill": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new KillCriteria(count);
                        break;
                    }
                    case "win": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new WinCriteria(count);
                        break;
                    }
                    case "lose":
                    case "loss": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new LoseCriteria(count);
                        break;
                    }
                    default: {
                        _logger.Error("Unknown criteria type '" + type + "' in achievement '" + key + "'.");
                        continue;
                    }
                }
                //#endregion
                List<RewardAction> rewards = new ArrayList<>();
                //#region Rewards
                if (achievementData.containsKey("rewards")) {
                    Object rewardsObj = achievementData.get("rewards");
                    if (!(rewardsObj instanceof List)) {
                        _logger.Error("Invalid format for rewards in achievement '" + key + "': Not a list.");
                        continue;
                    }

                    List<Map<String, Object>> rawRewards = TypeUtils.castAsListOfMaps(rewardsObj, _logger);
                    if (rawRewards == null) {
                        _logger.Error("Invalid format for rewards in achievement '" + key + "': Unable to cast to list of maps.");
                        continue;
                    }

                    for (var reward : rawRewards) {
                        String rewardType = (String) reward.get("type");
                        switch (rewardType)
                        {
                            case "vault": {
                                double amount = ((Number) reward.getOrDefault("amount", 0)).doubleValue();
                                rewards.add(new VaultReward(amount));
                                break;
                            }
                            case "coins": {
                                int coins = (int) reward.getOrDefault("amount", 0);
                                rewards.add(new CoinsReward(coins));
                                break;
                            }
                            case "command": {
                                var command = (String) reward.get("command");
                                var asConsole = (boolean) reward.getOrDefault("asConsole", false);
                                rewards.add(new CommandReward(command, asConsole));
                                break;
                            }
                        }
                    }
                }
                else {
                    _logger.Warn("Achievement '" + key + "' has no rewards defined.");
                }
                //#endregion

                Achievement achievement = new Achievement(key, name, description, triggers, criteria, rewards);
                achievements.add(achievement);
            }
        }
        catch (FileNotFoundException ex) {
            _logger.Error(fileName+" not found: " + ex.getMessage());
            return new ArrayList<>();
        }
        catch (Exception ex) {
            _logger.Error("Failed to load "+fileName+": " + ex.getMessage());
            return new ArrayList<>();
        }
        return achievements;
    }

    public static void saveAchievements(File dataFile, List<Achievement> achievements) {
        if (dataFile == null)
            return;

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Forces multi-line formatting
        dumperOptions.setIndent(2);
        Yaml yaml = new Yaml(dumperOptions);

        try (FileWriter writer = new FileWriter(dataFile)) {
            yaml.dump(achievements, writer);
        }
        catch (Exception ex) {
            _logger.Error("Failed to save achievements.yml: " + ex.getMessage());
        }
    }
}
