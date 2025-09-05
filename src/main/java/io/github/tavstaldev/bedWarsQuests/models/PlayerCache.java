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

public class PlayerCache {
    private final Player _player;
    private boolean _isGUIOpened;
    private SGMenu _mainMenu;
    private SGMenu _achievementMenu;
    private int _achievementPage;
    private List<CompletedAchievementData> _completedAchievements;
    private List<DailyObjectiveData> _dailyObjectives;
    private List<WeeklyObjectiveData> _weeklyObjectives;
    private PlayerMatchStats _matchStats;

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

    public boolean isGuiOpened() {
        return _isGUIOpened;
    }

    public void setGuiOpened(boolean isGUIOpened) {
        this._isGUIOpened = isGUIOpened;
    }

    public PlayerMatchStats getMatchStats() {
        return _matchStats;
    }

    //#region Database
    public List<CompletedAchievementData> getAchievements() {
        return _completedAchievements;
    }

    public List<DailyObjectiveData> getDailyObjectives() {
        return _dailyObjectives;
    }

    public List<WeeklyObjectiveData> getWeeklyObjectives() {
        return _weeklyObjectives;
    }

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

    public boolean isAchievementCompleted(String achievementId) {
        for (CompletedAchievementData ach : _completedAchievements) {
            if (ach.AchievementId.equals(achievementId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDailyObjectiveCompleted(String objectiveId) {
        for (DailyObjectiveData obj : _dailyObjectives) {
            if (obj.ObjectiveId.equals(objectiveId) && obj.IsCompleted) {
                return true;
            }
        }
        return false;
    }

    public boolean isWeeklyObjectiveCompleted(String objectiveId) {
        for (WeeklyObjectiveData obj : _weeklyObjectives) {
            if (obj.ObjectiveId.equals(objectiveId) && obj.IsCompleted) {
                return true;
            }
        }
        return false;
    }

    public void setAchievements(List<CompletedAchievementData> achievements) {
        this._completedAchievements = achievements;
    }

    public void addAchievement(CompletedAchievementData achievement) {
        this._completedAchievements.add(achievement);
    }

    public void setDailyObjectives(List<DailyObjectiveData> dailyObjectives) {
        this._dailyObjectives = dailyObjectives;
    }

    public void addDailyObjective(DailyObjectiveData dailyObjective) {
        this._dailyObjectives.add(dailyObjective);
    }

    public void completeDailyObjective(String objectiveId) {
        for (DailyObjectiveData obj : _dailyObjectives) {
            if (obj.ObjectiveId.equals(objectiveId)) {
                obj.IsCompleted = true;
                return;
            }
        }
    }

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

    public void setWeeklyObjectives(List<WeeklyObjectiveData> weeklyObjectives) {
        this._weeklyObjectives = weeklyObjectives;
    }

    public void addWeeklyObjective(WeeklyObjectiveData weeklyObjective) {
        this._weeklyObjectives.add(weeklyObjective);
    }

    public void completeWeeklyObjective(String objectiveId) {
        for (WeeklyObjectiveData obj : _weeklyObjectives) {
            if (obj.ObjectiveId.equals(objectiveId)) {
                obj.IsCompleted = true;
                return;
            }
        }
    }

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
    public SGMenu getMainMenu() {
        if (_mainMenu == null) {
            _mainMenu = MainGUI.create(_player);
        }
        return _mainMenu;
    }
    //#endregion

    //#region Achievement Menu
    public SGMenu getAchievementMenu() {
        if (_achievementMenu == null) {
            _achievementMenu = AchievementGUI.create(_player);
        }
        return _achievementMenu;
    }

    public int getAchievementPage() {
        return _achievementPage;
    }

    public void setAchievementPage(int achievementPage) {
        this._achievementPage = achievementPage;
    }
    //#endregion
}
