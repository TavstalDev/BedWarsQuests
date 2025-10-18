package io.github.tavstaldev.bedWarsQuests.models;

import com.samjakob.spigui.menu.SGMenu;
import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.gui.AchievementGUI;
import io.github.tavstaldev.bedWarsQuests.gui.MainGUI;
import io.github.tavstaldev.bedWarsQuests.models.database.CompletedAchievementData;
import io.github.tavstaldev.bedWarsQuests.models.database.DailyObjectiveData;
import io.github.tavstaldev.bedWarsQuests.models.database.WeeklyObjectiveData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * The PlayerCache class represents a cache of player-specific data for the BedWarsQuests plugin.
 * It stores information about the player's achievements, objectives, GUI states, and match statistics.
 */
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

    // The list of completed achievements for the player.
    private List<CompletedAchievementData> _completedAchievements;

    // The list of daily objectives for the player.
    private List<DailyObjectiveData> _dailyObjectives;

    // The list of weekly objectives for the player.
    private List<WeeklyObjectiveData> _weeklyObjectives;

    // The match statistics for the player.
    private PlayerMatchStats _matchStats;

    /**
     * Constructs a PlayerCache instance for the specified player.
     * Initializes the player's achievements, objectives, and match statistics.
     * Generates daily and weekly objectives if they are not already present.
     *
     * @param player The player associated with this cache.
     */
    public PlayerCache(Player player) {
        this._player = player;
        this._isGUIOpened = false;
        this._mainMenu = null;
        this._achievementMenu = null;
        this._achievementPage = 1;
        _completedAchievements = BedWarsQuests.Database().getPlayerCompletedAchievements(player.getUniqueId());
        _dailyObjectives = BedWarsQuests.Database().getPlayerDailyObjectives(player.getUniqueId());
        _weeklyObjectives = BedWarsQuests.Database().getPlayerWeeklyObjectives(player.getUniqueId());
        _matchStats = new PlayerMatchStats();

        if (getDailyObjectives().isEmpty()) {
            generateDailyObjectives();
        }

        if (getWeeklyObjectives().isEmpty()) {
            generateWeeklyObjectives();
        }
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

    //#region Database

    /**
     * Retrieves the list of completed achievements for the player.
     *
     * @return A list of CompletedAchievementData objects.
     */
    public List<CompletedAchievementData> getAchievements() {
        return _completedAchievements;
    }

    /**
     * Retrieves the list of daily objectives for the player.
     *
     * @return A list of DailyObjectiveData objects.
     */
    public List<DailyObjectiveData> getDailyObjectives() {
        return _dailyObjectives;
    }

    /**
     * Retrieves the list of weekly objectives for the player.
     *
     * @return A list of WeeklyObjectiveData objects.
     */
    public List<WeeklyObjectiveData> getWeeklyObjectives() {
        return _weeklyObjectives;
    }

    /**
     * Checks if the specified objective ID belongs to a weekly objective.
     *
     * @param objectiveId The ID of the objective to check.
     * @return True if it is a weekly objective, false if it is a daily objective, or null if not found.
     */
    public @Nullable Boolean isWeeklyObjective(String objectiveId) {
        for (WeeklyObjectiveData obj : _weeklyObjectives) {
            if (obj.ObjectiveId.equals(objectiveId)) {
                return true;
            }
        }

        for (DailyObjectiveData obj : _dailyObjectives) {
            if (obj.ObjectiveId.equals(objectiveId)) {
                return false;
            }
        }

        return null;
    }

    /**
     * Checks if the specified achievement ID is completed.
     *
     * @param achievementId The ID of the achievement to check.
     * @return True if the achievement is completed, false otherwise.
     */
    public boolean isAchievementCompleted(String achievementId) {
        for (CompletedAchievementData ach : _completedAchievements) {
            if (ach.AchievementId.equals(achievementId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the specified daily objective ID is completed.
     *
     * @param objectiveId The ID of the daily objective to check.
     * @return True if the daily objective is completed, false otherwise.
     */
    public boolean isDailyObjectiveCompleted(String objectiveId) {
        for (DailyObjectiveData obj : _dailyObjectives) {
            if (obj.ObjectiveId.equals(objectiveId) && obj.IsCompleted) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the specified weekly objective ID is completed.
     *
     * @param objectiveId The ID of the weekly objective to check.
     * @return True if the weekly objective is completed, false otherwise.
     */
    public boolean isWeeklyObjectiveCompleted(String objectiveId) {
        for (WeeklyObjectiveData obj : _weeklyObjectives) {
            if (obj.ObjectiveId.equals(objectiveId) && obj.IsCompleted) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the list of completed achievements for the player.
     *
     * @param achievements The list of CompletedAchievementData objects to set.
     */
    public void setAchievements(List<CompletedAchievementData> achievements) {
        this._completedAchievements = achievements;
    }

    /**
     * Adds a completed achievement to the player's cache.
     *
     * @param achievement The CompletedAchievementData object to add.
     */
    public void addAchievement(CompletedAchievementData achievement) {
        this._completedAchievements.add(achievement);
    }

    /**
     * Sets the list of daily objectives for the player.
     *
     * @param dailyObjectives The list of DailyObjectiveData objects to set.
     */
    public void setDailyObjectives(List<DailyObjectiveData> dailyObjectives) {
        this._dailyObjectives = dailyObjectives;
    }

    /**
     * Adds a daily objective to the player's cache.
     *
     * @param dailyObjective The DailyObjectiveData object to add.
     */
    public void addDailyObjective(DailyObjectiveData dailyObjective) {
        this._dailyObjectives.add(dailyObjective);
    }

    /**
     * Marks the specified daily objective as completed.
     *
     * @param objectiveId The ID of the daily objective to mark as completed.
     */
    public void completeDailyObjective(String objectiveId) {
        for (DailyObjectiveData obj : _dailyObjectives) {
            if (obj.ObjectiveId.equals(objectiveId)) {
                obj.IsCompleted = true;
                return;
            }
        }
    }

    /**
     * Generates new daily objectives for the player.
     * Removes existing daily objectives and selects new ones from the available pool.
     */
    public void generateDailyObjectives() {
        // Wipe existing daily objectives from cache
        _dailyObjectives.clear();

        var objectives = BedWarsQuests.ObjectiveManager().getObjectives();
        if (!getWeeklyObjectives().isEmpty()) {
            for (var weeklyObj : getWeeklyObjectives()) {
                objectives.removeIf(obj -> obj.Id.equals(weeklyObj.ObjectiveId));
            }
        }

        Collections.shuffle(objectives);
        int numToTake = Math.min(3, objectives.size());
        for (var item : objectives.subList(0, numToTake)) {
            BedWarsQuests.Database().addPlayerDailyObjective(_player.getUniqueId(), item.Id);
            addDailyObjective(new DailyObjectiveData(_player.getUniqueId(), item.Id, false));
        }
    }

    /**
     * Sets the list of weekly objectives for the player.
     *
     * @param weeklyObjectives The list of WeeklyObjectiveData objects to set.
     */
    public void setWeeklyObjectives(List<WeeklyObjectiveData> weeklyObjectives) {
        this._weeklyObjectives = weeklyObjectives;
    }

    /**
     * Adds a weekly objective to the player's cache.
     *
     * @param weeklyObjective The WeeklyObjectiveData object to add.
     */
    public void addWeeklyObjective(WeeklyObjectiveData weeklyObjective) {
        this._weeklyObjectives.add(weeklyObjective);
    }

    /**
     * Marks the specified weekly objective as completed.
     *
     * @param objectiveId The ID of the weekly objective to mark as completed.
     */
    public void completeWeeklyObjective(String objectiveId) {
        for (WeeklyObjectiveData obj : _weeklyObjectives) {
            if (obj.ObjectiveId.equals(objectiveId)) {
                obj.IsCompleted = true;
                return;
            }
        }
    }

    /**
     * Generates new weekly objectives for the player.
     * Removes existing weekly objectives and selects new ones from the available pool.
     */
    public void generateWeeklyObjectives() {
        // Wipe existing weekly objectives from cache
        _weeklyObjectives.clear();

        var objectives = BedWarsQuests.ObjectiveManager().getObjectives();
        if (!getDailyObjectives().isEmpty()) {
            for (var dailyObj : getDailyObjectives()) {
                objectives.removeIf(obj -> obj.Id.equals(dailyObj.ObjectiveId));
            }
        }
        Collections.shuffle(objectives);
        int numToTake = Math.min(3, objectives.size());
        for (var item : objectives.subList(0, numToTake)) {
            BedWarsQuests.Database().addPlayerWeeklyObjective(_player.getUniqueId(), item.Id);
            addWeeklyObjective(new WeeklyObjectiveData(_player.getUniqueId(), item.Id, false));
        }
    }
    //#endregion

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
