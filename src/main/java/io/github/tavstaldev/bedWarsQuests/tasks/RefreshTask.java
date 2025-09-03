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

public class RefreshTask implements Runnable {
    private LocalDateTime nextDailyRefresh = null;
    private LocalDateTime nextWeeklyReset = null;

    public RefreshTask() {
        refreshResetTimes();
    }

    @Override
    public void run()
    {
        if (!(BedWarsQuests.Config() instanceof ConfigurationBase config)) {
            return;
        }

        boolean saveConfig = false;
        if (nextDailyRefresh != null && LocalDateTime.now().isAfter(nextDailyRefresh)) {
            BedWarsQuests.Database().wipePlayerDailyObjectives();
            LocalDateTime nextDailyReset = LocalDate.now().plusDays(1).atStartOfDay();
            config.set("dates.daily-refresh", nextDailyReset.toString());
            saveConfig = true;

            // Load new daily quests for online players
            for (var player : Bukkit.getOnlinePlayers()) {
                PlayerCache cache = PlayerCacheManager.get(player.getUniqueId());
                if (cache == null)
                    continue;

                cache.generateDailyObjectives();
            }
        }

        if (nextWeeklyReset != null && LocalDateTime.now().isAfter(nextWeeklyReset)) {
            BedWarsQuests.Database().wipePlayerWeeklyObjectives();

            LocalDateTime nextSeasonReset = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atStartOfDay();
            config.set("dates.weekly-refresh", nextSeasonReset.toString());
            saveConfig = true;

            // Load new weekly quests for online players
            for (var player : Bukkit.getOnlinePlayers()) {
                PlayerCache cache = PlayerCacheManager.get(player.getUniqueId());
                if (cache == null)
                    continue;

                cache.generateWeeklyObjectives();
            }
        }

        if (saveConfig)
        {
            config.save();
        }
    }

    private void refreshResetTimes() {
        String seasonRaw = BedWarsQuests.Config().getString("dates.weekly-refresh");
        if (seasonRaw != null)
            nextWeeklyReset = LocalDateTime.parse(seasonRaw);

        String dailyRaw = BedWarsQuests.Config().getString("dates.daily-refresh");
        if (dailyRaw != null)
            nextDailyRefresh = LocalDateTime.parse(dailyRaw);
    }
}
