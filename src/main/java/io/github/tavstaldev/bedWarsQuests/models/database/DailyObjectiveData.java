package io.github.tavstaldev.bedWarsQuests.models.database;

import java.util.UUID;

/**
 * The DailyObjectiveData class represents the data model for a daily objective.
 * It stores the player's unique identifier, the ID of the objective, and its completion status.
 */
public class DailyObjectiveData {
    // The unique identifier of the player associated with the daily objective.
    public UUID PlayerId;

    // The unique identifier of the daily objective.
    public String ObjectiveId;

    // Indicates whether the daily objective has been completed.
    public boolean IsCompleted;

    /**
     * Constructs a DailyObjectiveData instance with the specified player ID, objective ID, and completion status.
     *
     * @param playerId      The unique identifier of the player.
     * @param objectiveId   The unique identifier of the daily objective.
     * @param isCompleted   The completion status of the daily objective.
     */
    public DailyObjectiveData(UUID playerId, String objectiveId, boolean isCompleted) {
        PlayerId = playerId;
        ObjectiveId = objectiveId;
        IsCompleted = isCompleted;
    }
}
