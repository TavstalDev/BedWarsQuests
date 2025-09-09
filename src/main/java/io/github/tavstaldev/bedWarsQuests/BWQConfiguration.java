package io.github.tavstaldev.bedWarsQuests;

import io.github.tavstaldev.bedWarsQuests.utils.IconUtils;
import io.github.tavstaldev.minecorelib.config.ConfigurationBase;
import org.bukkit.Material;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class BWQConfiguration extends ConfigurationBase {

    public BWQConfiguration() {
        super(BedWarsQuests.Instance, "config.yml", null);
    }
    
    public String prefix;
    public boolean checkForUpdates, debug;

    public String storageType, storageFilename, storageHost, storageDatabase, storageUsername, storagePassword, storageTablePrefix;
    public int storagePort;


    public Material guiPlaceholderItem, guiNoPreviousPageItem, guiPreviousPageItem, guiCurrentPageItem, guiNoNextPageItem, guiNextPageItem,
            guiCloseItem, guiBackItem, guiTitleItem, guiAchievementItem, guiDailyQuestItem,  guiCompletedDailyQuestItem,
            guiWeeklyQuestItem, guiCompletedWeeklyQuestItem, guiLockedAchievementItem, guiCompletedAchievementItem;

    @Override
    protected void loadDefaults() {
        // General
        resolve("locale", "hun");
        resolve("usePlayerLocale", true);
        checkForUpdates = resolveGet("checkForUpdates", true);
        debug = resolveGet("debug", false);
        prefix = resolveGet("prefix", "&cBedWars&fQuests &8»");

        // Dates
        resolve("dates.daily-refresh", LocalDate.now().plusDays(1).atStartOfDay().toString());
        resolve("dates.weekly-refresh", LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).atStartOfDay().toString());

        // Storage
        storageType = resolveGet("storage.type", "sqlite");
        storageFilename = resolveGet("storage.filename", "database");
        storageHost = resolveGet("storage.host", "localhost");
        storagePort = resolveGet("storage.port", 3306);
        storageDatabase = resolveGet("storage.database", "minecraft");
        storageUsername = resolveGet("storage.username", "root");
        storagePassword = resolveGet("storage.password", "ascent");
        storageTablePrefix = resolveGet("storage.tablePrefix", "bwq");

        // GUI
        resolve("gui.placeholderItem", "BLACK_STAINED_GLASS_PANE");
        guiPlaceholderItem = IconUtils.getMaterialFromConfig("gui.placeholderItem");
        resolve("gui.noPreviousPageItem", "BLACK_STAINED_GLASS_PANE");
        guiNoPreviousPageItem = IconUtils.getMaterialFromConfig("gui.noPreviousPageItem");
        resolve("gui.previousPageItem", "ARROW");
        guiPreviousPageItem = IconUtils.getMaterialFromConfig("gui.previousPageItem");
        resolve("gui.currentPageItem", "PAPER");
        guiCurrentPageItem = IconUtils.getMaterialFromConfig("gui.currentPageItem");
        resolve("gui.noNextPageItem", "BLACK_STAINED_GLASS_PANE");
        guiNoNextPageItem = IconUtils.getMaterialFromConfig("gui.noNextPageItem");
        resolve("gui.nextPageItem", "ARROW");
        guiNextPageItem = IconUtils.getMaterialFromConfig("gui.nextPageItem");
        resolve("gui.closeItem", "BARRIER");
        guiCloseItem = IconUtils.getMaterialFromConfig("gui.closeItem");
        resolve("gui.backItem", "SPRUCE_DOOR");
        guiBackItem = IconUtils.getMaterialFromConfig("gui.backItem");
        resolve("gui.titleItem", "RED_BED");
        guiTitleItem = IconUtils.getMaterialFromConfig("gui.titleItem");
        resolve("gui.achievementItem", "DIAMOND");
        guiAchievementItem = IconUtils.getMaterialFromConfig("gui.achievementItem");
        resolve("gui.dailyQuestItem", "PAPER");
        guiDailyQuestItem = IconUtils.getMaterialFromConfig("gui.dailyQuestItem");
        resolve("gui.completedDailyQuestItem", "MAP");
        guiCompletedDailyQuestItem = IconUtils.getMaterialFromConfig("gui.completedDailyQuestItem");
        resolve("gui.weeklyQuestItem", "PAPER");
        guiWeeklyQuestItem = IconUtils.getMaterialFromConfig("gui.weeklyQuestItem");
        resolve("gui.completedWeeklyQuestItem", "MAP");
        guiCompletedWeeklyQuestItem = IconUtils.getMaterialFromConfig("gui.completedWeeklyQuestItem");
        resolve("gui.lockedAchievementItem", "GRAY_DYE");
        guiLockedAchievementItem = IconUtils.getMaterialFromConfig("gui.lockedAchievementItem");
        resolve("gui.completedAchievementItem", "LIME_DYE");
        guiCompletedAchievementItem = IconUtils.getMaterialFromConfig("gui.completedAchievementItem");
    }
}
