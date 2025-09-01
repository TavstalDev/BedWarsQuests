package io.github.tavstaldev.bedWarsQuests.conditions;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public interface ConditionEvaluator {
    // Called when an event happens
    boolean matches(Event event, Player player, Condition condition);

    // How to update player progress if it matches
    void updateProgress(Player player, Condition condition);
}

