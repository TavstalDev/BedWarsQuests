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

public class BlockEventListener implements Listener {
    public BlockEventListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled())
            return;

        var player = event.getPlayer();
        // Ignore if in creative mode
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE)
            return;

        // Ignore if not in a game
        if (!BedWarsQuests.BedwarsApi().isPlayerPlayingAnyGame(player))
            return;

        PlayerMatchStats stats = PlayerCacheManager.get(player.getUniqueId()).getMatchStats();
        stats.addBlockPlaced(event.getBlock().getType(), 1);
        EventMapping.handleEvent(player, event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        var player = event.getPlayer();
        // Ignore if in creative mode
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE)
            return;

        // Ignore if not in a game
        if (!BedWarsQuests.BedwarsApi().isPlayerPlayingAnyGame(player))
            return;

        PlayerMatchStats stats = PlayerCacheManager.get(player.getUniqueId()).getMatchStats();
        stats.addBlockBroken(event.getBlock().getType(), 1);
        EventMapping.handleEvent(player, event);
    }
}
