package io.github.tavstaldev.bedWarsQuests.database;

import io.github.tavstaldev.bedWarsQuests.models.database.ObjectiveData;
import io.github.tavstaldev.bedWarsQuests.models.database.PlayerData;
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

    @Nullable Boolean isWeeklyObjective(UUID playerId, String objectiveId);

    //#region Daily Objectives
    void addPlayerDailyObjective(UUID playerId, String objectiveId);

    void updatePlayerDailyObjective(UUID playerId, String objectiveId, boolean isCompleted);

    void wipePlayerDailyObjectives();

    List<ObjectiveData> getPlayerDailyObjectives(UUID playerId);

    boolean isDailyObjectiveCompleted(UUID playerId, String objectiveId);

    boolean isDailyObjectiveExists(UUID playerId, String objectiveId);

    void generateDailyObjectives(UUID playerId);
    //#endregion

    //#region Weekly Objectives
    void addPlayerWeeklyObjective(UUID playerId, String objectiveId);

    void updatePlayerWeeklyObjective(UUID playerId, String objectiveId, boolean isCompleted);

    void wipePlayerWeeklyObjectives();

    List<ObjectiveData> getPlayerWeeklyObjectives(UUID playerId);

    boolean isWeeklyObjectiveCompleted(UUID playerId, String objectiveId);

    boolean isWeeklyObjectiveExists(UUID playerId, String objectiveId);

    void generateWeeklyObjectives(UUID playerId);
    //#endregion

    //#region Completed Achievements
    void addCompletedAchievement(UUID playerId, String achievementId);

    void wipeCompletedAchievements();

    List<String> getPlayerCompletedAchievements(UUID playerId);

    boolean isAchievementCompleted(UUID playerId, String achievementId);
    //#endregion
}