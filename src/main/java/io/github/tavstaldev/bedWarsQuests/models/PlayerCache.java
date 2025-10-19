package io.github.tavstaldev.bedWarsQuests.models;

import com.samjakob.spigui.menu.SGMenu;
import io.github.tavstaldev.bedWarsQuests.gui.AchievementGUI;
import io.github.tavstaldev.bedWarsQuests.gui.MainGUI;
import org.bukkit.entity.Player;

public class PlayerCache {
    // The player associated with this cache.
    private final Player _player;

    // Indicates whether the GUI is currently opened for the player.
    private boolean _isGUIOpened;

    // The main menu GUI for the player.
    private SGMenu _mainMenu;

    // The achievement menu GUI for the player.
    private SGMenu _achievementMenu;

    // The current page of the achievement menu.
    private int _achievementPage;

    // The match statistics for the player.
    private PlayerMatchStats _matchStats;

    public PlayerCache(Player player) {
        this._player = player;
        this._isGUIOpened = false;
        this._mainMenu = null;
        this._achievementMenu = null;
        this._achievementPage = 1;
        _matchStats = new PlayerMatchStats();
    }

    /**
     * Checks if the GUI is currently opened for the player.
     *
     * @return True if the GUI is opened, false otherwise.
     */
    public boolean isGuiOpened() {
        return _isGUIOpened;
    }

    /**
     * Sets the GUI opened state for the player.
     *
     * @param isGUIOpened True to mark the GUI as opened, false otherwise.
     */
    public void setGuiOpened(boolean isGUIOpened) {
        this._isGUIOpened = isGUIOpened;
    }

    /**
     * Retrieves the match statistics for the player.
     *
     * @return The PlayerMatchStats object representing the player's match statistics.
     */
    public PlayerMatchStats getMatchStats() {
        return _matchStats;
    }

    //#region Main Menu

    /**
     * Retrieves the main menu GUI for the player.
     * Creates the menu if it does not already exist.
     *
     * @return The SGMenu object representing the main menu.
     */
    public SGMenu getMainMenu() {
        if (_mainMenu == null) {
            _mainMenu = MainGUI.create(_player);
        }
        return _mainMenu;
    }
    //#endregion

    //#region Achievement Menu

    /**
     * Retrieves the achievement menu GUI for the player.
     * Creates the menu if it does not already exist.
     *
     * @return The SGMenu object representing the achievement menu.
     */
    public SGMenu getAchievementMenu() {
        if (_achievementMenu == null) {
            _achievementMenu = AchievementGUI.create(_player);
        }
        return _achievementMenu;
    }

    /**
     * Retrieves the current page of the achievement menu.
     *
     * @return The current page number.
     */
    public int getAchievementPage() {
        return _achievementPage;
    }

    /**
     * Sets the current page of the achievement menu.
     *
     * @param achievementPage The page number to set.
     */
    public void setAchievementPage(int achievementPage) {
        this._achievementPage = achievementPage;
    }
    //#endregion
}
