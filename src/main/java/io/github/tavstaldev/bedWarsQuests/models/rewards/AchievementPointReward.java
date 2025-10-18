package io.github.tavstaldev.bedWarsQuests.models.rewards;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.RewardAction;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * The AchievementPointReward class represents a reward action that grants achievement points to a player.
 */
public class AchievementPointReward extends RewardAction {
    // The number of achievement points to be granted.
    private final int points;

    /**
     * Constructs an AchievementPointReward instance with the specified number of points.
     *
     * @param points The number of achievement points to be granted.
     */
    public AchievementPointReward(int points) {
        super("achievement_points");
        this.points = points;
    }

    /**
     * Grants the achievement points to the specified player and sends a localized message.
     *
     * @param player        The player to whom the points are granted.
     * @param name          The name of the reward or achievement.
     * @param isAchievement Indicates whether the reward is for an achievement.
     */
    @Override
    public void grant(Player player, String name, boolean isAchievement) {
        BedWarsQuests.Database().increaseAchievementPoints(player.getUniqueId(), points);
        BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.AchievementPoints", Map.of("name", name, "amount", String.valueOf(points)));
    }

    /**
     * Retrieves the localized lore description of the reward for the specified player.
     *
     * @param player The player for whom the lore is generated.
     * @return A localized string representing the reward's lore.
     */
    @Override
    public String getLore(Player player) {
        return BedWarsQuests.Translator().localize(player, "GUI.Rewards.AchievementPoint", Map.of("amount", String.valueOf(points)));
    }
}
