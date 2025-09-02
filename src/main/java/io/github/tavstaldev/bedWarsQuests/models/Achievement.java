package io.github.tavstaldev.bedWarsQuests.models;

import org.bukkit.entity.Player;

import java.util.List;

public class Achievement {
    public String Id;
    public String Name;
    public String Description;
    public List<String> Triggers;
    public AchievementCriteria Criteria;
    public List<RewardAction> Rewards;

    public Achievement(String id, String name, String description, List<String> triggers, AchievementCriteria criteria, List<RewardAction> rewards) {
        Id = id;
        Name = name;
        Description = description;
        Triggers = triggers;
        Criteria = criteria;
        Rewards = rewards;
    }

    public void complete(Player player, boolean isAchievement) {
        // TODO: Notify player of completion
        for (RewardAction reward : Rewards) {
            reward.grant(player);
        }
    }
}
