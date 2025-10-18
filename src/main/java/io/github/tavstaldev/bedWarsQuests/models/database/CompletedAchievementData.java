package io.github.tavstaldev.bedWarsQuests.models.database;

import java.util.UUID;

/**
 * The CompletedAchievementData class represents the data model for a completed achievement.
 * It stores the player's unique identifier and the ID of the completed achievement.
 */
public class CompletedAchievementData {
    // The unique identifier of the player who completed the achievement.
    public UUID PlayerId;

    // The unique identifier of the completed achievement.
    public String AchievementId;

    /**
     * Constructs a CompletedAchievementData instance with the specified player ID and achievement ID.
     *
     * @param playerId      The unique identifier of the player.
     * @param achievementId The unique identifier of the completed achievement.
     */
    public CompletedAchievementData(UUID playerId, String achievementId) {
        PlayerId = playerId;
        AchievementId = achievementId;
    }
}
