package io.github.tavstaldev.bedWarsQuests.events;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.EventMapping;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.PlayerMatchStats;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

/**
 * The BlockEventListener class listens for block-related events in the game,
 * such as block placement and block breaking, and updates player statistics
 * accordingly. It also triggers event handling logic for these events.
 */
public class BlockEventListener implements Listener {

    /**
     * Registers the BlockEventListener with the plugin's event manager.
     *
     * @param plugin The plugin instance used to register the event listener.
     */
    public BlockEventListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Handles the BlockPlaceEvent. Updates the player's match statistics for
     * blocks placed and triggers event handling logic.
     *
     * @param event The BlockPlaceEvent triggered when a player places a block.
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;

        var player = event.getPlayer();
        // Ignore if the player is in creative mode.
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE)
            return;

        // Ignore if the player is not currently in a game.
        if (!BedWarsQuests.BedwarsApi().isPlayerPlayingAnyGame(player))
            return;

        // Update the player's match statistics for blocks placed.
        PlayerMatchStats stats = PlayerCacheManager.get(player.getUniqueId()).getMatchStats();
        stats.addBlockPlaced(event.getBlock().getType(), 1);

        // Trigger event handling logic.
        EventMapping.handleEvent(player, event);
    }

    /**
     * Handles the BlockBreakEvent. Updates the player's match statistics for
     * blocks broken and triggers event handling logic.
     *
     * @param event The BlockBreakEvent triggered when a player breaks a block.
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        var player = event.getPlayer();
        // Ignore if the player is in creative mode.
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE)
            return;

        // Ignore if the player is not currently in a game.
        if (!BedWarsQuests.BedwarsApi().isPlayerPlayingAnyGame(player))
            return;

        // Update the player's match statistics for blocks broken.
        PlayerMatchStats stats = PlayerCacheManager.get(player.getUniqueId()).getMatchStats();
        stats.addBlockBroken(event.getBlock().getType(), 1);

        // Trigger event handling logic.
        EventMapping.handleEvent(player, event);
    }
}