package io.github.tavstaldev.bedWarsQuests.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.menu.SGMenu;
import io.github.tavstaldev.bedWarsQuests.BWQConfiguration;
import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.minecorelib.core.PluginTranslator;
import io.github.tavstaldev.minecorelib.utils.ChatUtils;
import io.github.tavstaldev.minecorelib.utils.GuiUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The AchievementGUI class is responsible for creating, opening, closing, and refreshing
 * the achievement GUI for players. It provides a paginated interface for players to view
 * their achievements and their statuses.
 */
public class AchievementGUI {
    // Logger instance for logging messages related to the AchievementGUI.
    private static final PluginLogger _logger = BedWarsQuests.Logger().withModule(MainGUI.class);

    // Translator instance for localizing messages and GUI elements.
    private static final PluginTranslator _translator = BedWarsQuests.Instance.getTranslator();

    // Number of items displayed per page in the GUI.
    private static final Integer itemsPerPage = 28;

    // Number of rows in the GUI.
    private static final Integer rows = 6;

    /**
     * Creates the achievement GUI for the specified player.
     *
     * @param player The player for whom the GUI is being created.
     * @return The created SGMenu instance representing the achievement GUI.
     */
    public static SGMenu create(@NotNull Player player) {
        try {
            SGMenu menu = BedWarsQuests.GUI().create(_translator.localize(player, "GUI.Achievements.Title"), rows);
            BWQConfiguration config = BedWarsQuests.Config();

            // Create placeholders for empty slots in the GUI.
            SGButton placeholderButton = new SGButton(GuiUtils.createItem(BedWarsQuests.Instance, config.guiPlaceholderItem, " "));
            int slots = rows * 9;
            for (int i = 0; i < slots; i++) {
                menu.setButton(0, i, placeholderButton);
            }

            // Add the title button to the GUI.
            List<Component> titleLore = new ArrayList<>();
            var rawRole = _translator.localizeList(player, "GUI.Achievements.Lore");
            for (String line : rawRole) {
                titleLore.add(ChatUtils.translateColors(line, true));
            }

            SGButton titleButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, config.guiAchievementItem, _translator.localize(player, "GUI.Achievements.Item"), titleLore)
            );
            menu.setButton(0, 4, titleButton);

            // Add the back button to the GUI.
            SGButton backButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, config.guiBackItem, _translator.localize(player, "GUI.Back"))
            ).withListener(event -> {
                close(player);
                MainGUI.open(player);
            });
            menu.setButton(0, 45, backButton);
            return menu;
        } catch (Exception ex) {
            _logger.error("An error occurred while creating the achievement GUI.");
            _logger.error(ex);
            return null;
        }
    }

    /**
     * Opens the achievement GUI for the specified player.
     *
     * @param player The player for whom the GUI is being opened.
     */
    public static void open(@NotNull Player player) {
        var playerCache = PlayerCacheManager.get(player.getUniqueId());
        // Show the GUI.
        playerCache.setGuiOpened(true);
        player.openInventory(playerCache.getAchievementMenu().getInventory());
        refresh(player);
    }

    /**
     * Closes the achievement GUI for the specified player.
     *
     * @param player The player for whom the GUI is being closed.
     */
    public static void close(@NotNull Player player) {
        var playerCache = PlayerCacheManager.get(player.getUniqueId());
        player.closeInventory();
        playerCache.setGuiOpened(false);
    }

    /**
     * Refreshes the achievement GUI for the specified player, updating its contents
     * based on the player's current achievements and page.
     *
     * @param player The player for whom the GUI is being refreshed.
     */
    public static void refresh(@NotNull Player player) {
        try {
            var playerId = player.getUniqueId();
            var playerCache = PlayerCacheManager.get(playerId);
            var menu = playerCache.getAchievementMenu();
            BWQConfiguration config = BedWarsQuests.Config();

            var achievements = BedWarsQuests.AchievementManager().getAchievements();
            int page = playerCache.getAchievementPage();
            boolean hasPrevious = page > 1;
            boolean hasNext = achievements.size() > page * itemsPerPage;

            // Add the previous page button.
            Material prevMaterial = hasPrevious ?
                    config.guiPreviousPageItem
                    :
                    config.guiNoPreviousPageItem;
            String prevName = hasPrevious ? _translator.localize(player, "GUI.PreviousPage") : " ";
            SGButton prevPageButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, prevMaterial, prevName)
            ).withListener(event -> {
                var playerCache_ = PlayerCacheManager.get(playerId);
                if (playerCache_.getAchievementPage() > 1) {
                    playerCache_.setAchievementPage(playerCache_.getAchievementPage() - 1);
                    refresh(player);
                }
            });
            menu.setButton(0, 48, prevPageButton);

            // Add the page indicator button.
            SGButton pageButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, config.guiCurrentPageItem, _translator.localize(player, "GUI.Page", Map.of(
                            "page", String.valueOf(page)))
                    )
            );
            menu.setButton(0, 49, pageButton);

            // Add the next page button.
            Material nextMaterial = hasNext ?
                    config.guiNextPageItem
                    :
                    config.guiNoNextPageItem;
            String nextName = hasNext ? _translator.localize(player, "GUI.NextPage") : " ";
            SGButton nextPageButton = new SGButton(GuiUtils.createItem(BedWarsQuests.Instance, nextMaterial, nextName)
            ).withListener(event -> {
                var playerCache_ = PlayerCacheManager.get(playerId);
                int maxPage = 1 + achievements.size() / itemsPerPage;
                if (playerCache_.getAchievementPage() < maxPage) {
                    playerCache_.setAchievementPage(playerCache_.getAchievementPage() + 1);
                    refresh(player);
                }
            });
            menu.setButton(0, 50, nextPageButton);

            // Populate the GUI with achievements.
            for (int i = 0; i < itemsPerPage; i++) {
                int index = i + (page - 1) * itemsPerPage;
                int slot = i + 10 + (2 * (i / 7));
                if (index >= achievements.size()) {
                    menu.removeButton(0, slot);
                    continue;
                }

                var achievement = achievements.get(index);
                boolean isCompleted = BedWarsQuests.Database().isAchievementCompleted(playerId, achievement.Id);
                Material material = isCompleted ? config.guiCompletedAchievementItem : config.guiLockedAchievementItem;

                String displayName = BedWarsQuests.Translator().localize(player, isCompleted ? "GUI.AchievementData.UnlockedName" : "GUI.AchievementData.LockedName", Map.of("achievement_name", achievement.Name));
                String status = BedWarsQuests.Translator().localize(player, isCompleted ? "GUI.AchievementData.UnlockedStatus" : "GUI.AchievementData.LockedStatus");

                String[] descriptionLines = achievement.Description.split("\n");
                var lore = new ArrayList<Component>();
                var rawRole = _translator.localizeList(player, "GUI.AchievementData.Lore");
                for (String line : rawRole) {
                    if (line.contains("%achievement_description%")) {
                        for (String descLine : descriptionLines) {
                            lore.add(ChatUtils.translateColors(descLine, true));
                        }
                        continue;
                    }

                    if (line.contains("%reward%")) {
                        for (var reward : achievement.Rewards) {
                            lore.add(ChatUtils.translateColors(reward.getLore(player), true));
                        }
                        continue;
                    }

                    lore.add(ChatUtils.translateColors(line.replace("%status%", status), true));
                }

                var item = GuiUtils.createItem(BedWarsQuests.Instance, material, displayName, lore);
                menu.setButton(0, slot, new SGButton(item));
            }

            player.openInventory(menu.getInventory());
        } catch (Exception ex) {
            _logger.error("An error occurred while refreshing the achievement GUI.");
            _logger.error(ex);
        }
    }
}