package io.github.tavstaldev.bedWarsQuests.utils;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.Achievement;
import io.github.tavstaldev.bedWarsQuests.models.AchievementCriteria;
import io.github.tavstaldev.bedWarsQuests.models.RewardAction;
import io.github.tavstaldev.bedWarsQuests.models.criterias.*;
import io.github.tavstaldev.bedWarsQuests.models.rewards.AchievementPointReward;
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

/**
 * Utility class for managing achievements in the BedWarsQuests plugin.
 * Provides methods to load and save achievements from/to YAML files.
 */
public class AchievementUtils {
    // Logger instance for logging messages related to AchievementUtils.
    private static final PluginLogger _logger = BedWarsQuests.Logger().withModule(AchievementUtils.class);

    /**
     * Loads achievements from the specified data file. If the file does not exist,
     * it attempts to copy a default resource file to the data folder.
     *
     * @param dataFile    The file to load achievements from.
     * @param resourceName The name of the default resource file to copy if the data file does not exist.
     * @return A list of loaded achievements.
     */
    public static List<Achievement> loadAchievements(File dataFile, String resourceName) {
        List<Achievement> achievements = new ArrayList<>();
        String fileName = dataFile.getName();
        if (!dataFile.exists()) {
            InputStream inputStream = BedWarsQuests.Instance.getResource(resourceName);
            if (inputStream == null) {
                _logger.error("Could not find default " + resourceName + " in plugin resources.");
                return achievements;
            }

            try {
                Files.copy(inputStream, dataFile.toPath());
                _logger.info("Default " + resourceName + " copied to plugin data folder.");
            } catch (Exception ex) {
                _logger.error("Failed to copy default " + resourceName + ": " + ex.getMessage());
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
                _logger.error("Invalid format in " + fileName + ": Root element is not a map.");
                return achievements;
            }

            Map<String, Object> yamlMap = TypeUtils.castAsMap(yamlObj, _logger);
            if (yamlMap == null) {
                _logger.error("Invalid format in " + fileName + ": Unable to cast root element to map.");
                return achievements;
            }

            if (!yamlMap.containsKey("data")) {
                _logger.error("Invalid format in " + fileName + ": Missing 'data' key.");
                return achievements;
            }

            Object achievementsObj = yamlMap.get("data");
            if (!(achievementsObj instanceof Map)) {
                _logger.error("Invalid format in " + fileName + ": 'data' is not a map.");
                return achievements;
            }

            Map<String, Object> achievementsMap = TypeUtils.castAsMap(achievementsObj, _logger);
            if (achievementsMap == null) {
                _logger.error("Invalid format in " + fileName + ": Unable to cast 'data' to map.");
                return achievements;
            }

            for (String key : achievementsMap.keySet()) {
                Object achievementDataObj = achievementsMap.get(key);
                if (!(achievementDataObj instanceof Map)) {
                    _logger.error("Invalid format for achievement '" + key + "': Not a map.");
                    continue;
                }

                Map<String, Object> achievementData = TypeUtils.castAsMap(achievementDataObj, _logger);
                if (achievementData == null) {
                    _logger.error("Invalid format for achievement '" + key + "': Unable to cast to map.");
                    continue;
                }

                String name = (String) achievementData.get("name");
                String description = (String) achievementData.get("description");
                List<String> triggers = TypeUtils.castAsList(achievementData.get("triggers"), _logger);
                if (triggers == null) {
                    _logger.error("Invalid or missing 'triggers' for achievement '" + key + "'.");
                    continue;
                }
                AchievementCriteria criteria;
                //#region Criteria
                if (!achievementData.containsKey("criteria")) {
                    _logger.error("Achievement '" + key + "' is missing 'criteria'.");
                    continue;
                }

                Map<String, Object> criteriaData = TypeUtils.castAsMap(achievementData.get("criteria"), _logger);
                if (criteriaData == null) {
                    _logger.error("Invalid format for criteria in achievement '" + key + "': Unable to cast to map.");
                    continue;
                }
                String type = (String) criteriaData.get("type");
                String operator = (String) criteriaData.getOrDefault("operator", "EQUALS");
                boolean singleMatch = (boolean) criteriaData.getOrDefault("single_match", false);
                switch (type) {
                    case "bed_break": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new BedBreakCriteria(operator, singleMatch, count);
                        break;
                    }
                    case "death": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new DeathCriteria(operator, singleMatch, count);
                        break;
                    }
                    case "kill": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new KillCriteria(operator, singleMatch, count);
                        break;
                    }
                    case "win": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new WinCriteria(operator, singleMatch, count);
                        break;
                    }
                    case "lose":
                    case "loss": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new LoseCriteria(operator, singleMatch, count);
                        break;
                    }
                    case "played": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new GamesPlayedCriteria(operator, singleMatch, count);
                        break;
                    }
                    case "block_place": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        String material = (String) criteriaData.get("material");
                        if (material == null) {
                            _logger.error("Missing 'material' for block_place criteria in achievement '" + key + "'.");
                            continue;
                        }
                        criteria = new BlockPlaceCriteria(operator, singleMatch, count, material);
                        break;
                    }
                    case "block_break": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        String material = (String) criteriaData.get("material");
                        if (material == null) {
                            _logger.error("Missing 'material' for block_break criteria in achievement '" + key + "'.");
                            continue;
                        }
                        criteria = new BlockBreakCriteria(operator, singleMatch, count, material);
                        break;
                    }
                    case "item_bought": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new ItemBoughtCriteria(operator, singleMatch, count);
                        break;
                    }
                    case "killstreak": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new KillStreakCriteria(operator, singleMatch, count);
                        break;
                    }
                    case "final_kill": {
                        int count = (int) criteriaData.getOrDefault("count", 1);
                        criteria = new FinalKillCriteria(operator, singleMatch, count);
                        break;
                    }
                    default: {
                        _logger.error("Unknown criteria type '" + type + "' in achievement '" + key + "'.");
                        continue;
                    }
                }
                //#endregion
                List<RewardAction> rewards = new ArrayList<>();
                //#region Rewards
                if (achievementData.containsKey("rewards")) {
                    Object rewardsObj = achievementData.get("rewards");
                    if (!(rewardsObj instanceof List)) {
                        _logger.error("Invalid format for rewards in achievement '" + key + "': Not a list.");
                        continue;
                    }

                    List<Map<String, Object>> rawRewards = TypeUtils.castAsListOfMaps(rewardsObj, _logger);
                    if (rawRewards == null) {
                        _logger.error("Invalid format for rewards in achievement '" + key + "': Unable to cast to list of maps.");
                        continue;
                    }

                    for (var reward : rawRewards) {
                        String rewardType = (String) reward.get("type");
                        switch (rewardType) {
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
                            case "points":
                            case "achievement_points": {
                                int points = (int) reward.getOrDefault("amount", 0);
                                rewards.add(new AchievementPointReward(points));
                                break;
                            }
                        }
                    }
                } else {
                    _logger.warn("Achievement '" + key + "' has no rewards defined.");
                }
                //#endregion

                Achievement achievement = new Achievement(key, name, description, triggers, criteria, rewards);
                achievements.add(achievement);
            }
        } catch (FileNotFoundException ex) {
            _logger.error(fileName + " not found: " + ex.getMessage());
            return new ArrayList<>();
        } catch (Exception ex) {
            _logger.error("Failed to load " + fileName + ": " + ex.getMessage());
            return new ArrayList<>();
        }
        return achievements;
    }

    /**
     * Saves the provided list of achievements to the specified data file.
     *
     * @param dataFile    The file to save achievements to.
     * @param achievements The list of achievements to save.
     */
    public static void saveAchievements(File dataFile, List<Achievement> achievements) {
        if (dataFile == null)
            return;

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Forces multi-line formatting
        dumperOptions.setIndent(2);
        Yaml yaml = new Yaml(dumperOptions);

        try (FileWriter writer = new FileWriter(dataFile)) {
            yaml.dump(achievements, writer);
        } catch (Exception ex) {
            _logger.error("Failed to save achievements.yml: " + ex.getMessage());
        }
    }
}