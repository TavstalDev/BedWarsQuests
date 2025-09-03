package io.github.tavstaldev.bedWarsQuests.models.rewards;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.RewardAction;
import io.github.tavstaldev.bedWarsQuests.utils.EconomyUtils;
import org.bukkit.entity.Player;

import java.util.Map;

public class VaultReward extends RewardAction {
    private final double amount;

    public VaultReward(double amount) {
        super("vault");
        this.amount = amount;
    }

    @Override
    public void grant(Player player, boolean isAchievement) {
        if (!EconomyUtils.isEnabled())
            return;

        EconomyUtils.deposit(player, amount);
        if (isAchievement)
            BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.Vault.Achievement", Map.of("amount", String.valueOf(amount)));
        else
            BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.Vault.Quest", Map.of("amount", String.valueOf(amount)));
    }

    @Override
    public String getLore(Player player) {
        return  BedWarsQuests.Translator().Localize(player, "GUI.Rewards.Vault", Map.of("amount", String.valueOf(amount)));
    }
}
