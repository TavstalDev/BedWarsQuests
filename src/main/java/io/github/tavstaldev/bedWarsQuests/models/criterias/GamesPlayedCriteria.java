package io.github.tavstaldev.bedWarsQuests.models.criterias;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.AchievementCriteria;
import org.bukkit.event.Event;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;

public class GamesPlayedCriteria extends AchievementCriteria {
    private final int count;

    public GamesPlayedCriteria(String operator, boolean isSingleMatch, int count) {
        super("played", operator, isSingleMatch);
        this.count = count;
    }

    @Override
    public boolean isSatisfied(org.bukkit.entity.Player player, Event event, boolean isAchievement) {
        int value = -1;
        PlayerStatistic statistic;
        // For games played criteria, singleMatch doesn't make sense, so we ignore it.
        if (isAchievement) {
            statistic = BedWarsQuests.BedwarsApi().getStatisticsManager().getStatistic(player.getUniqueId());
        } else {
            statistic = BedWarsQuests.BedwarsApi().getStatisticsManager().getDailyStatistic(player.getUniqueId());
        }
        value = statistic.getGames();

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
