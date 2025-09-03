package io.github.tavstaldev.bedWarsQuests.models.criterias;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.AchievementCriteria;
import io.github.tavstaldev.bedWarsQuests.models.PlayerMatchStats;
import org.bukkit.GameEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;

public class BedBreakCriteria extends AchievementCriteria {
    private final int count;

    public BedBreakCriteria(String operator, boolean isSingleMatch, int count) {
        super("bed_break", operator, isSingleMatch);
        this.count = count;
    }

    @Override
    public boolean isSatisfied(Player player, Event event, boolean isAchievement) {
        int value = -1;
        if (singleMatch)
        {
            value = PlayerCacheManager.get(player.getUniqueId()).getMatchStats().BedsDestroyed;
        }
        else {
            PlayerStatistic statistic;
            if (isAchievement) {
                statistic = BedWarsQuests.BedwarsApi().getStatisticsManager().getStatistic(player.getUniqueId());
            } else {
                statistic = BedWarsQuests.BedwarsApi().getStatisticsManager().getDailyStatistic(player.getUniqueId());
            }
            value = statistic.getDestroyedBeds();
        }

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
