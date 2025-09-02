package io.github.tavstaldev.bedWarsQuests.models.database;

import java.util.UUID;

public class DailyObjectiveData {
    public UUID PlayerId;
    public String ObjectiveId;
    public boolean IsCompleted;

    public DailyObjectiveData(UUID playerId, String objectiveId, boolean isCompleted) {
        PlayerId = playerId;
        ObjectiveId = objectiveId;
        IsCompleted = isCompleted;
    }
}
