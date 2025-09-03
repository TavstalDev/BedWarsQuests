package io.github.tavstaldev.bedWarsQuests.events;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.EventMapping;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.PlayerCache;
import io.github.tavstaldev.bedWarsQuests.models.database.DailyObjectiveData;
import io.github.tavstaldev.bedWarsQuests.models.database.WeeklyObjectiveData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.Collections;

public class PlayerEventListener implements Listener {
    public PlayerEventListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerCache playerData = new PlayerCache(player);
        if (playerData.getDailyObjectives().isEmpty()) {

            var objectives = BedWarsQuests.ObjectiveManager().getObjectives();
            if (!playerData.getWeeklyObjectives().isEmpty()) {
                for (var weeklyObj : playerData.getWeeklyObjectives()) {
                    objectives.removeIf(obj -> obj.Id.equals(weeklyObj.ObjectiveId));
                }
            }

            Collections.shuffle(objectives);
            int numToTake = Math.min(3, objectives.size());
            for (var item : objectives.subList(0, numToTake)) {
                BedWarsQuests.Database().AddPlayerDailyObjective(player.getUniqueId().toString(), item.Id);
                playerData.addDailyObjective(new DailyObjectiveData(player.getUniqueId(), item.Id, false));
            }
        }

        if (playerData.getWeeklyObjectives().isEmpty()) {
            var objectives = BedWarsQuests.ObjectiveManager().getObjectives();
            if (!playerData.getDailyObjectives().isEmpty()) {
                for (var weeklyObj : playerData.getDailyObjectives()) {
                    objectives.removeIf(obj -> obj.Id.equals(weeklyObj.ObjectiveId));
                }
            }
            Collections.shuffle(objectives);
            int numToTake = Math.min(3, objectives.size());
            for (var item : objectives.subList(0, numToTake)) {
                BedWarsQuests.Database().AddPlayerWeeklyObjective(player.getUniqueId().toString(), item.Id);
                playerData.addWeeklyObjective(new WeeklyObjectiveData(player.getUniqueId(), item.Id, false));
            }
        }

        PlayerCacheManager.add(player.getUniqueId(), playerData);
    }

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerCacheManager.remove(player.getUniqueId());
    }

    @EventHandler
    public void onItemPickup(PlayerAttemptPickupItemEvent event) {
        if (event.isCancelled())
            return;

        var player = event.getPlayer();
        // Ignore if not in survival mode
        if (player.getGameMode() != GameMode.SURVIVAL)
            return;

        // Ignore if not in a game
        if (!BedWarsQuests.BedwarsApi().isPlayerPlayingAnyGame(player))
            return;

        EventMapping.handleEvent(player, event);
    }
}
