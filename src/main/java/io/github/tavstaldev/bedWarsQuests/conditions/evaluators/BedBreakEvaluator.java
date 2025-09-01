package io.github.tavstaldev.bedWarsQuests.conditions.evaluators;

import io.github.tavstaldev.bedWarsQuests.conditions.Condition;
import io.github.tavstaldev.bedWarsQuests.conditions.ConditionEvaluator;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.screamingsandals.bedwars.api.events.BedwarsTargetBlockDestroyedEvent;

public class BedBreakEvaluator implements ConditionEvaluator {
    @Override
    public boolean matches(Event event, Player player, Condition condition) {
        if (!(event instanceof BedwarsTargetBlockDestroyedEvent))
            return false;
        return true;
    }

    @Override
    public void updateProgress(Player player, Condition condition) {

    }
}
