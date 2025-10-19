package io.github.tavstaldev.bedWarsQuests.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.menu.SGMenu;
import io.github.tavstaldev.bedWarsQuests.BWQConfiguration;
import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.database.ObjectiveData;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.minecorelib.core.PluginTranslator;
import io.github.tavstaldev.minecorelib.utils.ChatUtils;
import io.github.tavstaldev.minecorelib.utils.GuiUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The MainGUI class is responsible for creating, opening, closing, and refreshing
 * the main GUI for players. It provides an interface for players to view and interact
 * with daily and weekly quests, as well as other options.
 */
public class MainGUI {
    // Logger instance for logging messages related to the MainGUI.
    private static final PluginLogger _logger = BedWarsQuests.Logger().withModule(MainGUI.class);

    // Translator instance for localizing messages and GUI elements.
    private static final PluginTranslator _translator = BedWarsQuests.Instance.getTranslator();

    // Number of rows in the main GUI.
    private static final Integer rows = 3;

    /**
     * Creates the main GUI for the specified player.
     *
     * @param player The player for whom the GUI is being created.
     * @return The created SGMenu instance representing the main GUI.
     */
    public static SGMenu create(@NotNull Player player) {
        try {
            SGMenu menu = BedWarsQuests.GUI().create(_translator.localize(player, "GUI.Main.Title"), rows);
            var playerId = player.getUniqueId();
            BWQConfiguration config = BedWarsQuests.Config();

            // Create placeholders for empty slots in the GUI.
            SGButton placeholderButton = new SGButton(GuiUtils.createItem(BedWarsQuests.Instance, config.guiPlaceholderItem, " "));
            int slots = rows * 9;
            for (int i = 0; i < slots; i++) {
                menu.setButton(0, i, placeholderButton);
            }

            // Add the title button to the GUI.
            List<Component> titleLore = new ArrayList<>();
            var rawRole = _translator.localizeList(player, "GUI.Main.Lore");
            for (String line : rawRole) {
                titleLore.add(ChatUtils.translateColors(line, true));
            }

            SGButton titleButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, config.guiTitleItem, _translator.localize(player, "GUI.Main.Item"), titleLore)
            );
            menu.setButton(0, 4, titleButton);

            // Add the close button to the GUI.
            SGButton closeButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, config.guiCloseItem, _translator.localize(player, "GUI.Close"))
            ).withListener(event -> close(player));
            menu.setButton(0, 18, closeButton);

            // Add the achievement button to the GUI.
            SGButton achievementButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, config.guiAchievementItem, _translator.localize(player, "GUI.Achievements.Item"))
            ).withListener(event -> {
                var data = PlayerCacheManager.get(playerId);
                close(player);
                data.setAchievementPage(1);
                AchievementGUI.open(player);
            });
            menu.setButton(0, 26, achievementButton);
            return menu;
        } catch (Exception ex) {
            _logger.error("An error occurred while creating the main GUI.");
            _logger.error(ex);
            return null;
        }
    }

    /**
     * Opens the main GUI for the specified player.
     *
     * @param player The player for whom the GUI is being opened.
     */
    public static void open(@NotNull Player player) {
        var playerCache = PlayerCacheManager.get(player.getUniqueId());
        // Show the GUI.
        playerCache.setGuiOpened(true);
        player.openInventory(playerCache.getMainMenu().getInventory());
        refresh(player);
    }

    /**
     * Closes the main GUI for the specified player.
     *
     * @param player The player for whom the GUI is being closed.
     */
    public static void close(@NotNull Player player) {
        var playerCache = PlayerCacheManager.get(player.getUniqueId());
        player.closeInventory();
        playerCache.setGuiOpened(false);
    }

    /**
     * Refreshes the main GUI for the specified player, updating its contents
     * based on the player's current daily and weekly objectives.
     *
     * @param player The player for whom the GUI is being refreshed.
     */
    public static void refresh(@NotNull Player player) {
        try {
            var playerId = player.getUniqueId();
            var playerCache = PlayerCacheManager.get(playerId);
            var menu = playerCache.getMainMenu();
            BWQConfiguration config = BedWarsQuests.Config();

            // Populate the GUI with daily quests.
            final var dailyObjectives = BedWarsQuests.Database().getPlayerDailyObjectives(playerId);
            for (int i = 0; i < 3; i++) {
                int slot = i + 10;

                if (i >= dailyObjectives.size()) {
                    menu.removeButton(slot);
                    continue;
                }

                ObjectiveData dailyObjectiveData = dailyObjectives.get(i);
                if (dailyObjectiveData == null) {
                    menu.removeButton(slot);
                    continue;
                }

                Material questMaterial = dailyObjectiveData.IsCompleted
                        ? config.guiCompletedDailyQuestItem
                        : config.guiDailyQuestItem;

                var objective = BedWarsQuests.ObjectiveManager().getObjectiveById(dailyObjectiveData.ObjectiveId);
                if (objective == null) {
                    _logger.warn("Objective with ID " + dailyObjectiveData.ObjectiveId + " not found for player " + player.getName());
                    menu.removeButton(slot);
                    continue;
                }

                var rawRole = _translator.localizeList(player, "GUI.DailyQuestLore");
                String[] descriptionLines = objective.Description.split("\n");
                List<Component> lore = new ArrayList<>();
                String status = _translator.localize(player, dailyObjectiveData.IsCompleted ? "GUI.Completed" : "GUI.InProgress");

                for (String line : rawRole) {
                    if (line.contains("%quest_description%")) {
                        for (String descLine : descriptionLines) {
                            lore.add(ChatUtils.translateColors(descLine, true));
                        }
                        continue;
                    }

                    if (line.contains("%reward%")) {
                        for (var reward : objective.Rewards) {
                            lore.add(ChatUtils.translateColors(reward.getLore(player), true));
                        }
                        continue;
                    }

                    lore.add(ChatUtils.translateColors(line.replace("%status%", status), true));
                }

                ItemStack stack = GuiUtils.createItem(BedWarsQuests.Instance, questMaterial, _translator.localize("GUI.DailyQuestName", Map.of("quest_name", objective.Name)), lore);
                menu.setButton(0, slot, new SGButton(stack));
            }

            // Populate the GUI with weekly quests.
            final var weeklyObjectives = BedWarsQuests.Database().getPlayerWeeklyObjectives(playerId);
            for (int i = 0; i < 3; i++) {
                int slot = i + 14;

                if (i >= weeklyObjectives.size()) {
                    menu.removeButton(slot);
                    continue;
                }

                ObjectiveData objectiveData = weeklyObjectives.get(i);
                if (objectiveData == null) {
                    menu.removeButton(slot);
                    continue;
                }

                Material questMaterial = objectiveData.IsCompleted
                        ? config.guiCompletedWeeklyQuestItem
                        : config.guiWeeklyQuestItem;

                var objective = BedWarsQuests.ObjectiveManager().getObjectiveById(objectiveData.ObjectiveId);
                if (objective == null) {
                    _logger.warn("Objective with ID " + objectiveData.ObjectiveId + " not found for player " + player.getName());
                    menu.removeButton(slot);
                    continue;
                }

                var rawRole = _translator.localizeList(player, "GUI.WeeklyQuestLore");
                String[] descriptionLines = objective.Description.split("\n");
                List<Component> lore = new ArrayList<>();
                String status = _translator.localize(player, objectiveData.IsCompleted ? "GUI.Completed" : "GUI.InProgress");

                for (String line : rawRole) {
                    if (line.contains("%quest_description%")) {
                        for (String descLine : descriptionLines) {
                            lore.add(ChatUtils.translateColors(descLine, true));
                        }
                        continue;
                    }

                    if (line.contains("%reward%")) {
                        for (var reward : objective.Rewards) {
                            lore.add(ChatUtils.translateColors(reward.getLore(player), true));
                        }
                        continue;
                    }

                    lore.add(ChatUtils.translateColors(line.replace("%status%", status), true));
                }

                ItemStack stack = GuiUtils.createItem(BedWarsQuests.Instance, questMaterial, _translator.localize("GUI.WeeklyQuestName", Map.of("quest_name", objective.Name)), lore);
                menu.setButton(0, slot, new SGButton(stack));
            }

            player.openInventory(menu.getInventory());
        } catch (Exception ex) {
            _logger.error("An error occurred while refreshing the main GUI.");
            _logger.error(ex);
        }
    }
}