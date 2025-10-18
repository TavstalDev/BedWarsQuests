package io.github.tavstaldev.bedWarsQuests.models.rewards;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.RewardAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * The CommandReward class represents a reward action that executes a command
 * either as the console or the player.
 */
public class CommandReward extends RewardAction {
    // The command to be executed as part of the reward.
    private final String command;

    // Indicates whether the command should be executed as the console.
    private final boolean asConsole;

    /**
     * Constructs a CommandReward instance with the specified command and execution mode.
     *
     * @param command   The command to be executed.
     * @param asConsole True if the command should be executed as the console, false if as the player.
     */
    public CommandReward(String command, boolean asConsole) {
        super("command");
        this.command = command;
        this.asConsole = asConsole;
    }

    /**
     * Grants the reward by executing the command for the specified player.
     * If the command is set to execute as the console, it will be dispatched by the server.
     * Otherwise, the player will execute the command.
     *
     * @param player        The player for whom the reward is granted.
     * @param name          The name of the reward or achievement.
     * @param isAchievement Indicates whether the reward is for an achievement.
     */
    @Override
    public void grant(Player player, String name, boolean isAchievement) {
        String cmd = command.replace("%player%", player.getName());
        if (asConsole) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
        } else {
            player.performCommand(cmd);
        }
        BedWarsQuests.Instance.sendLocalizedMsg(player, "Rewards.Command");
    }

    /**
     * Retrieves the localized lore description of the reward for the specified player.
     *
     * @param player The player for whom the lore is generated.
     * @return A localized string representing the reward's lore.
     */
    @Override
    public String getLore(Player player) {
        // TODO: Replace the placeholder with the actual command description.
        return BedWarsQuests.Translator().localize(player, "GUI.Rewards.Command", Map.of("command", ""));
    }
}
