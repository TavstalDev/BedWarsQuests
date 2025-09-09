package io.github.tavstaldev.bedWarsQuests.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.menu.SGMenu;
import io.github.tavstaldev.bedWarsQuests.BWQConfiguration;
import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.utils.IconUtils;
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

public class AchievementGUI {
    private static final PluginLogger _logger = BedWarsQuests.Logger().WithModule(MainGUI.class);
    private static final PluginTranslator _translator = BedWarsQuests.Instance.getTranslator();
    private static final Integer ItemsPerPage = 28;
    private static final  Integer Rows = 6;

    public static SGMenu create(@NotNull Player player) {
        try {
            SGMenu menu = BedWarsQuests.GUI().create(_translator.Localize(player, "GUI.Achievements.Title"), Rows);
            BWQConfiguration config = BedWarsQuests.Config();

            // Create Placeholders
            SGButton placeholderButton = new SGButton(GuiUtils.createItem(BedWarsQuests.Instance, config.guiPlaceholderItem, " "));
            int slots = Rows * 9;
            for (int i = 0; i < slots; i++) {
                menu.setButton(0, i, placeholderButton);
            }

            // Title Button
            List<Component> titleLore = new ArrayList<>();
            var rawRole = _translator.LocalizeList(player, "GUI.Achievements.Lore");
            for (String line : rawRole) {
                titleLore.add(ChatUtils.translateColors(line, true));
            }

            SGButton titleButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, config.guiAchievementItem, _translator.Localize(player, "GUI.Achievements.Item"), titleLore)
            );
            menu.setButton(0, 4, titleButton);

            // Back Button
            SGButton backButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, config.guiBackItem, _translator.Localize(player, "GUI.Back"))
            ).withListener(event -> {
                close(player);
                MainGUI.open(player);
            });
            menu.setButton(0, 45, backButton);
            return menu;
        }
        catch (Exception ex) {
            _logger.Error("An error occurred while creating the achievement GUI.");
            _logger.Error(ex);
            return null;
        }
    }

    public static void open(@NotNull Player player) {
        var playerCache = PlayerCacheManager.get(player.getUniqueId());
        // Show the GUI
        playerCache.setGuiOpened(true);
        player.openInventory(playerCache.getAchievementMenu().getInventory());
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
            var menu = playerCache.getAchievementMenu();
            BWQConfiguration config = BedWarsQuests.Config();

            var achievements = BedWarsQuests.AchievementManager().getAchievements();
            int page = playerCache.getAchievementPage();
            boolean hasPrevious = page > 1;
            boolean hasNext = achievements.size() > page * ItemsPerPage;

            //#region Previous Page Button
            Material prevMaterial = hasPrevious ?
                    config.guiPreviousPageItem
                    :
                    config.guiNoPreviousPageItem;
            String prevName = hasPrevious ? _translator.Localize(player, "GUI.PreviousPage") : " ";
            SGButton prevPageButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, prevMaterial, prevName )
            ).withListener(event -> {
                var playerCache_ = PlayerCacheManager.get(playerId);
                if (playerCache_.getAchievementPage() > 1) {
                    playerCache_.setAchievementPage(playerCache_.getAchievementPage() - 1);
                    refresh(player);
                }
            });
            menu.setButton(0, 48, prevPageButton);
            //#endregion

            //#region Page Indicator
            SGButton pageButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, config.guiCurrentPageItem, _translator.Localize(player, "GUI.Page", Map.of(
                            "page", String.valueOf(page)))
                    )
            );
            menu.setButton(0, 49, pageButton);
            //#endregion

            //#region Next Page Button
            Material nextMaterial = hasNext ?
                    config.guiNextPageItem
                    :
                    config.guiNoNextPageItem;
            String nextName = hasNext ? _translator.Localize(player, "GUI.NextPage") : " ";
            SGButton nextPageButton = new SGButton(GuiUtils.createItem(BedWarsQuests.Instance, nextMaterial,nextName)
            ).withListener(event -> {
                var playerCache_ = PlayerCacheManager.get(playerId);
                int maxPage = 1 + achievements.size() / ItemsPerPage;
                if (playerCache_.getAchievementPage() < maxPage) {
                    playerCache_.setAchievementPage(playerCache_.getAchievementPage() + 1);
                    refresh(player);
                }
            });
            menu.setButton(0, 50, nextPageButton);
            //#endregion

            for (int i = 0; i < ItemsPerPage; i++) {
                int index = i + (page - 1) * ItemsPerPage;
                int slot = i + 10 + (2 * (i / 7));
                if (index >= achievements.size()) {
                    menu.removeButton(0, slot);
                    continue;
                }

                var achievement = achievements.get(index);
                boolean isCompleted = playerCache.isAchievementCompleted(achievement.Id);
                Material material = isCompleted ? config.guiCompletedAchievementItem : config.guiLockedAchievementItem;

                String displayName = BedWarsQuests.Translator().Localize(player, isCompleted ? "GUI.AchievementData.UnlockedName" : "GUI.AchievementData.LockedName", Map.of("achievement_name", achievement.Name));
                String status = BedWarsQuests.Translator().Localize(player, isCompleted ? "GUI.AchievementData.UnlockedStatus" : "GUI.AchievementData.LockedStatus");

                String[] descriptionLines = achievement.Description.split("\n");
                var lore = new ArrayList<Component>();
                var rawRole = _translator.LocalizeList(player, "GUI.AchievementData.Lore");
                for (String line : rawRole) {
                    if (line.contains("%achievement_description%")) {
                        for (String descLine : descriptionLines) {
                            lore.add(ChatUtils.translateColors(descLine, true));
                        }
                        continue;
                    }

                    if (line.contains("%reward%"))
                    {
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
        }
        catch (Exception ex) {
            _logger.Error("An error occurred while refreshing the achievement GUI.");
            _logger.Error(ex);
        }
    }
}