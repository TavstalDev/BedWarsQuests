package io.github.tavstaldev.bedWarsQuests.database;

import io.github.tavstaldev.bedWarsQuests.models.database.CompletedAchievementData;
import io.github.tavstaldev.bedWarsQuests.models.database.DailyObjectiveData;
import io.github.tavstaldev.bedWarsQuests.models.database.PlayerData;
import io.github.tavstaldev.bedWarsQuests.models.database.WeeklyObjectiveData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface IDatabase {

    void load();

    void unload();

    void checkSchema();

    //#region PlayerData
    void addPlayerData(UUID playerId);

    void updatePlayerData(UUID playerId, long achievementPoints, int completedDailyObjectives, int completedWeeklyObjectives);

    void increaseAchievementPoints(UUID playerId, long points);

    void increaseCompletedDailyObjectives(UUID playerId);

    void increaseCompletedWeeklyObjectives(UUID playerId);

    void wipePlayerData();

    @Nullable PlayerData getPlayerData(UUID playerId);
    //#endregion

    //#region Daily Objectives
    void addPlayerDailyObjective(UUID playerId, String objectiveId);

    void updatePlayerDailyObjective(UUID playerId, String objectiveId, boolean isCompleted);

    boolean hasPlayerCompletedDailyObjective(UUID playerId, String objectiveId);

    void wipePlayerDailyObjectives();

    List<DailyObjectiveData> getPlayerDailyObjectives(UUID playerId);
    //#endregion

    //#region Weekly Objectives
    void addPlayerWeeklyObjective(UUID playerId, String objectiveId);

    void updatePlayerWeeklyObjective(UUID playerId, String objectiveId, boolean isCompleted);

    boolean hasPlayerCompletedWeeklyObjective(UUID playerId, String objectiveId);

    void wipePlayerWeeklyObjectives();

    List<WeeklyObjectiveData> getPlayerWeeklyObjectives(UUID playerId);
    //#endregion

    //#region Completed Achievements
    void addCompletedAchievement(UUID playerId, String achievementId);

    boolean hasPlayerCompletedAchievement(UUID playerId, String achievementId);

    void wipeCompletedAchievements();

    List<CompletedAchievementData> getPlayerCompletedAchievements(UUID playerId);
    //#endregion
}