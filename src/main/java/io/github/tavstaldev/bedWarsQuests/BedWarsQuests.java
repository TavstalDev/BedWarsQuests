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
import io.github.tavstaldev.bedWarsQuests.tasks.RefreshTask;
import io.github.tavstaldev.bedWarsQuests.utils.EconomyUtils;
import io.github.tavstaldev.minecorelib.PluginBase;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import io.github.tavstaldev.minecorelib.core.PluginTranslator;
import io.github.tavstaldev.minecorelib.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.screamingsandals.bedwars.api.BedwarsAPI;

public class BedWarsQuests extends PluginBase {
    public static BedWarsQuests Instance;
    private SpiGUI _spiGUI;
    private BedwarsAPI _bedwarsApi;
    private BanyaszApi _banyaszApi;
    private IDatabase _database;
    private AchievementManager _achievementManager;
    private ObjectiveManager _objectiveManager;

    public static PluginLogger Logger() {
        return Instance.getCustomLogger();
    }

    public static PluginTranslator Translator() {
        return Instance.getTranslator();
    }

    public static FileConfiguration Config() {
        return Instance.getConfig();
    }

    public static SpiGUI GUI() {
        return Instance._spiGUI;
    }

    public static BedwarsAPI BedwarsApi() {
        return Instance._bedwarsApi;
    }

    public static BanyaszApi BanyaszApi() { return Instance._banyaszApi; }
    public static IDatabase Database() {
        return Instance._database;
    }
    public static AchievementManager AchievementManager() {
        return Instance._achievementManager;
    }
    public static ObjectiveManager ObjectiveManager() {
        return Instance._objectiveManager;
    }

    public BedWarsQuests() {
        super("https://github.com/TavstalDev/BedWarsQuests/releases/latest");
    }

    @Override
    public void onEnable() {
        Instance = this;
        _config = new BWQConfiguration();
        _translator = new PluginTranslator(this, new String[]{"eng", "hun"});
        _logger.Info(String.format("Loading %s...", getProjectName()));

        if (VersionUtils.isLegacy()) {
            _logger.Error("The plugin is not compatible with legacy versions of Minecraft. Please use a newer version of the game.");
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
        if (!_translator.Load()) {
            _logger.Error("Failed to load localizations... Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Check BedWars Plugin
        _logger.Debug("Hooking into BedWars...");
        if (Bukkit.getPluginManager().isPluginEnabled("BedWars") || Bukkit.getPluginManager().isPluginEnabled("ScreamingBedWars")) {
            _bedwarsApi = BedwarsAPI.getInstance();
            _logger.Info("BedWars found and hooked into it.");
        } else {
            _logger.Warn("BedWars not found. Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Check BanyaszLib Plugin
        _logger.Debug("Hooking into BanyaszLib...");
        if (Bukkit.getPluginManager().isPluginEnabled("BanyaszLib")) {
            _banyaszApi = BanyaszApi.getInstance();
            _logger.Info("BanyaszLib found and hooked into it.");
        } else {
            _logger.Warn("BanyaszLib not found. Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register economy integration
        _logger.Debug("Hooking into Vault...");
        if (EconomyUtils.setupEconomy()) {
            _logger.Info("Economy plugin found and hooked into Vault.");
        } else {
            _logger.Warn("Economy plugin not found. Unloading...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize database based on configuration
        String databaseType = this.getConfig().getString("storage.type");
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
        _logger.Debug("Initializing SpiGUI...");
        _spiGUI = new SpiGUI(this);

        // Register Commands
        _logger.Debug("Registering commands...");
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

        _logger.Ok(String.format("%s has been successfully loaded.", getProjectName()));
        isUpToDate().thenAccept(upToDate -> {
            if (upToDate) {
                _logger.Ok("Plugin is up to date!");
            } else {
                _logger.Warn("A new version of the plugin is available: " + getDownloadUrl());
            }
        }).exceptionally(e -> {
            _logger.Error("Failed to determine update status: " + e.getMessage());
            return null;
        });
    }

    @Override
    public void onDisable() {
        _logger.Info(String.format("%s has been successfully unloaded.", getProjectName()));
    }

    public void reload() {
        _logger.Info(String.format("Reloading %s...", getProjectName()));
        _logger.Debug("Reloading localizations...");
        _translator.Load();
        _logger.Debug("Localizations reloaded.");
        _logger.Debug("Reloading configuration...");
        this.reloadConfig();
        _logger.Debug("Configuration reloaded.");
    }
}