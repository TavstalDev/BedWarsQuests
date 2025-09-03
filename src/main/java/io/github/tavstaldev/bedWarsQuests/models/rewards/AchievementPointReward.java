package io.github.tavstaldev.bedWarsQuests.models.rewards;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.RewardAction;
import org.bukkit.entity.Player;

import java.util.Map;

public class AchievementPointReward extends RewardAction {
    private final long points;

    public AchievementPointReward(long points) {
        super("achievement_points");
        this.points = points;
    }

    @Override
    public void grant(Player player) {
        var data = BedWarsQuests.Database().GetPlayerData(player.getUniqueId().toString());
        if (data == null) return;
        BedWarsQuests.Database().UpdatePlayerData(player.getUniqueId().toString(), data.AchievementPoints + points);
        BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.AchievementPoints", Map.of("amount", String.valueOf(points)));
    }
}
