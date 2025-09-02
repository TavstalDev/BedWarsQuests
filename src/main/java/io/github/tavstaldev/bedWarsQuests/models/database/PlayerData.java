package io.github.tavstaldev.bedWarsQuests.models.database;

import java.util.UUID;

public class PlayerData {
    public UUID Id;
    public long AchievementPoints;
    public int CompletedDailyObjectives;
    public int CompletedWeeklyObjectives;

    public PlayerData(UUID id, long achievementPoints, int completedDailyObjectives, int completedWeeklyObjectives) {
        Id = id;
        AchievementPoints = achievementPoints;
        CompletedWeeklyObjectives = completedWeeklyObjectives;
        CompletedDailyObjectives = completedDailyObjectives;
    }
}
