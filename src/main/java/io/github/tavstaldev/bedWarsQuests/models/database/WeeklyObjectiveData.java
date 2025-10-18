package io.github.tavstaldev.bedWarsQuests.models.database;

import java.util.UUID;

/**
 * The WeeklyObjectiveData class represents the data model for a weekly objective.
 * It stores the player's unique identifier, the ID of the objective, and its completion status.
 */
public class WeeklyObjectiveData {
    // The unique identifier of the player associated with the weekly objective.
    public UUID PlayerId;

    // The unique identifier of the weekly objective.
    public String ObjectiveId;

    // Indicates whether the weekly objective has been completed.
    public boolean IsCompleted;

    /**
     * Constructs a WeeklyObjectiveData instance with the specified player ID, objective ID, and completion status.
     *
     * @param playerId      The unique identifier of the player.
     * @param objectiveId   The unique identifier of the weekly objective.
     * @param isCompleted   The completion status of the weekly objective.
     */
    public WeeklyObjectiveData(UUID playerId, String objectiveId, boolean isCompleted) {
        PlayerId = playerId;
        ObjectiveId = objectiveId;
        IsCompleted = isCompleted;
    }
}
