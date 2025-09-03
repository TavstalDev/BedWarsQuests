package io.github.tavstaldev.bedWarsQuests;

import io.github.tavstaldev.minecorelib.config.ConfigurationBase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class BWQConfiguration extends ConfigurationBase {

    public BWQConfiguration() {
        super(BedWarsQuests.Instance, "config.yml", null);
    }

    @Override
    protected void loadDefaults() {
        // General
        resolve("locale", "hun");
        resolve("usePlayerLocale", true);
        resolve("updateChecker", true);
        resolve("debug", false);
        resolve("prefix", "&cBedWars&fQuests &8»");

        // Dates
        resolve("dates.daily-refresh", LocalDate.now().plusDays(1).atStartOfDay().toString());
        resolve("dates.weekly-refresh", LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atStartOfDay().toString());

        // Storage
        resolve("storage.type", "sqlite");
        resolve("storage.filename", "database");
        resolve("storage.host", "localhost");
        resolve("storage.port", 3306);
        resolve("storage.database", "minecraft");
        resolve("storage.username", "root");
        resolve("storage.password", "ascent");
        resolve("storage.tablePrefix", "bwq");

        // GUI
        resolve("gui.placeholderItem", "BLACK_STAINED_GLASS_PANE");
        resolve("gui.noPreviousPageItem", "BLACK_STAINED_GLASS_PANE");
        resolve("gui.previousPageItem", "ARROW");
        resolve("gui.currentPageItem", "PAPER");
        resolve("gui.noNextPageItem", "BLACK_STAINED_GLASS_PANE");
        resolve("gui.nextPageItem", "ARROW");
        resolve("gui.closeItem", "BARRIER");
        resolve("gui.backItem", "SPRUCE_DOOR");
        resolve("gui.titleItem", "RED_BED");
        resolve("gui.achievementItem", "DIAMOND");
        resolve("gui.dailyQuestItem", "PAPER");
        resolve("gui.completedDailyQuestItem", "MAP");
        resolve("gui.weeklyQuestItem", "PAPER");
        resolve("gui.completedWeeklyQuestItem", "MAP");
        resolve("gui.lockedAchievementItem", "GRAY_DYE");
        resolve("gui.completedAchievementItem", "LIME_DYE");
    }
}
