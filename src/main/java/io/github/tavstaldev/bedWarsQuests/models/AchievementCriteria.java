package io.github.tavstaldev.bedWarsQuests.models;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class AchievementCriteria {
    private final String type;
    public abstract boolean isSatisfied(Player player, Event event, boolean isAchievement);

    public AchievementCriteria(String type) {
        this.type = type;
    }
}