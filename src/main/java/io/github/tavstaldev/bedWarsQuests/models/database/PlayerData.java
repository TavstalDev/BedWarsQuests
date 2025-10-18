package io.github.tavstaldev.bedWarsQuests.models.database;

import java.util.UUID;

/**
 * The PlayerData class represents the data model for a player in the BedWarsQuests system.
 * It stores the player's unique identifier, achievement points, and the number of completed objectives.
 */
public class PlayerData {
    // The unique identifier of the player.
    public UUID Id;

    // The total achievement points earned by the player.
    public long AchievementPoints;

    // The number of daily objectives completed by the player.
    public int CompletedDailyObjectives;

    // The number of weekly objectives completed by the player.
    public int CompletedWeeklyObjectives;

    /**
     * Constructs a PlayerData instance with the specified player details.
     *
     * @param id                       The unique identifier of the player.
     * @param achievementPoints        The total achievement points earned by the player.
     * @param completedDailyObjectives The number of daily objectives completed by the player.
     * @param completedWeeklyObjectives The number of weekly objectives completed by the player.
     */
    public PlayerData(UUID id, long achievementPoints, int completedDailyObjectives, int completedWeeklyObjectives) {
        Id = id;
        AchievementPoints = achievementPoints;
        CompletedWeeklyObjectives = completedWeeklyObjectives;
        CompletedDailyObjectives = completedDailyObjectives;
    }
}
