package io.github.tavstaldev.bedWarsQuests.events;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.EvaluatorRegistry;
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

        EvaluatorRegistry.handleEvent(event, player);
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

        EvaluatorRegistry.handleEvent(event, player);
    }
}
