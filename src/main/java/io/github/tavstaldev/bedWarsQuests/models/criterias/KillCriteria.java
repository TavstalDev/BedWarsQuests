package io.github.tavstaldev.bedWarsQuests.models.criterias;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.AchievementCriteria;
import org.bukkit.GameEvent;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;

public class KillCriteria extends AchievementCriteria {
    private final int count;

    public KillCriteria(int count) {
        super("kill");
        this.count = count;
    }

    @Override
    public boolean isSatisfied(Player player, GameEvent event, boolean isAchievement) {
        PlayerStatistic statistic;
        if (isAchievement) {
            statistic = BedWarsQuests.BedwarsApi().getStatisticsManager().getStatistic(player.getUniqueId());
        } else {
            statistic = BedWarsQuests.BedwarsApi().getStatisticsManager().getDailyStatistic(player.getUniqueId());
        }

        return statistic.getKills() >= count;
    }
}
