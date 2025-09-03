package io.github.tavstaldev.bedWarsQuests.models;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

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

        if (isAchievement)
            BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.Achievement", Map.of("achievement_name", Name));

        for (RewardAction reward : Rewards) {
            reward.grant(player);
        }
    }
}
