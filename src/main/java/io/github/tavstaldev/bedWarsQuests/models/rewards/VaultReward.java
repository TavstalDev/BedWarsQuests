package io.github.tavstaldev.bedWarsQuests.models.rewards;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.RewardAction;
import io.github.tavstaldev.bedWarsQuests.utils.EconomyUtils;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * The VaultReward class represents a reward action that grants a specified amount of currency
 * to a player using the Vault economy system.
 */
public class VaultReward extends RewardAction {
    // The amount of currency to be granted.
    private final double amount;

    /**
     * Constructs a VaultReward instance with the specified amount of currency.
     *
     * @param amount The amount of currency to be granted.
     */
    public VaultReward(double amount) {
        super("vault");
        this.amount = amount;
    }

    /**
     * Grants the reward by depositing the specified amount into the player's account.
     * If the economy system is not enabled, the method returns without performing any action.
     * Sends a localized message to the player indicating the reward details.
     *
     * @param player        The player to whom the reward is granted.
     * @param name          The name of the reward or achievement.
     * @param isAchievement Indicates whether the reward is for an achievement.
     */
    @Override
    public void grant(Player player, String name, boolean isAchievement) {
        if (!EconomyUtils.isEnabled())
            return;

        EconomyUtils.deposit(player, amount);
        if (isAchievement)
            BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.Vault.Achievement", Map.of("name", name, "amount", String.valueOf(amount)));
        else
            BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.Vault.Quest", Map.of("name", name, "amount", String.valueOf(amount)));
    }

    /**
     * Retrieves the localized lore description of the reward for the specified player.
     *
     * @param player The player for whom the lore is generated.
     * @return A localized string representing the reward's lore.
     */
    @Override
    public String getLore(Player player) {
        return BedWarsQuests.Translator().localize(player, "GUI.Rewards.Vault", Map.of("amount", String.valueOf(amount)));
    }
}
