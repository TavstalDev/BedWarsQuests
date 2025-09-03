package io.github.tavstaldev.bedWarsQuests.models.rewards;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.RewardAction;
import org.bukkit.entity.Player;

import java.util.Map;

public class CoinsReward extends RewardAction {
    private final int coins;

    public CoinsReward(int coins) {
        super("coins");
        this.coins = coins;
    }

    @Override
    public void grant(Player player) {
        BedWarsQuests.BanyaszApi().increaseBalance(player.getUniqueId(), coins);
        BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.Coins", Map.of("amount", String.valueOf(coins)));
    }
}
