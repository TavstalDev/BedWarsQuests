package io.github.tavstaldev.bedWarsQuests.models.criterias;

import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.AchievementCriteria;

public class FinalKillCriteria extends AchievementCriteria {
    private final int count;

    public FinalKillCriteria(String operator, boolean isSingleMatch, int count) {
        super("final_kill", operator, isSingleMatch);
        this.count = count;
    }

    @Override
    public boolean isSatisfied(org.bukkit.entity.Player player, org.bukkit.event.Event event, boolean isAchievement) {
        int value = PlayerCacheManager.get(player.getUniqueId()).getMatchStats().FinalKills;
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
