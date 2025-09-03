package io.github.tavstaldev.bedWarsQuests.models.rewards;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.RewardAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class CommandReward extends RewardAction {
    private final String command;
    private final boolean asConsole;

    public CommandReward(String command, boolean asConsole) {
        super("command");
        this.command = command;
        this.asConsole = asConsole;
    }

    @Override
    public void grant(Player player, boolean isAchievement) {
        String cmd = command.replace("%player%", player.getName());
        if (asConsole) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
        } else {
            player.performCommand(cmd);
        }
        BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.Command");
    }

    @Override
    public String getLore(Player player) {
        // TODO
        return  BedWarsQuests.Translator().Localize(player, "GUI.Rewards.Command", Map.of("command", ""));
    }
}
