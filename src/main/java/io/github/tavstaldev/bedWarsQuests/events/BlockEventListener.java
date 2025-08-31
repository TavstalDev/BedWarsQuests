package io.github.tavstaldev.bedWarsQuests.events;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import org.bukkit.Material;
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

        var block = event.getBlock();
        var player = event.getPlayer();

        // Ignore if in creative mode
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE)
            return;

        // Ignore if not in a game
        if (!BedWarsQuests.BedwarsApi().isPlayerPlayingAnyGame(player))
            return;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        var block = event.getBlock();
        var data = block.getBlockData();
        // Ignore if not a bed
        if (!(data.getMaterial() == Material.BLACK_BED ||
              data.getMaterial() == Material.BLUE_BED ||
              data.getMaterial() == Material.BROWN_BED ||
              data.getMaterial() == Material.CYAN_BED ||
              data.getMaterial() == Material.GRAY_BED ||
              data.getMaterial() == Material.GREEN_BED ||
              data.getMaterial() == Material.LIGHT_BLUE_BED ||
              data.getMaterial() == Material.LIGHT_GRAY_BED ||
              data.getMaterial() == Material.LIME_BED ||
              data.getMaterial() == Material.MAGENTA_BED ||
              data.getMaterial() == Material.ORANGE_BED ||
              data.getMaterial() == Material.PINK_BED ||
              data.getMaterial() == Material.PURPLE_BED ||
              data.getMaterial() == Material.RED_BED ||
              data.getMaterial() == Material.WHITE_BED ||
              data.getMaterial() == Material.YELLOW_BED)) {
            return;
        }
    }
}
