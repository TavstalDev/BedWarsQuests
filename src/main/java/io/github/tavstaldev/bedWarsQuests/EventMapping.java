package io.github.tavstaldev.bedWarsQuests;

import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.Achievement;
import io.github.tavstaldev.bedWarsQuests.models.ECompletionKind;
import io.github.tavstaldev.bedWarsQuests.models.PlayerCache;
import io.github.tavstaldev.bedWarsQuests.models.database.CompletedAchievementData;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.events.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The EventMapping class provides utilities for mapping event names to their corresponding
 * event classes and handling events triggered by players in the BedWarsQuests plugin.
 */
public class EventMapping {
    // A map that associates event names with their corresponding event classes.
    private static final Map<String, Class<? extends Event>> eventMap = Map.of(
            "game_end", BedwarsGameEndEvent.class,
            "player_leave", BedwarsPlayerLeaveEvent.class,
            "player_death", BedwarsPlayerKilledEvent.class,
            "bed_destroy", BedwarsTargetBlockDestroyedEvent.class,
            "item_bought", BedwarsItemBoughtEvent.class,
            "block_place", BlockPlaceEvent.class,
            "block_break", BlockBreakEvent.class
    );

    /**
     * Retrieves the event class associated with the given event name.
     *
     * @param eventName The name of the event.
     * @return The class of the event, or null if no mapping exists.
     */
    public static Class<? extends Event> getEventClass(String eventName) {
        return eventMap.get(eventName);
    }

    /**
     * Retrieves the event name associated with the given event class.
     *
     * @param eventClass The class of the event.
     * @return The name of the event, or null if no mapping exists.
     */
    public static String getEventName(Class<? extends Event> eventClass) {
        for (Map.Entry<String, Class<? extends Event>> entry : eventMap.entrySet()) {
            if (entry.getValue().equals(eventClass)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Handles an event triggered by a player. This method checks if the event satisfies
     * the criteria for achievements or objectives and updates the player's progress accordingly.
     *
     * @param player The player who triggered the event.
     * @param event  The event that was triggered.
     */
    public static void handleEvent(@NotNull Player player, @NotNull Event event) {
        // Retrieve the event name based on the event class.
        String trigger = getEventName(event.getClass());
        if (trigger == null)
            return;

        // Get the player's UUID and cache.
        UUID playerUuid = player.getUniqueId();
        PlayerCache cache = PlayerCacheManager.get(playerUuid);

        // Process achievements associated with the event trigger.
        List<Achievement> achievements = BedWarsQuests.AchievementManager().getAchievementsByTrigger(trigger);
        for (Achievement achievement : achievements) {
            if (cache.isAchievementCompleted(achievement.Id))
                continue;

            // Check if the achievement criteria are satisfied.
            if (achievement.Criteria.isSatisfied(player, event, true)) {
                BedWarsQuests.Database().addCompletedAchievement(playerUuid, achievement.Id);
                // Save to cache.
                cache.addAchievement(new CompletedAchievementData(playerUuid, achievement.Id));
                achievement.complete(player, ECompletionKind.Achievement);
            }
        }

        // Process objectives associated with the event trigger.
        List<Achievement> objectives = BedWarsQuests.ObjectiveManager().getObjectivesByTrigger(trigger);
        for (Achievement objective : objectives) {

            // Check if the player has the objective and whether it is weekly.
            Boolean isWeekly = cache.isWeeklyObjective(objective.Id);
            if (isWeekly == null) // The player does not have this objective.
                continue;

            // Check if the objective criteria are satisfied.
            if (!objective.Criteria.isSatisfied(player, event, false))
                continue;

            if (isWeekly) {
                // Handle weekly objectives.
                if (cache.isWeeklyObjectiveCompleted(objective.Id))
                    continue;

                BedWarsQuests.Database().updatePlayerWeeklyObjective(playerUuid, objective.Id, true);
                // Update in cache.
                cache.completeWeeklyObjective(objective.Id);
                objective.complete(player, ECompletionKind.WeeklyObjective);
            } else {
                // Handle daily objectives.
                if (cache.isDailyObjectiveCompleted(objective.Id))
                    continue;

                BedWarsQuests.Database().updatePlayerDailyObjective(playerUuid, objective.Id, true);
                // Update in cache.
                cache.completeDailyObjective(objective.Id);
                objective.complete(player, ECompletionKind.DailyObjective);
            }
        }
    }
}
