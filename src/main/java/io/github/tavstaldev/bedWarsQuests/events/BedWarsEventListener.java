package io.github.tavstaldev.bedWarsQuests.events;

import io.github.tavstaldev.bedWarsQuests.EventMapping;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.PlayerCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.screamingsandals.bedwars.api.events.*;

/**
 * The BedWarsEventListener class listens for various BedWars game events
 * and handles them accordingly. It updates player statistics, manages
 * achievements, and triggers event handling logic.
 */
public class BedWarsEventListener implements Listener {

    /**
     * Registers the event listener with the plugin's event manager.
     *
     * @param plugin The plugin instance used to register the event listener.
     */
    public BedWarsEventListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Handles the BedwarsGameStartEvent. Resets match statistics for all players
     * connected to the game.
     *
     * @param event The BedwarsGameStartEvent triggered when a game starts.
     */
    @EventHandler
    public void onGameStart(BedwarsGameStartEvent event) {
        // Wipe player match data
        for (var player : event.getGame().getConnectedPlayers()) {
            PlayerCache cache = PlayerCacheManager.get(player.getUniqueId());
            if (cache == null)
                continue;

            cache.getMatchStats().reset();
        }
    }

    /**
     * Handles the BedwarsGameEndEvent. Triggers event handling logic for all
     * players connected to the game.
     *
     * @param event The BedwarsGameEndEvent triggered when a game ends.
     */
    @EventHandler
    public void onGameEnd(BedwarsGameEndEvent event) {
        for (var player : event.getGame().getConnectedPlayers())
            EventMapping.handleEvent(player, event);
    }

    /**
     * Handles the BedwarsPlayerLeaveEvent. Triggers event handling logic for
     * the player leaving the game if the game is active.
     *
     * @param event The BedwarsPlayerLeaveEvent triggered when a player leaves the game.
     */
    @EventHandler
    public void onPlayerLeave(BedwarsPlayerLeaveEvent event) {
        if (!event.getGame().isActivated())
            return;
        EventMapping.handleEvent(event.getPlayer(), event);
    }

    /**
     * Handles the BedwarsItemBoughtEvent. Updates the player's match statistics
     * for items bought and triggers event handling logic.
     *
     * @param event The BedwarsItemBoughtEvent triggered when a player buys an item.
     */
    @EventHandler
    public void onItemBought(BedwarsItemBoughtEvent event) {
        if (event.isCancelled())
            return;

        var player = event.getCustomer();
        PlayerCacheManager.get(player.getUniqueId()).getMatchStats().ItemsBought++;
        EventMapping.handleEvent(player, event);
    }

    /**
     * Handles the BedwarsTargetBlockDestroyedEvent. Updates the player's match
     * statistics for beds destroyed and triggers event handling logic.
     *
     * @param event The BedwarsTargetBlockDestroyedEvent triggered when a player destroys a bed.
     */
    @EventHandler
    public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent event) {
        var player = event.getPlayer();
        PlayerCacheManager.get(player.getUniqueId()).getMatchStats().BedsDestroyed++;
        EventMapping.handleEvent(player, event);
    }

    /**
     * Handles the BedwarsPlayerKilledEvent. Updates the match statistics for both
     * the victim and the killer, and triggers event handling logic for both players.
     *
     * @param event The BedwarsPlayerKilledEvent triggered when a player is killed.
     */
    @EventHandler
    public void onPlayerKilledEvent(BedwarsPlayerKilledEvent event) {
        // Handle both the victim player and the killer
        var victimPlayer = event.getPlayer();
        var matchStats = PlayerCacheManager.get(victimPlayer.getUniqueId()).getMatchStats();
        matchStats.Deaths++;
        matchStats.KillStreak = 0;
        EventMapping.handleEvent(victimPlayer, event);

        var killerPlayer = event.getKiller();
        if (killerPlayer != null) {
            var killerStats = PlayerCacheManager.get(killerPlayer.getUniqueId()).getMatchStats();
            killerStats.Kills++;
            killerStats.KillStreak++;
            var team = event.getGame().getTeamOfPlayer(victimPlayer);
            if (team != null && !team.isTargetBlockExists()) {
                killerStats.FinalKills++;
            }
            EventMapping.handleEvent(killerPlayer, event);
        }
    }
}
