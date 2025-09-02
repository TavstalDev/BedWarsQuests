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

    public PlayerCache(Player player) {
        this._player = player;
        this._isGUIOpened = false;
        this._mainMenu = null;
        this._achievementMenu = null;
        this._achievementPage = 1;
        _completedAchievements = BedWarsQuests.Database().GetPlayerCompletedAchievements(player.getUniqueId().toString());
        _dailyObjectives = BedWarsQuests.Database().GetPlayerDailyObjectives(player.getUniqueId().toString());
        _weeklyObjectives = BedWarsQuests.Database().GetPlayerWeeklyObjectives(player.getUniqueId().toString());
    }

    public boolean isGuiOpened() {
        return _isGUIOpened;
    }

    public void setGuiOpened(boolean isGUIOpened) {
        this._isGUIOpened = isGUIOpened;
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

    public @Nullable Boolean IsWeeklyObjective(String objectiveId) {
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

    public boolean IsAchievementCompleted(String achievementId) {
        for (CompletedAchievementData ach : _completedAchievements) {
            if (ach.AchievementId.equals(achievementId)) {
                return true;
            }
        }
        return false;
    }

    public boolean IsDailyObjectiveCompleted(String objectiveId) {
        for (DailyObjectiveData obj : _dailyObjectives) {
            if (obj.ObjectiveId.equals(objectiveId) && obj.IsCompleted) {
                return true;
            }
        }
        return false;
    }

    public boolean IsWeeklyObjectiveCompleted(String objectiveId) {
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
