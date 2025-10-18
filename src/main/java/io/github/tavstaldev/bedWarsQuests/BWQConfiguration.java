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
        resolve("locale", "eng");
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
        String material = resolveGet("gui.placeholderItem", "BLACK_STAINED_GLASS_PANE");
        guiPlaceholderItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.noPreviousPageItem", "BLACK_STAINED_GLASS_PANE");
        guiNoPreviousPageItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.previousPageItem", "ARROW");
        guiPreviousPageItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.currentPageItem", "PAPER");
        guiCurrentPageItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.noNextPageItem", "BLACK_STAINED_GLASS_PANE");
        guiNoNextPageItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.nextPageItem", "ARROW");
        guiNextPageItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.closeItem", "BARRIER");
        guiCloseItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.backItem", "SPRUCE_DOOR");
        guiBackItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.titleItem", "RED_BED");
        guiTitleItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.achievementItem", "DIAMOND");
        guiAchievementItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.dailyQuestItem", "PAPER");
        guiDailyQuestItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.completedDailyQuestItem", "MAP");
        guiCompletedDailyQuestItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.weeklyQuestItem", "PAPER");
        guiWeeklyQuestItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.completedWeeklyQuestItem", "MAP");
        guiCompletedWeeklyQuestItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.lockedAchievementItem", "GRAY_DYE");
        guiLockedAchievementItem = IconUtils.getMaterial(material);
        material =resolveGet("gui.completedAchievementItem", "LIME_DYE");
        guiCompletedAchievementItem = IconUtils.getMaterial(material);
    }
}
