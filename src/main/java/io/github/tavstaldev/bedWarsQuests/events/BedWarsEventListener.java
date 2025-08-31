package io.github.tavstaldev.bedWarsQuests.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.screamingsandals.bedwars.api.events.BedwarsGameEndEvent;
import org.screamingsandals.bedwars.api.events.BedwarsItemBoughtEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerKilledEvent;
import org.screamingsandals.bedwars.api.events.BedwarsTargetBlockDestroyedEvent;

public class BedWarsEventListener implements Listener {

    public BedWarsEventListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onGameEnd(BedwarsGameEndEvent event) {

    }

    @EventHandler
    public void onItemBought(BedwarsItemBoughtEvent event) {
        if (event.isCancelled())
            return;
    }

    @EventHandler
    public void onTargetBlockDestroyed(BedwarsTargetBlockDestroyedEvent event) {

    }

    @EventHandler
    public void onPlayerKilledEvent(BedwarsPlayerKilledEvent event) {

    }
}
