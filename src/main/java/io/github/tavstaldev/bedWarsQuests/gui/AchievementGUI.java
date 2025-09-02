package io.github.tavstaldev.bedWarsQuests.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.menu.SGMenu;
import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.utils.IconUtils;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.minecorelib.core.PluginTranslator;
import io.github.tavstaldev.minecorelib.utils.GuiUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AchievementGUI {
    private static final PluginLogger _logger = BedWarsQuests.Logger().WithModule(MainGUI.class);
    private static final PluginTranslator _translator = BedWarsQuests.Instance.getTranslator();
    private static final  Integer Rows = 3;

    public static SGMenu create(@NotNull Player player) {
        try {
            SGMenu menu = BedWarsQuests.GUI().create(_translator.Localize(player, "GUI.Achievements.Title"), Rows);
            var playerId = player.getUniqueId();

            // Create Placeholders
            Material placeholderMaterial = IconUtils.getMaterialFromConfig("gui.placeholderItem");
            SGButton placeholderButton = new SGButton(GuiUtils.createItem(BedWarsQuests.Instance, placeholderMaterial, " "));
            int slots = Rows * 9;
            for (int i = 0; i < slots; i++) {
                menu.setButton(0, i, placeholderButton);
            }

            // Back Button
            Material backMaterial = IconUtils.getMaterialFromConfig("gui.backItem");
            SGButton backButton = new SGButton(
                    GuiUtils.createItem(BedWarsQuests.Instance, backMaterial, _translator.Localize(player, "GUI.Back"))
            ).withListener(event -> {
                close(player);
                MainGUI.open(player);
            });
            menu.setButton(0, 18, backButton);
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

            // TODO

            player.openInventory(menu.getInventory());
        }
        catch (Exception ex) {
            _logger.Error("An error occurred while refreshing the achievement GUI.");
            _logger.Error(ex);
        }
    }
}