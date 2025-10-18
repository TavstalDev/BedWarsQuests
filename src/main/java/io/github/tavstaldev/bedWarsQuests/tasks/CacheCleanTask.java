package io.github.tavstaldev.bedWarsQuests.tasks;

import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A task that periodically cleans up player caches marked for removal.
 * This task is executed asynchronously using the Bukkit scheduler.
 */
public class CacheCleanTask extends BukkitRunnable {
    /**
     * The main logic of the task. This method is executed when the task runs.
     * It checks if there are any player caches marked for removal and removes them.
     */
    @Override
    public void run() {
        // If there are no player caches marked for removal, exit early.
        if (PlayerCacheManager.isMarkedForRemovalEmpty())
            return;

        // Iterate through the set of player IDs marked for removal.
        for (var playerId : PlayerCacheManager.getMarkedForRemovalSet()) {
            // Retrieve the player cache associated with the player ID.
            var playerCache = PlayerCacheManager.get(playerId);

            // If the player cache does not exist, unmark the player ID and continue.
            if (playerCache == null) {
                PlayerCacheManager.unmarkForRemoval(playerId);
                continue;
            }

            // Remove the player cache and unmark the player ID.
            PlayerCacheManager.remove(playerId);
            PlayerCacheManager.unmarkForRemoval(playerId);
        }
    }
}
