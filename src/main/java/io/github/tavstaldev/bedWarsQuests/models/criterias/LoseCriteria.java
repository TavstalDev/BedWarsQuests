package io.github.tavstaldev.bedWarsQuests.models.criterias;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.AchievementCriteria;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;

public class LoseCriteria extends AchievementCriteria {
    private final int count;

    public LoseCriteria(String operator, boolean isSingleMatch, int count) {
        super("lose", operator, isSingleMatch);
        this.count = count;
    }

    @Override
    public boolean isSatisfied(Player player, Event event, boolean isAchievement) {
        int value = -1;
        // For lose criteria, single match doesn't make sense, so we ignore it.
        PlayerStatistic statistic;
        if (isAchievement) {
            statistic = BedWarsQuests.BedwarsApi().getStatisticsManager().getStatistic(player.getUniqueId());
        } else {
            statistic = BedWarsQuests.BedwarsApi().getStatisticsManager().getDailyStatistic(player.getUniqueId());
        }
        if (statistic == null) {
            BedWarsQuests.Logger().Warn("Player statistic is null for player: " + player.getName());
            return false;
        }
        value = statistic.getLoses();

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
