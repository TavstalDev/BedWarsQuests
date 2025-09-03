package io.github.tavstaldev.bedWarsQuests.models.criterias;

import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.AchievementCriteria;

public class BlockBreakCriteria extends AchievementCriteria {
    private final int count;
    private final String material;

    public BlockBreakCriteria(String operator, boolean isSingleMatch, int count, String material) {
        super("block_break", operator, isSingleMatch);
        this.count = count;
        this.material = material;
    }

    @Override
    public boolean isSatisfied(org.bukkit.entity.Player player, org.bukkit.event.Event event, boolean isAchievement) {
        int value = PlayerCacheManager.get(player.getUniqueId()).getMatchStats().getBlocksBroken(material);
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
