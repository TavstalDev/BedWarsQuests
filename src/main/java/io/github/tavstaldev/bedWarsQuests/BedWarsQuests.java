package io.github.tavstaldev.bedWarsQuests;

import com.samjakob.spigui.SpiGUI;
import io.github.tavstaldev.banyaszLib.api.BanyaszApi;
import io.github.tavstaldev.bedWarsQuests.commands.CommandGUI;
import io.github.tavstaldev.bedWarsQuests.database.IDatabase;
import io.github.tavstaldev.bedWarsQuests.database.MySqlDatabase;
import io.github.tavstaldev.bedWarsQuests.database.SqlLiteDatabase;
import io.github.tavstaldev.bedWarsQuests.events.BedWarsEventListener;
import io.github.tavstaldev.bedWarsQuests.events.BlockEventListener;
import io.github.tavstaldev.bedWarsQuests.events.PlayerEventListener;
import io.github.tavstaldev.bedWarsQuests.managers.AchievementManager;
import io.github.tavstaldev.bedWarsQuests.managers.ObjectiveManager;
import io.github.tavstaldev.bedWarsQuests.metrics.Metrics;
import io.github.tavstaldev.bedWarsQuests.tasks.CacheCleanTask;
import io.github.tavstaldev.bedWarsQuests.tasks.RefreshTask;
import io.github.tavstaldev.bedWarsQuests.utils.EconomyUtils;
import io.github.tavstaldev.minecorelib.PluginBase;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.minecorelib.core.PluginTranslator;
import io.github.tavstaldev.minecorelib.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.screamingsandals.bedwars.api.BedwarsAPI;

/**
 * The BedWarsQuests class is the main plugin class for the BedWarsQuests plugin.
 * It handles initialization, event registration, database setup, and integration with other plugins.
 */
public class BedWarsQuests extends PluginBase {
    /** Singleton instance of the plugin. */
    public static BedWarsQuests Instance;

    /** SpiGUI instance for managing GUI interactions. */
    private SpiGUI _spiGUI;

    /** BedwarsAPI instance for interacting with the BedWars plugin. */
    private BedwarsAPI _bedwarsApi;

    /** BanyaszApi instance for interacting with the BanyaszLib plugin. */
    private BanyaszApi _banyaszApi;

    /** Database instance for managing plugin data. */
    private IDatabase _database;

    /** Manager for handling achievements. */
    private AchievementManager _achievementManager;

    /** Manager for handling objectives. */
    private ObjectiveManager _objectiveManager;

    /** Task for cleaning player caches. */
    private CacheCleanTask cacheCleanTask;

    /**
     * Retrieves the plugin logger instance.
     *
     * @return The PluginLogger instance.
     */
    public static PluginLogger Logger() {
        return Instance.getCustomLogger();
    }

    /**
     * Retrieves the plugin translator instance.
     *
     * @return The PluginTranslator instance.
     */
    public static PluginTranslator Translator() {
        return Instance.getTranslator();
    }

    /**
     * Retrieves the plugin configuration instance.
     *
     * @return The BWQConfiguration instance.
     */
    public static BWQConfiguration Config() {
        return (BWQConfiguration) Instance.getConfig();
    }

    /**
     * Retrieves the SpiGUI instance.
     *
     * @return The SpiGUI instance.
     */
    public static SpiGUI GUI() {
        return Instance._spiGUI;
    }

    /**
     * Retrieves the BedwarsAPI instance.
     *
     * @return The BedwarsAPI instance.
     */
    public static BedwarsAPI BedwarsApi() {
        return Instance._bedwarsApi;
    }

    /**
     * Retrieves the BanyaszApi instance.
     *
     * @return The BanyaszApi instance.
     */
    public static BanyaszApi BanyaszApi() {
        return Instance._banyaszApi;
    }

    /**
     * Retrieves the database instance.
     *
     * @return The IDatabase instance.
     */
    public static IDatabase Database() {
        return Instance._database;
    }

    /**
     * Retrieves the achievement manager instance.
     *
     * @return The AchievementManager instance.
     */
    public static AchievementManager AchievementManager() {
        return Instance._achievementManager;
    }

    /**
     * Retrieves the objective manager instance.
     *
     * @return The ObjectiveManager instance.
     */
    public static ObjectiveManager ObjectiveManager() {
        return Instance._objectiveManager;
    }

    /**
     * Constructs a new BedWarsQuests instance.
     */
    public BedWarsQuests() {
        super(true, "https://github.com/TavstalDev/BedWarsQuests/releases/latest");
    }

    /**
     * Called when the plugin is enabled. Handles initialization and setup.
     */
    @Override
    public void onEnable() {
        Instance = this;
        super.onEnable();
        _config = new BWQConfiguration();
        _translator = new PluginTranslator(this, new String[]{"eng", "hun"});
        _logger.info(String.format("Loading %s...", getProjectName()));

        if (VersionUtils.isLegacy()) {
            _logger.error("The plugin is not compatible with legacy versions of Minecraft. Please use a newer version of the game.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register Events
        new PlayerEventListener(this);
        new BlockEventListener(this);
        new BedWarsEventListener(this);

        // Generate config file
        saveDefaultConfig();

        // Load Localizations
        if (!_translator.load()) {
            _logger.error("Failed to load localizations... Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Check BedWars Plugin
        _logger.debug("Hooking into BedWars...");
        if (Bukkit.getPluginManager().isPluginEnabled("BedWars") || Bukkit.getPluginManager().isPluginEnabled("ScreamingBedWars")) {
            _bedwarsApi = BedwarsAPI.getInstance();
            _logger.info("BedWars found and hooked into it.");
        } else {
            _logger.warn("BedWars not found. Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Check BanyaszLib Plugin
        _logger.debug("Hooking into BanyaszLib...");
        if (Bukkit.getPluginManager().isPluginEnabled("BanyaszLib")) {
            _banyaszApi = BanyaszApi.getInstance();
            _logger.info("BanyaszLib found and hooked into it.");
        } else {
            _logger.warn("BanyaszLib not found. Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register economy integration
        _logger.debug("Hooking into Vault...");
        if (EconomyUtils.setupEconomy()) {
            _logger.info("Economy plugin found and hooked into Vault.");
        } else {
            _logger.warn("Economy plugin not found. Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize database based on configuration
        String databaseType = Config().storageType;
        if (databaseType == null) {
            databaseType = "sqlite";
        }
        switch (databaseType.toLowerCase()) {
            case "mysql": {
                _database = new MySqlDatabase();
                break;
            }
            case "sqlite":
            default: {
                _database = new SqlLiteDatabase();
                break;
            }
        }
        _database.load();
        _database.checkSchema();

        // Initialize SpiGUI
        _logger.debug("Initializing SpiGUI...");
        _spiGUI = new SpiGUI(this);

        // Register Commands
        _logger.debug("Registering commands...");
        var command = getCommand("bwquests");
        if (command != null) {
            command.setExecutor(new CommandGUI());
        }

        // Initialize Managers
        _achievementManager = new AchievementManager(this);
        _objectiveManager = new ObjectiveManager(this);

        // Register tasks
        RefreshTask task = new RefreshTask();
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, task, 20L * 30, 20L * 900);

        // Register cache cleanup task.
        if (cacheCleanTask != null && !cacheCleanTask.isCancelled())
            cacheCleanTask.cancel();
        cacheCleanTask = new CacheCleanTask(); // Runs every 5 minutes
        cacheCleanTask.runTaskTimer(this, 0, 5 * 60 * 20);

        // Metrics
        try {
            @SuppressWarnings("unused") Metrics metrics = new Metrics(this, 27757);
        }
        catch (Exception ex)
        {
            _logger.error("Failed to start Metrics: " + ex.getMessage());
        }

        _logger.ok(String.format("%s has been successfully loaded.", getProjectName()));
        if (Config().checkForUpdates) {
            isUpToDate().thenAccept(upToDate -> {
                if (upToDate) {
                    _logger.ok("Plugin is up to date!");
                } else {
                    _logger.warn("A new version of the plugin is available: " + getDownloadUrl());
                }
            }).exceptionally(e -> {
                _logger.error("Failed to determine update status: " + e.getMessage());
                return null;
            });
        }
    }

    /**
     * Called when the plugin is disabled. Handles cleanup.
     */
    @Override
    public void onDisable() {
        super.onDisable();
        _logger.info(String.format("%s has been successfully unloaded.", getProjectName()));
    }

    /**
     * Reloads the plugin configuration and localizations.
     */
    public void reload() {
        _logger.info(String.format("Reloading %s...", getProjectName()));
        _logger.debug("Reloading localizations...");
        _translator.load();
        _logger.debug("Localizations reloaded.");
        _logger.debug("Reloading configuration...");
        _config.load();
        _logger.debug("Configuration reloaded.");
    }
}