package io.github.tavstaldev.bedWarsQuests.models;

import org.bukkit.GameEvent;
import org.bukkit.entity.Player;

public abstract class AchievementCriteria {
    private final String type;
    public abstract boolean isSatisfied(Player player, GameEvent event, boolean isAchievement);

    public AchievementCriteria(String type) {
        this.type = type;
    }
}