package io.github.tavstaldev.bedWarsQuests.models.criterias;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.AchievementCriteria;
import org.bukkit.event.Event;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;

public class GamesPlayedCriteria extends AchievementCriteria {
    private final int count;

    public GamesPlayedCriteria(int count) {
        super("played");
        this.count = count;
    }

    @Override
    public boolean isSatisfied(org.bukkit.entity.Player player, Event event, boolean isAchievement)
    {
        PlayerStatistic statistic;
        if (isAchievement) {
            statistic = BedWarsQuests.BedwarsApi().getStatisticsManager().getStatistic(player.getUniqueId());
        } else {
            statistic = BedWarsQuests.BedwarsApi().getStatisticsManager().getDailyStatistic(player.getUniqueId());
        }

        return statistic.getGames() >= count;
    }
}
