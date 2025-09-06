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

        for (RewardAction reward : Rewards) {
            reward.grant(player, Name, kind == ECompletionKind.Achievement);
        }
    }
}
