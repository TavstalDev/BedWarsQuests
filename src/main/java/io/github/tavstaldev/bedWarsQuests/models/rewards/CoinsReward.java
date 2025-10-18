package io.github.tavstaldev.bedWarsQuests.models.rewards;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.RewardAction;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * The CoinsReward class represents a reward action that grants coins to a player.
 */
public class CoinsReward extends RewardAction {
    // The number of coins to be granted.
    private final int coins;

    /**
     * Constructs a CoinsReward instance with the specified number of coins.
     *
     * @param coins The number of coins to be granted.
     */
    public CoinsReward(int coins) {
        super("coins");
        this.coins = coins;
    }

    /**
     * Grants the coins to the specified player and sends a localized message.
     *
     * @param player        The player to whom the coins are granted.
     * @param Name          The name of the reward or achievement.
     * @param isAchievement Indicates whether the reward is for an achievement.
     */
    @Override
    public void grant(Player player, String Name, boolean isAchievement) {
        BedWarsQuests.BanyaszApi().increaseBalance(player.getUniqueId(), coins);
        if (isAchievement)
            BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.Coins.Achievement", Map.of("name", Name, "amount", String.valueOf(coins)));
        else
            BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.Coins.Quest", Map.of("name", Name, "amount", String.valueOf(coins)));
    }

    /**
     * Retrieves the localized lore description of the reward for the specified player.
     *
     * @param player The player for whom the lore is generated.
     * @return A localized string representing the reward's lore.
     */
    @Override
    public String getLore(Player player) {
        return BedWarsQuests.Translator().localize(player, "GUI.Rewards.Coins", Map.of("amount", String.valueOf(coins)));
    }
}
