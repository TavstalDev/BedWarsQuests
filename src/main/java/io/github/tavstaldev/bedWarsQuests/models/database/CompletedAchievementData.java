package io.github.tavstaldev.bedWarsQuests.models.database;

import java.util.UUID;

public class CompletedAchievementData {
    public UUID PlayerId;
    public String AchievementId;

    public CompletedAchievementData(UUID playerId, String achievementId) {
        PlayerId = playerId;
        AchievementId = achievementId;
    }
}
