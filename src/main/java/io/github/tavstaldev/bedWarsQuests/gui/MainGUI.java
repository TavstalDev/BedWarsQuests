package io.github.tavstaldev.bedWarsQuests.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.menu.SGMenu;
import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.managers.ObjectiveManager;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.database.DailyObjectiveData;
import io.github.tavstaldev.bedWarsQuests.models.database.WeeklyObjectiveData;
import io.github.tavstaldev.bedWarsQuests.utils.IconUtils;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.minecorelib.core.PluginTranslator;
import io.github.tavstaldev.minecorelib.utils.ChatUtils;
import io.github.tavstaldev.minecorelib.utils.GuiUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainGUI {
    private static final PluginLogger _logger = BedWarsQuests.Logger().WithModule(MainGUI.class);
    private static final PluginTranslator _translator = BedWarsQuests.Instance.getTranslator();
    private static final  Integer Rows = 3;

    public static SGMenu create(@NotNull Player player) {
        try {
            SGMenu menu = BedWarsQuests.GUI().create(_translator.Localize(player, "GUI.Main.Title"), Rows);
            var playerId = player.getUniqueId();

            // Create Placeholders
            Material placeholderMaterial = IconUtils.getMaterialFromConfig("gui.placeholderItem");
            SGButton placeholderButton = new SGButton(GuiUtils.createItem(BedWarsQuests.Instance, placeholderMaterial, " "));
            int slots = Rows * 9;
            for (int i = 0; i < slots; i++) {
                menu.setButton(0, i, placeholderButton);
            }

            // Close Button
            Material closeMaterial = IconUtils.getMaterialFromConfig("gui.closeItem");
            SGButton closeButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, closeMaterial, _translator.Localize(player, "GUI.Close"))
            ).withListener(event -> close(player));
            menu.setButton(0, 18, closeButton);


            // Achievement Button
            Material achievementMaterial = IconUtils.getMaterialFromConfig("gui.achievementItem");
            SGButton achievementButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, achievementMaterial, _translator.Localize(player, "GUI.AchievementsItem"))
            ).withListener(event -> {
                var data = PlayerCacheManager.get(playerId);
                close(player);
                data.setAchievementPage(1);
                AchievementGUI.open(player);
            });
            menu.setButton(0, 26, achievementButton);
            return menu;
        }
        catch (Exception ex) {
            _logger.Error("An error occurred while creating the main GUI.");
            _logger.Error(ex);
            return null;
        }
    }

    public static void open(@NotNull Player player) {
        var playerCache = PlayerCacheManager.get(player.getUniqueId());
        // Show the GUI
        playerCache.setGuiOpened(true);
        player.openInventory(playerCache.getMainMenu().getInventory());
        refresh(player);
    }

    public static void close(@NotNull Player player) {
        var playerCache = PlayerCacheManager.get(player.getUniqueId());
        player.closeInventory();
        playerCache.setGuiOpened(false);
    }

    public static void refresh(@NotNull Player player) {
        try {
            var playerId = player.getUniqueId();
            var playerCache = PlayerCacheManager.get(playerId);
            var menu = playerCache.getMainMenu();

            // Daily Quests
            for (int i = 0; i < 3; i++) {
                int slot = i + 10;

                DailyObjectiveData dailyObjectiveData = playerCache.getDailyObjectives().get(i);
                if (dailyObjectiveData == null)
                {
                    menu.setButton(0, slot, null);
                    continue;
                }

                Material questMaterial;
                if (dailyObjectiveData.IsCompleted)
                    questMaterial = IconUtils.getMaterialFromConfig("gui.completedDailyQuestItem");
                else
                    questMaterial = IconUtils.getMaterialFromConfig("gui.dailyQuestItem");

                var objective = BedWarsQuests.ObjectiveManager().getObjectiveById(dailyObjectiveData.ObjectiveId);

                String[] rawRole = _translator.LocalizeArray(player, "GUI.DailyQuestLore");
                String[] descriptionLines = objective.Description.split("\n");
                List<Component> lore = new ArrayList<>();
                String status = _translator.Localize(player, dailyObjectiveData.IsCompleted ? "GUI.Completed" : "GUI.InProgress");
                for (String line : rawRole) {
                    if (line.contains("%quest_description%")) {
                        for (String descLine : descriptionLines) {
                            lore.add(ChatUtils.translateColors(descLine, true));
                        }
                        continue;
                    }

                    if (line.contains("%reward%"))
                    {
                        for (var reward : objective.Rewards) {
                            lore.add(ChatUtils.translateColors(_translator.Localize("GUI.RewardFormat", Map.of(
                                    "reward_amount", "TODO",
                                    "reward_name", "TODO"
                                    )), true));
                        }
                        continue;
                    }

                    lore.add(ChatUtils.translateColors(line.replace("%status%", status), true));
                }

                ItemStack stack = GuiUtils.createItem(BedWarsQuests.Instance, questMaterial, _translator.Localize("GUI.DailyQuestName", Map.of("quest_name", objective.Name)), lore);
                menu.setButton(0, slot, new SGButton(stack));
            }

            // Weekly Quests
            for (int i = 0; i < 3; i++) {
                int slot = i + 14;

                WeeklyObjectiveData weeklyObjectiveData = playerCache.getWeeklyObjectives().get(i);
                if (weeklyObjectiveData == null)
                {
                    menu.setButton(0, slot, null);
                    continue;
                }

                Material questMaterial;
                if (weeklyObjectiveData.IsCompleted)
                    questMaterial = IconUtils.getMaterialFromConfig("gui.completedWeeklyQuestItem");
                else
                    questMaterial = IconUtils.getMaterialFromConfig("gui.weeklyQuestItem");

                var objective = BedWarsQuests.ObjectiveManager().getObjectiveById(weeklyObjectiveData.ObjectiveId);
                String[] rawRole = _translator.LocalizeArray(player, "GUI.WeeklyQuestLore");
                String[] descriptionLines = objective.Description.split("\n");
                List<Component> lore = new ArrayList<>();
                String status = _translator.Localize(player, weeklyObjectiveData.IsCompleted ? "GUI.Completed" : "GUI.InProgress");
                for (String line : rawRole) {
                    if (line.contains("%quest_description%")) {
                        for (String descLine : descriptionLines) {
                            lore.add(ChatUtils.translateColors(descLine, true));
                        }
                        continue;
                    }

                    if (line.contains("%reward%"))
                    {
                        for (var reward : objective.Rewards) {
                            lore.add(ChatUtils.translateColors(_translator.Localize("GUI.RewardFormat", Map.of(
                                    "reward_amount", "TODO",
                                    "reward_name", "TODO"
                            )), true));
                        }
                        continue;
                    }

                    lore.add(ChatUtils.translateColors(line.replace("%status%", status), true));
                }


                ItemStack stack = GuiUtils.createItem(BedWarsQuests.Instance, questMaterial, _translator.Localize("GUI.WeeklyQuestName", Map.of("quest_name", objective.Name)), lore);
                menu.setButton(0, slot, new SGButton(stack));
            }

            player.openInventory(menu.getInventory());
        }
        catch (Exception ex) {
            _logger.Error("An error occurred while refreshing the main GUI.");
            _logger.Error(ex);
        }
    }
}
