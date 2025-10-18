package io.github.tavstaldev.bedWarsQuests.tasks;

import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.managers.PlayerCacheManager;
import io.github.tavstaldev.bedWarsQuests.models.PlayerCache;
import io.github.tavstaldev.minecorelib.config.ConfigurationBase;
import org.bukkit.Bukkit;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * The RefreshTask class is responsible for managing the periodic refresh of daily and weekly objectives
 * for players in the BedWarsQuests plugin. It ensures that objectives are reset at the appropriate times
 * and new objectives are generated for online players.
 */
public class RefreshTask implements Runnable {
    // The next scheduled time for daily refresh.
    private LocalDateTime nextDailyRefresh = null;

    // The next scheduled time for weekly reset.
    private LocalDateTime nextWeeklyReset = null;

    /**
     * Constructs a new RefreshTask and initializes the reset times
     * by reading from the configuration.
     */
    public RefreshTask() {
        refreshResetTimes();
    }

    /**
     * The main logic of the task. This method is executed periodically
     * and handles the refresh of daily and weekly objectives.
     */
    @Override
    public void run() {
        ConfigurationBase config = BedWarsQuests.Config();
        boolean saveConfig = false;

        // Check if it's time for the daily refresh.
        if (nextDailyRefresh != null && LocalDateTime.now().isAfter(nextDailyRefresh)) {
            // Wipe daily objectives from the database.
            BedWarsQuests.Database().wipePlayerDailyObjectives();

            // Schedule the next daily refresh.
            LocalDateTime nextDailyReset = LocalDate.now().plusDays(1).atStartOfDay();
            config.set("dates.daily-refresh", nextDailyReset.toString());
            saveConfig = true;

            // Generate new daily objectives for online players.
            for (var player : Bukkit.getOnlinePlayers()) {
                PlayerCache cache = PlayerCacheManager.get(player.getUniqueId());
                if (cache == null)
                    continue;

                cache.generateDailyObjectives();
            }
        }

        // Check if it's time for the weekly reset.
        if (nextWeeklyReset != null && LocalDateTime.now().isAfter(nextWeeklyReset)) {
            // Wipe weekly objectives from the database.
            BedWarsQuests.Database().wipePlayerWeeklyObjectives();

            // Schedule the next weekly reset.
            LocalDateTime nextSeasonReset = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atStartOfDay();
            config.set("dates.weekly-refresh", nextSeasonReset.toString());
            saveConfig = true;

            // Generate new weekly objectives for online players.
            for (var player : Bukkit.getOnlinePlayers()) {
                PlayerCache cache = PlayerCacheManager.get(player.getUniqueId());
                if (cache == null)
                    continue;

                cache.generateWeeklyObjectives();
            }
        }

        // Save the updated configuration if changes were made.
        if (saveConfig) {
            config.save();
        }
    }

    /**
     * Refreshes the reset times for daily and weekly objectives by reading
     * the values from the configuration file.
     */
    private void refreshResetTimes() {
        // Retrieve the next weekly reset time from the configuration.
        String seasonRaw = BedWarsQuests.Config().getString("dates.weekly-refresh");
        if (seasonRaw != null)
            nextWeeklyReset = LocalDateTime.parse(seasonRaw);

        // Retrieve the next daily refresh time from the configuration.
        String dailyRaw = BedWarsQuests.Config().getString("dates.daily-refresh");
        if (dailyRaw != null)
            nextDailyRefresh = LocalDateTime.parse(dailyRaw);
    }
}
