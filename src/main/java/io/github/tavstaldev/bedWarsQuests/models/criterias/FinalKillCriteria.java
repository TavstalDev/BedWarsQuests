package io.github.tavstaldev.bedWarsQuests.models.criterias;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.AchievementCriteria;

/**
 * The FinalKillCriteria class represents a specific achievement criterion
 * that checks if a player has achieved a certain number of final kills
 * in a match or across all matches, depending on the configuration.
 */
public class FinalKillCriteria extends AchievementCriteria {
    // The number of final kills required to satisfy the criterion.
    private final int count;

    /**
     * Constructs a FinalKillCriteria instance with the specified parameters.
     *
     * @param operator      The comparison operator to use (e.g., EQUALS, GREATER_THAN).
     * @param isSingleMatch Whether the criterion applies to a single match or all matches.
     * @param count         The number of final kills required to satisfy the criterion.
     */
    public FinalKillCriteria(String operator, boolean isSingleMatch, int count) {
        super("final_kill", operator, isSingleMatch);
        this.count = count;
    }

    /**
     * Checks if the criterion is satisfied for the given player and event.
     *
     * @param player        The player whose progress is being checked.
     * @param event         The event that triggered the check.
     * @param isAchievement Whether the check is for an achievement or a daily objective.
     * @return True if the criterion is satisfied, false otherwise.
     */
    @Override
    public boolean isSatisfied(org.bukkit.entity.Player player, org.bukkit.event.Event event, boolean isAchievement) {
        if (!this.singleMatch) {
            BedWarsQuests.Logger().warn("FinalKillCriteria is only supported for single match achievements/objectives.");
        }

        // Retrieve the number of final kills made by the player.
        int value = PlayerCacheManager.get(player.getUniqueId()).getMatchStats().FinalKills;

        // Compare the retrieved value with the required count using the specified operator.
        return switch (getOperator()) {
            case EQUALS -> value == count;
            case NOT_EQUALS -> value != count;
            case GREATER_THAN -> value > count;
            case LESS_THAN -> value < count;
            case GREATER_THAN_OR_EQUAL -> value >= count;
            case LESS_THAN_OR_EQUAL -> value <= count;
        };
    }
}
