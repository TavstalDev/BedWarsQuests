package io.github.tavstaldev.bedWarsQuests.models;

import com.samjakob.spigui.menu.SGMenu;
import org.bukkit.entity.Player;

public class PlayerCache {
    private final Player _player;
    private boolean _isGUIOpened;
    private SGMenu _mainMenu;
    private int _mainPage;

    public PlayerCache(Player _player) {
        this._player = _player;
        this._isGUIOpened = false;
        this._mainMenu = null;
        this._mainPage = 0;
    }

    public boolean isGuiOpened() {
        return _isGUIOpened;
    }

    public void setGuiOpened(boolean isGUIOpened) {
        this._isGUIOpened = isGUIOpened;
    }

    //#region Main Menu
    public SGMenu getMainMenu() {
        if (_mainMenu == null) {
            _mainMenu = MainGUI.create(_player);
        }
        return _mainMenu;
    }

    public int getMainPage() {
        return _mainPage;
    }

    public void setMainPage(int page) {
        this._mainPage = page;
    }
    //#endregion
}
