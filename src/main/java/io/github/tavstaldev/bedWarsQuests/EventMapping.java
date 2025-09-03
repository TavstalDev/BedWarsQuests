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

public class EventMapping {
    private static final Map<String, Class<? extends Event>> eventMap = Map.of(
            "game_end", BedwarsGameEndEvent.class,
            "player_leave", BedwarsPlayerLeaveEvent.class,
            "player_death", BedwarsPlayerKilledEvent.class,
            "bed_destroy", BedwarsTargetBlockDestroyedEvent.class,
            "item_bought", BedwarsItemBoughtEvent.class,
            "block_place", BlockPlaceEvent.class,
            "block_break", BlockBreakEvent.class
    );

    public static Class<? extends Event> getEventClass(String eventName) {
        return eventMap.get(eventName);
    }

    public static String getEventName(Class<? extends Event> eventClass) {
        for (Map.Entry<String, Class<? extends Event>> entry : eventMap.entrySet()) {
            if (entry.getValue().equals(eventClass)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void handleEvent(@NotNull Player player, @NotNull Event event) {
        String trigger = getEventName(event.getClass());
        if (trigger == null)
            return;

        UUID playerUuid = player.getUniqueId();
        PlayerCache cache = PlayerCacheManager.get(playerUuid);

        List<Achievement> achievements = BedWarsQuests.AchievementManager().getAchievementsByTrigger(trigger);
        for (Achievement achievement : achievements) {
            if (cache.isAchievementCompleted(achievement.Id))
                continue;

            if (achievement.Criteria.isSatisfied(player, event, true)) {
                BedWarsQuests.Database().addCompletedAchievement(playerUuid, achievement.Id);
                // Save to cache
                cache.addAchievement(new CompletedAchievementData(playerUuid, achievement.Id));
                achievement.complete(player, ECompletionKind.Achievement);
            }
        }

        List<Achievement> objectives = BedWarsQuests.ObjectiveManager().getObjectivesByTrigger(trigger);
        for (Achievement objective : objectives) {

            Boolean isWeekly = cache.isWeeklyObjective(objective.Id);
            if (isWeekly == null) // The player does not have this objective
                continue;

            if (!objective.Criteria.isSatisfied(player, event, false))
                continue;

            if (isWeekly) {
                if (cache.isWeeklyObjectiveCompleted(objective.Id))
                    continue;

                BedWarsQuests.Database().updatePlayerWeeklyObjective(playerUuid, objective.Id, true);
                // Update in cache
                cache.completeWeeklyObjective(objective.Id);
                objective.complete(player, ECompletionKind.WeeklyObjective);
            }
            else {
                if (cache.isDailyObjectiveCompleted(objective.Id))
                    continue;

                BedWarsQuests.Database().updatePlayerDailyObjective(playerUuid, objective.Id, true);
                // Update in cache
                cache.completeDailyObjective(objective.Id);
                objective.complete(player, ECompletionKind.DailyObjective);
            }
        }
    }
}
