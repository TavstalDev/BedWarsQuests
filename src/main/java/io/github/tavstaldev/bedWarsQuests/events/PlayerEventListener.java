package io.github.tavstaldev.bedWarsQuests.events;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.PlayerCache;
import io.github.tavstaldev.bedWarsQuests.models.database.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

/**
 * The PlayerEventListener class listens for player-related events such as joining
 * and quitting the server. It manages player data and cache during these events.
 */
public class PlayerEventListener implements Listener {

    /**
     * Registers the PlayerEventListener with the plugin's event manager.
     *
     * @param plugin The plugin instance used to register the event listener.
     */
    public PlayerEventListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Handles the PlayerJoinEvent. Initializes the player's cache and ensures
     * their data exists in the database.
     *
     * @param event The PlayerJoinEvent triggered when a player joins the server.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Create and add a new player cache for the joining player.
        PlayerCache playerCache = new PlayerCache(player);
        PlayerCacheManager.add(player.getUniqueId(), playerCache);

        // Check if the player's data exists in the database; if not, add it.
        PlayerData playerData = BedWarsQuests.Database().getPlayerData(player.getUniqueId());
        if (playerData == null) {
            BedWarsQuests.Database().addPlayerData(player.getUniqueId());
        }
    }

    /**
     * Handles the PlayerQuitEvent. Removes the player's cache when they leave the server.
     *
     * @param event The PlayerQuitEvent triggered when a player quits the server.
     */
    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Remove the player's cache from the cache manager.
        PlayerCacheManager.remove(player.getUniqueId());
    }
}
