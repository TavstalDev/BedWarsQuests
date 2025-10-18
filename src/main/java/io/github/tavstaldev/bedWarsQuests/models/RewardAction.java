package io.github.tavstaldev.bedWarsQuests.models;

import org.bukkit.entity.Player;

/**
 * The RewardAction class serves as a base class for defining reward actions
 * that can be granted to players upon completing achievements or objectives.
 * Subclasses must implement the methods to grant rewards and provide lore descriptions.
 */
public abstract class RewardAction {
    // The type of the reward action (e.g., "item", "experience", etc.).
    private final String type;

    /**
     * Grants the reward to the specified player.
     *
     * @param player          The player to whom the reward is granted.
     * @param achievementName The name of the achievement associated with the reward.
     * @param isAchievement   Indicates whether the reward is for an achievement.
     */
    public abstract void grant(Player player, String achievementName, boolean isAchievement);

    /**
     * Retrieves the lore description of the reward for the specified player.
     *
     * @param player The player for whom the lore is generated.
     * @return A string representing the lore description of the reward.
     */
    public abstract String getLore(Player player);

    /**
     * Constructs a RewardAction instance with the specified type.
     *
     * @param type The type of the reward action.
     */
    public RewardAction(String type) {
        this.type = type;
    }
}