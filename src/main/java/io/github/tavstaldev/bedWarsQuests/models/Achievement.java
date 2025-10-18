package io.github.tavstaldev.bedWarsQuests.models;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * The Achievement class represents an achievement in the BedWarsQuests plugin.
 * It contains details about the achievement, including its ID, name, description,
 * triggers, criteria, and rewards.
 */
public class Achievement {
    // The unique identifier of the achievement.
    public String Id;

    // The name of the achievement.
    public String Name;

    // A brief description of the achievement.
    public String Description;

    // A list of triggers associated with the achievement.
    public List<String> Triggers;

    // The criteria that must be met to complete the achievement.
    public AchievementCriteria Criteria;

    // A list of rewards granted upon completing the achievement.
    public List<RewardAction> Rewards;

    /**
     * Constructs an Achievement instance with the specified details.
     *
     * @param id          The unique identifier of the achievement.
     * @param name        The name of the achievement.
     * @param description A brief description of the achievement.
     * @param triggers    A list of triggers associated with the achievement.
     * @param criteria    The criteria that must be met to complete the achievement.
     * @param rewards     A list of rewards granted upon completing the achievement.
     */
    public Achievement(String id, String name, String description, List<String> triggers, AchievementCriteria criteria, List<RewardAction> rewards) {
        Id = id;
        Name = name;
        Description = description;
        Triggers = triggers;
        Criteria = criteria;
        Rewards = rewards;
    }

    /**
     * Completes the achievement for the specified player and handles the completion kind.
     * Sends a localized message to the player and updates the database based on the completion kind.
     * Grants the associated rewards to the player.
     *
     * @param player The player who completed the achievement.
     * @param kind   The kind of completion (e.g., Achievement, DailyObjective, WeeklyObjective).
     */
    public void complete(Player player, ECompletionKind kind) {

        switch (kind) {
            case Achievement: {
                BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.AchievementComplete", Map.of("achievement_name", Name));
                break;
            }
            case DailyObjective: {
                BedWarsQuests.Database().increaseCompletedDailyObjectives(player.getUniqueId());
                break;
            }
            case WeeklyObjective: {
                BedWarsQuests.Database().increaseCompletedWeeklyObjectives(player.getUniqueId());
                break;
            }
        }

        // Grant all rewards associated with the achievement.
        for (RewardAction reward : Rewards) {
            reward.grant(player, Name, kind == ECompletionKind.Achievement);
        }
    }
}
