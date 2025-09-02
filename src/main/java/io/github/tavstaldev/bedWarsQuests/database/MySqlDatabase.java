package io.github.tavstaldev.bedWarsQuests.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.database.CompletedAchievementData;
import io.github.tavstaldev.bedWarsQuests.models.database.DailyObjectiveData;
import io.github.tavstaldev.bedWarsQuests.models.database.PlayerData;
import io.github.tavstaldev.bedWarsQuests.models.database.WeeklyObjectiveData;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySqlDatabase implements IDatabase {
    private static HikariDataSource _dataSource;
    private static FileConfiguration getConfig() { return BedWarsQuests.Instance.getConfig(); }
    private static final PluginLogger _logger = BedWarsQuests.Logger().WithModule(MySqlDatabase.class);

    @Override
    public void Load() {
        _dataSource = CreateDataSource();
    }

    @Override
    public void Unload() {
        if (_dataSource != null) {
            if (!_dataSource.isClosed())
                _dataSource.close();
        }
    }

    public HikariDataSource CreateDataSource() {
        try
        {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", getConfig().getString("storage.host"), getConfig().getString("storage.port"), getConfig().getString("storage.database"))); // Address of your running MySQL database
            config.setUsername(getConfig().getString("storage.username")); // Username
            config.setPassword(getConfig().getString("storage.password")); // Password
            config.setMaximumPoolSize(10); // Pool size defaults to 10
            config.setMaxLifetime(30000);
            return new HikariDataSource(config);
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened during the creation of database connection...\n%s", ex.getMessage()));
            return null;
        }
    }

    @Override
    public void CheckSchema() {
        try (Connection connection = _dataSource.getConnection())
        {
            // PlayerData
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s_playerData (" +
                    "PlayerId VARCHAR(36) PRIMARY KEY, " +
                    "AchievementPoints BIGINT, " +
                    "CompletedDailyObjectives INT(11), " +
                    "CompletedWeeklyObjectives INT(11));",
                    getConfig().getString("storage.tablePrefix")
            );
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();

            // Completed Achievements
            sql = String.format("CREATE TABLE IF NOT EXISTS %s_comp_achievements (" +
                            "PlayerId VARCHAR(36), " +
                            "AchievementId VARCHAR(64);",
                    getConfig().getString("storage.tablePrefix")
            );
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();

            // Daily Objectives
            sql = String.format("CREATE TABLE IF NOT EXISTS %s_daily_obj (" +
                            "PlayerId VARCHAR(36), " +
                            "ObjectiveId VARCHAR(64), " +
                            "IsCompleted BOOLEAN);",
                    getConfig().getString("storage.tablePrefix")
            );
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();

            // Weekly Objectives
            sql = String.format("CREATE TABLE IF NOT EXISTS %s_weekly_obj (" +
                            "PlayerId VARCHAR(36), " +
                            "ObjectiveId VARCHAR(64), " +
                            "IsCompleted BOOLEAN);",
                    getConfig().getString("storage.tablePrefix")
            );
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while creating tables...\n%s", ex.getMessage()));
        }
    }

    //#region PlayerData
    @Override
    public void AddPlayerData(String playerUUID) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("INSERT INTO %s_playerData (PlayerId, AchievementPoints, CompletedDailyObjectives, CompletedWeeklyObjectives) " +
                            "VALUES (?, ?, ?, ?);",
                    getConfig().getString("storage.tablePrefix"));

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameters for the prepared statement
                statement.setString(1, playerUUID);
                statement.setInt(2, 0);
                statement.setInt(3,0);
                statement.setInt(4, 0);

                // Execute the query
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while adding playerData...\n%s", ex.getMessage()));
        }
    }

    @Override
    public void UpdatePlayerData(String playerUUID, long achievementPoints, int completedDailyObjectives, int completedWeeklyObjectives) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("UPDATE %s_playerData SET AchievementPoints=?, CompletedDailyObjectives=?, CompletedWeeklyObjectives=? WHERE PlayerId=?;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, achievementPoints);
                statement.setInt(2, completedDailyObjectives);
                statement.setInt(3, completedWeeklyObjectives);
                statement.setString(4, playerUUID);
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while updating the playerData table...\n%s", ex.getMessage()));
        }
    }

    @Override
    public void UpdatePlayerData(String playerUUID, int completedDailyObjectives, int completedWeeklyObjectives) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("UPDATE %s_playerData SET CompletedDailyObjectives=?, CompletedWeeklyObjectives=? WHERE PlayerId=?;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, completedDailyObjectives);
                statement.setInt(2, completedWeeklyObjectives);
                statement.setString(3, playerUUID);
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while updating the playerData table...\n%s", ex.getMessage()));
        }
    }

    @Override
    public void UpdatePlayerData(String playerUUID, long achievementPoints) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("UPDATE %s_playerData SET AchievementPoints=? WHERE PlayerId=?;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, achievementPoints);
                statement.setString(2, playerUUID);
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while updating the playerData table...\n%s", ex.getMessage()));
        }
    }

    @Override
    public @Nullable PlayerData GetPlayerData(String playerUUID) {
        PlayerData data = null;
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_playerData WHERE PlayerId=? LIMIT 1;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerUUID);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        data = new PlayerData(
                                UUID.fromString(result.getString("PlayerId")),
                                result.getLong("AchievementPoints"),
                                result.getInt("CompletedDailyObjectives"),
                                result.getInt("CompletedWeeklyObjectives")
                        );
                    }
                }
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while finding playerData...\n%s", ex.getMessage()));
            return null;
        }

        return data;
    }
    //#endregion

    //#region Daily Objectives
    @Override
    public void AddPlayerDailyObjective(String playerUUID, String objectiveId) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("INSERT INTO %s_daily_obj (PlayerId, ObjectiveId, IsCompleted) " +
                            "VALUES (?, ?, ?);",
                    getConfig().getString("storage.tablePrefix"));

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameters for the prepared statement
                statement.setString(1, playerUUID);
                statement.setString(2, objectiveId);
                statement.setBoolean(3,false);

                // Execute the query
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while adding dailyObjective...\n%s", ex.getMessage()));
        }
    }

    @Override
    public void UpdatePlayerDailyObjective(String playerUUID, String objectiveId, boolean isCompleted) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("UPDATE %s_daily_obj SET IsCompleted=? WHERE PlayerId=? AND ObjectiveId=?;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isCompleted);
                statement.setString(2, playerUUID);
                statement.setString(3, objectiveId);
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while updating the dailyObjectiveData table...\n%s", ex.getMessage()));
        }
    }

    @Override
    public boolean HasPlayerCompletedDailyObjective(String playerUUID, String objectiveId) {
        boolean data = false;
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_daily_obj WHERE PlayerId=? AND ObjectiveId=? LIMIT 1;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerUUID);
                statement.setString(2, objectiveId);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        data = result.getBoolean("IsCompleted");
                    }
                }
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while finding dailyObjectiveData...\n%s", ex.getMessage()));
            return false;
        }

        return data;
    }

    @Override
    public void WipePlayerDailyObjectives(String playerUUID) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("DELETE FROM %s_daily_obj WHERE PlayerId=?;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerUUID);
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while wiping dailyObjectiveData...\n%s", ex.getMessage()));
        }
    }

    @Override
    public List<DailyObjectiveData> GetPlayerDailyObjectives(String playerUUID) {
        List<DailyObjectiveData> data = new ArrayList<>();
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_daily_obj WHERE PlayerId=?;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerUUID);
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        data.add(new DailyObjectiveData(
                                UUID.fromString(result.getString("PlayerId")),
                                result.getString("ObjectiveId"),
                                result.getBoolean("IsCompleted")
                        ));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while getting dailyObjectiveData...\n%s", ex.getMessage()));
            return null;
        }

        return data;
    }
    //#endregion

    //#region Weekly Objectives
    @Override
    public void AddPlayerWeeklyObjective(String playerUUID, String objectiveId) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("INSERT INTO %s_weekly_obj (PlayerId, ObjectiveId, IsCompleted) " +
                            "VALUES (?, ?, ?);",
                    getConfig().getString("storage.tablePrefix"));

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameters for the prepared statement
                statement.setString(1, playerUUID);
                statement.setString(2, objectiveId);
                statement.setBoolean(3,false);

                // Execute the query
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while adding weeklyObjective...\n%s", ex.getMessage()));
        }
    }

    @Override
    public void UpdatePlayerWeeklyObjective(String playerUUID, String objectiveId, boolean isCompleted) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("UPDATE %s_weekly_obj SET IsCompleted=? WHERE PlayerId=? AND ObjectiveId=?;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isCompleted);
                statement.setString(2, playerUUID);
                statement.setString(3, objectiveId);
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while updating the weeklyObjectiveData table...\n%s", ex.getMessage()));
        }
    }

    @Override
    public boolean HasPlayerCompletedWeeklyObjective(String playerUUID, String objectiveId) {
        boolean data = false;
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_weekly_obj WHERE PlayerId=? AND ObjectiveId=? LIMIT 1;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerUUID);
                statement.setString(2, objectiveId);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        data = result.getBoolean("IsCompleted");
                    }
                }
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while finding weeklyObjectiveData...\n%s", ex.getMessage()));
            return false;
        }

        return data;
    }

    @Override
    public void WipePlayerWeeklyObjectives(String playerUUID) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("DELETE FROM %s_weekly_obj WHERE PlayerId=?;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerUUID);
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while wiping weeklyObjectiveData...\n%s", ex.getMessage()));
        }
    }

    @Override
    public List<WeeklyObjectiveData> GetPlayerWeeklyObjectives(String playerUUID) {
        List<WeeklyObjectiveData> data = new ArrayList<>();
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_weekly_obj WHERE PlayerId=?;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerUUID);
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        data.add(new WeeklyObjectiveData(
                                UUID.fromString(result.getString("PlayerId")),
                                result.getString("ObjectiveId"),
                                result.getBoolean("IsCompleted")
                        ));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while getting weeklyObjectiveData...\n%s", ex.getMessage()));
            return null;
        }

        return data;
    }
    //#endregion

    //#region Completed Achievements
    @Override
    public void AddCompletedAchievement(String playerUUID, String achievementId) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("INSERT INTO %s_comp_achievements (PlayerId, AchievementId) " +
                            "VALUES (?, ?);",
                    getConfig().getString("storage.tablePrefix"));

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameters for the prepared statement
                statement.setString(1, playerUUID);
                statement.setString(2, achievementId);

                // Execute the query
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while adding completedAchievement...\n%s", ex.getMessage()));
        }
    }

    @Override
    public boolean HasPlayerCompletedAchievement(String playerUUID, String achievementId) {
        boolean data = false;
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_comp_achievements WHERE PlayerId=? AND AchievementId=? LIMIT 1;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerUUID);
                statement.setString(2, achievementId);
                try (ResultSet result = statement.executeQuery()) {
                    if (result == null)
                        return false;
                    if (result.next()) {
                        data = true;
                    }
                }
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while finding completedAchievementData...\n%s", ex.getMessage()));
            return false;
        }
        return data;
    }

    @Override
    public List<CompletedAchievementData> GetPlayerCompletedAchievements(String playerUUID) {
        List<CompletedAchievementData> data = new ArrayList<>();
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_comp_achievements WHERE PlayerId=?;",
                    getConfig().getString("storage.tablePrefix"));
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerUUID);
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        data.add(new CompletedAchievementData(
                                UUID.fromString(result.getString("PlayerId")),
                                result.getString("AchievementId")
                        ));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while getting completedAchievementData...\n%s", ex.getMessage()));
            return null;
        }
        return data;
    }

    //#endregion
}
