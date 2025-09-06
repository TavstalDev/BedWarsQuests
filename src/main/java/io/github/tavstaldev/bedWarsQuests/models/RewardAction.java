package io.github.tavstaldev.bedWarsQuests.models;

import org.bukkit.entity.Player;

public abstract class RewardAction {
    private final String type;
    public abstract void grant(Player player, String achievementName, boolean isAchievement);
    public abstract String getLore(Player player);

    public RewardAction(String type) {
        this.type = type;
    }
}
