package io.github.tavstaldev.bedWarsQuests.models.rewards;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.RewardAction;
import org.bukkit.entity.Player;

import java.util.Map;

public class AchievementPointReward extends RewardAction {
    private final int points;

    public AchievementPointReward(int points) {
        super("achievement_points");
        this.points = points;
    }

    @Override
    public void grant(Player player, String name, boolean isAchievement) {
        BedWarsQuests.Database().increaseAchievementPoints(player.getUniqueId(), points);
        BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.AchievementPoints", Map.of("name", name,"amount", String.valueOf(points)));
    }

    @Override
    public String getLore(Player player) {
        return  BedWarsQuests.Translator().Localize(player, "GUI.Rewards.AchievementPoint", Map.of("amount", String.valueOf(points)));
    }
}
