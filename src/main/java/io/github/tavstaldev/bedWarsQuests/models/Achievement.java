package io.github.tavstaldev.bedWarsQuests.models;

import java.util.List;

public class Achievement {
    public String Id;
    public String Name;
    public String Description;
    public AchievementCriteria Criteria;
    public List<RewardAction> Rewards;

    public Achievement(String id, String name, String description, AchievementCriteria criteria, List<RewardAction> rewards) {
        Id = id;
        Name = name;
        Description = description;
        Criteria = criteria;
        Rewards = rewards;
    }
}
