package io.github.tavstaldev.bedWarsQuests.database;

import io.github.tavstaldev.bedWarsQuests.models.database.CompletedAchievementData;
import io.github.tavstaldev.bedWarsQuests.models.database.DailyObjectiveData;
import io.github.tavstaldev.bedWarsQuests.models.database.PlayerData;
import io.github.tavstaldev.bedWarsQuests.models.database.WeeklyObjectiveData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IDatabase {

    void Load();

    void Unload();

    void CheckSchema();

    //#region PlayerData
    void AddPlayerData(String playerUUID);

    void UpdatePlayerData(String playerUUID, long achievementPoints, int completedDailyObjectives, int completedWeeklyObjectives);

    void UpdatePlayerData(String playerUUID, int completedDailyObjectives, int completedWeeklyObjectives);

    void UpdatePlayerData(String playerUUID, long achievementPoints);

    @Nullable PlayerData GetPlayerData(String playerUUID);
    //#endregion

    //#region Daily Objectives
    void AddPlayerDailyObjective(String playerUUID, String objectiveId);

    void UpdatePlayerDailyObjective(String playerUUID, String objectiveId, boolean isCompleted);

    boolean HasPlayerCompletedDailyObjective(String playerUUID, String objectiveId);

    void WipePlayerDailyObjectives(String playerUUID);

    List<DailyObjectiveData> GetPlayerDailyObjectives(String playerUUID);
    //#endregion

    //#region Weekly Objectives
    void AddPlayerWeeklyObjective(String playerUUID, String objectiveId);

    void UpdatePlayerWeeklyObjective(String playerUUID, String objectiveId, boolean isCompleted);

    boolean HasPlayerCompletedWeeklyObjective(String playerUUID, String objectiveId);

    void WipePlayerWeeklyObjectives(String playerUUID);

    List<WeeklyObjectiveData> GetPlayerWeeklyObjectives(String playerUUID);
    //#endregion

    //#region Completed Achievements
    void AddCompletedAchievement(String playerUUID, String achievementId);

    boolean HasPlayerCompletedAchievement(String playerUUID, String achievementId);

    List<CompletedAchievementData> GetPlayerCompletedAchievements(String playerUUID);
    //#endregion
}