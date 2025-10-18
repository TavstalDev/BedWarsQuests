package io.github.tavstaldev.bedWarsQuests.models.criterias;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.AchievementCriteria;
import org.bukkit.event.Event;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;

/**
 * The GamesPlayedCriteria class represents a specific achievement criterion
 * that checks if a player has participated in a certain number of games.
 */
public class GamesPlayedCriteria extends AchievementCriteria {
    // The number of games that need to be played to satisfy the criterion.
    private final int count;

    /**
     * Constructs a GamesPlayedCriteria instance with the specified parameters.
     *
     * @param operator      The comparison operator to use (e.g., EQUALS, GREATER_THAN).
     * @param isSingleMatch Whether the criterion applies to a single match or all matches.
     * @param count         The number of games required to satisfy the criterion.
     */
    public GamesPlayedCriteria(String operator, boolean isSingleMatch, int count) {
        super("played", operator, isSingleMatch);
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
    public boolean isSatisfied(org.bukkit.entity.Player player, Event event, boolean isAchievement) {
        int value;
        PlayerStatistic statistic;
        // For games played criteria, singleMatch doesn't make sense, so we ignore it.
        if (singleMatch) {
            BedWarsQuests.Logger().warn("GamesPlayedCriteria does not support single match mode.");
            return false;
        }

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

        // Retrieve the number of games played from the player's statistics.
        value = statistic.getGames();

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
