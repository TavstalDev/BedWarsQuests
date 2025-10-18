package io.github.tavstaldev.bedWarsQuests.models.criterias;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.AchievementCriteria;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;

/**
 * The WinCriteria class represents a specific achievement criterion
 * that checks if a player has won a certain number of games.
 */
public class WinCriteria extends AchievementCriteria {
    // The number of wins required to satisfy the criterion.
    private final int count;

    /**
     * Constructs a WinCriteria instance with the specified parameters.
     *
     * @param operator      The comparison operator to use (e.g., EQUALS, GREATER_THAN).
     * @param isSingleMatch Whether the criterion applies to a single match.
     * @param count         The number of wins required to satisfy the criterion.
     */
    public WinCriteria(String operator, boolean isSingleMatch, int count) {
        super("win", operator, isSingleMatch);
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
    public boolean isSatisfied(Player player, Event event, boolean isAchievement) {
        int value;

        // For win criteria, single match mode is not supported.
        if (singleMatch) {
            BedWarsQuests.Logger().warn("WinCriteria does not support single match mode.");
            return false;
        }

        // Retrieve the player's overall or daily statistics based on the isAchievement flag.
        PlayerStatistic statistic;
        if (isAchievement) {
            statistic = BedWarsQuests.BedwarsApi().getStatisticsManager().getStatistic(player.getUniqueId());
        } else {
            statistic = BedWarsQuests.BedwarsApi().getStatisticsManager().getDailyStatistic(player.getUniqueId());
        }

        // If the statistic is null, log a warning and return false.
        if (statistic == null) {
            BedWarsQuests.Logger().warn("Player statistic is null for player: " + player.getName());
            return false;
        }

        // Retrieve the number of wins from the player's statistics.
        value = statistic.getWins();

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
