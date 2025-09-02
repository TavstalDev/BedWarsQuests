package io.github.tavstaldev.bedWarsQuests.models.rewards;

import io.github.tavstaldev.bedWarsQuests.models.RewardAction;
import io.github.tavstaldev.bedWarsQuests.utils.EconomyUtils;
import org.bukkit.entity.Player;

public class VaultReward extends RewardAction {
    private final double amount;

    public VaultReward(double amount) {
        super("vault");
        this.amount = amount;
    }

    @Override
    public void grant(Player player) {
        if (!EconomyUtils.isEnabled())
            return;

        EconomyUtils.deposit(player, amount);
    }
}
