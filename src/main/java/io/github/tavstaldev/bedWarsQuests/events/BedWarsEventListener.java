package io.github.tavstaldev.bedWarsQuests.events;

import io.github.tavstaldev.bedWarsQuests.EventMapping;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.PlayerCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.screamingsandals.bedwars.api.events.*;

public class BedWarsEventListener implements Listener {
    public BedWarsEventListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onGameStart(BedwarsGameStartEvent event) {
        // Wipe player match data
        for (var player : event.getGame().getConnectedPlayers())
        {
            PlayerCache cache = PlayerCacheManager.get(player.getUniqueId());
            if (cache == null)
                continue;

            cache.getMatchStats().reset();
        }
    }

    @EventHandler
    public void onGameEnd(BedwarsGameEndEvent event) {
        for (var player : event.getGame().getConnectedPlayers())
            EventMapping.handleEvent(player, event);
    }

    @EventHandler
    public void onPlayerLeave(BedwarsPlayerLeaveEvent event) {
        if (!event.getGame().isActivated())
            return;
        EventMapping.handleEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onItemBought(BedwarsItemBoughtEvent event) {
        if (event.isCancelled())
            return;

        var player = event.getCustomer();
        PlayerCacheManager.get(player.getUniqueId()).getMatchStats().ItemsBought++;
        EventMapping.handleEvent(player, event);
    }

    @EventHandler
    public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent event) {
        var player = event.getPlayer();
        PlayerCacheManager.get(player.getUniqueId()).getMatchStats().BedsDestroyed++;
        EventMapping.handleEvent(player, event);
    }

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
            if (!event.getGame().getTeamOfPlayer(victimPlayer).isTargetBlockExists()) {
                killerStats.FinalKills++;
            }
            EventMapping.handleEvent(killerPlayer, event);
        }
    }
}
