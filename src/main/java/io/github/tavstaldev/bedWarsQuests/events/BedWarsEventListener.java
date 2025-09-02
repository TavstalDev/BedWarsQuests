package io.github.tavstaldev.bedWarsQuests.events;

import io.github.tavstaldev.bedWarsQuests.EventMapping;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.screamingsandals.bedwars.api.events.*;

public class BedWarsEventListener implements Listener {
    public BedWarsEventListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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

        EventMapping.handleEvent(event.getCustomer(), event);
    }

    @EventHandler
    public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent event) {
        EventMapping.handleEvent(event.getPlayer(), event);
    }

    @EventHandler
    public void onPlayerKilledEvent(BedwarsPlayerKilledEvent event) {
        // Handle both the killed player and the killer
        EventMapping.handleEvent(event.getPlayer(), event);
        EventMapping.handleEvent(event.getKiller(), event);
    }
}
