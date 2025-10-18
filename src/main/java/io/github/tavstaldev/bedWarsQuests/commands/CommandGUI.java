package io.github.tavstaldev.bedWarsQuests.commands;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.gui.MainGUI;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.PlayerCache;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.minecorelib.models.command.SubCommandData;
import io.github.tavstaldev.minecorelib.utils.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.BedwarsAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandGUI implements CommandExecutor {
    private final PluginLogger _logger = BedWarsQuests.Logger().withModule(CommandGUI.class);
    @SuppressWarnings("FieldCanBeLocal")
    private final String baseCommand = "bwquests";
    private final List<SubCommandData> _subCommands = new ArrayList<>() {
        {
            // HELP
            add(new SubCommandData("help", "bedwarsquests.commands.help", Map.of(
                "syntax", "",
                "description", "Commands.Help.Desc"
            )));
            // VERSION
            add(new SubCommandData("version", "bedwarsquests.commands.version", Map.of(
                "syntax", "",
                "description", "Commands.Version.Desc"
            )));
            // RELOAD
            add(new SubCommandData("reload", "bedwarsquests.commands.reload", Map.of(
               "syntax", "",
               "description", "Commands.Reload.Desc"
            )));
            // RESET
            add(new SubCommandData("reset", "bedwarsquests.commands.reset", Map.of(
                "syntax", "<player|all>",
                "description", "Commands.Reset.Desc"
            )));
            // OPEN
            add(new SubCommandData("", "bedwarsquests.commands.gui", Map.of(
                "syntax", "",
                "description", "Commands.Gui.Desc"
            )));
        }
    };

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (sender instanceof ConsoleCommandSender) {
            _logger.info(ChatUtils.translateColors("Commands.ConsoleCaller", true).toString());
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("bedwarsquests.commands.gui")) {
            BedWarsQuests.Instance.sendLocalizedMsg(player, "General.NoPermission");
            return true;
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "help":
                case "?": {
                    int page = 1;
                    if (args.length > 1) {
                        try {
                            page = Integer.parseInt(args[1]);
                        } catch (Exception ex) {
                            BedWarsQuests.Instance.sendLocalizedMsg(player, "Commands.Common.InvalidPage");
                            return true;
                        }
                    }

                    help(player, page);
                    return true;
                }
                case "version": {
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("version", BedWarsQuests.Instance.getVersion());
                    BedWarsQuests.Instance.sendLocalizedMsg(player, "Commands.Version.Current", parameters);

                    BedWarsQuests.Instance.isUpToDate().thenAccept(upToDate -> {
                        if (upToDate) {
                            BedWarsQuests.Instance.sendLocalizedMsg(player, "Commands.Version.UpToDate");
                        } else {
                            BedWarsQuests.Instance.sendLocalizedMsg(player, "Commands.Version.Outdated", Map.of("link", BedWarsQuests.Instance.getDownloadUrl()));
                        }
                    }).exceptionally(e -> {
                        _logger.error("Failed to determine update status: " + e.getMessage());
                        return null;
                    });
                    return true;
                }
                case "reload": {
                    if (!player.hasPermission("bedwarsquests.commands.reload")) {
                        BedWarsQuests.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    BedWarsQuests.Instance.reload();
                    BedWarsQuests.Instance.sendLocalizedMsg(player, "Commands.Reload.Done");
                    return true;
                }
                case "reset": {
                    if (!player.hasPermission("bedwarsquests.commands.reset")) {
                        BedWarsQuests.Instance.sendLocalizedMsg(player, "General.NoPermission");
                        return true;
                    }

                    if (args.length < 2) {
                        BedWarsQuests.Instance.sendLocalizedMsg(player, "Commands.Reset.Confirmation");
                        return true;
                    }

                    if (!args[1].equalsIgnoreCase("confirm")) {

                        BedWarsQuests.Instance.sendLocalizedMsg(player, "Commands.Reset.Confirmation");
                        return true;
                    }

                    BedWarsQuests.Database().wipeCompletedAchievements();
                    BedWarsQuests.Database().wipePlayerDailyObjectives();
                    BedWarsQuests.Database().wipePlayerWeeklyObjectives();
                    BedWarsQuests.Database().wipePlayerData();

                    for (Player onlinePlayer : BedWarsQuests.Instance.getServer().getOnlinePlayers()) {
                        PlayerCache playerData = new PlayerCache(onlinePlayer);
                        PlayerCacheManager.add(onlinePlayer.getUniqueId(), playerData);
                    }

                    BedWarsQuests.Instance.sendLocalizedMsg(player, "Commands.Reset.Done");
                    return true;
                }
            }

            BedWarsQuests.Instance.sendLocalizedMsg(player, "Commands.InvalidArguments");
            return true;
        }

        if (BedwarsAPI.getInstance().isPlayerPlayingAnyGame(player))
        {
            BedWarsQuests.Instance.sendLocalizedMsg(player, "Commands.InGame");
            return true;
        }

        MainGUI.open(player);
        return true;
    }

    private void help(Player player, int page) {
        int maxPage = 1 + (_subCommands.size() / 15);

        // Ensure the page number is within valid bounds
        if (page > maxPage)
            page = maxPage;
        if (page < 1)
            page = 1;
        int finalPage = page;

        // Send the help menu title and info
        BedWarsQuests.Instance.sendLocalizedMsg(player, "Commands.Help.Title", Map.of(
                        "current_page", finalPage,
                        "max_page", maxPage
                )
        );
        BedWarsQuests.Instance.sendLocalizedMsg(player, "Commands.Help.Info");

        boolean reachedEnd = false;
        int itemIndex = 0;

        // Display up to 15 subcommands per page
        for (int i = 0; i < 15; i++) {
            int index = itemIndex + (page - 1) * 15;
            if (index >= _subCommands.size()) {
                reachedEnd = true;
                break;
            }
            itemIndex++;

            SubCommandData subCommand = _subCommands.get(index);
            if (!subCommand.hasPermission(player)) {
                i--;
                continue;
            }

            subCommand.send(BedWarsQuests.Instance, player, baseCommand);
        }

        // Display navigation buttons for the help menu
        String previousBtn = BedWarsQuests.Instance.localize(player, "Commands.Help.PrevBtn");
        String nextBtn = BedWarsQuests.Instance.localize(player, "Commands.Help.NextBtn");
        String bottomMsg = BedWarsQuests.Instance.localize(player, "Commands.Help.Bottom")
                .replace("%current_page%", String.valueOf(page))
                .replace("%max_page%", String.valueOf(maxPage));

        Map<String, Component> bottomParams = new HashMap<>();
        if (page > 1)
            bottomParams.put("previous_btn", ChatUtils.translateColors(previousBtn, true)
                    .clickEvent(ClickEvent.runCommand(String.format("/%s help %s", baseCommand, page - 1))));
        else
            bottomParams.put("previous_btn", ChatUtils.translateColors(previousBtn, true));

        if (!reachedEnd && maxPage >= page + 1)
            bottomParams.put("next_btn", ChatUtils.translateColors(nextBtn, true)
                    .clickEvent(ClickEvent.runCommand(String.format("/%s help %s", baseCommand, page + 1))));
        else
            bottomParams.put("next_btn", ChatUtils.translateColors(nextBtn, true));

        Component bottomComp = ChatUtils.buildWithButtons(bottomMsg, bottomParams);
        player.sendMessage(bottomComp);
    }
}
