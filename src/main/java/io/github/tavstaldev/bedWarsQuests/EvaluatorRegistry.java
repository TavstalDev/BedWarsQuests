package io.github.tavstaldev.bedWarsQuests;

import io.github.tavstaldev.bedWarsQuests.conditions.ConditionEvaluator;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EvaluatorRegistry {
    private static final Map<String, ConditionEvaluator> evaluators = new HashMap<>();

    public static void register(@NotNull String eventType, @NotNull ConditionEvaluator evaluator) {
        evaluators.put(eventType.toUpperCase(), evaluator);
    }

    public static void clear() {
        evaluators.clear();
    }

    public static ConditionEvaluator get(@NotNull String eventType) {
        return evaluators.get(eventType.toUpperCase());
    }

    public static void handleEvent(@NotNull Event event, @NotNull Player player) {
        String eventName = event.getEventName();
        ConditionEvaluator evaluator = get(eventName);

        if (evaluator == null)
            return;

        // Handle achievements

        // Handle objectives
    }
}


