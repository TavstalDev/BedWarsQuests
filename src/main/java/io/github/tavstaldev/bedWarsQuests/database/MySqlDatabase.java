package io.github.tavstaldev.bedWarsQuests.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.tavstaldev.bedWarsQuests.BWQConfiguration;
import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.database.CompletedAchievementData;
import io.github.tavstaldev.bedWarsQuests.models.database.DailyObjectiveData;
import io.github.tavstaldev.bedWarsQuests.models.database.PlayerData;
import io.github.tavstaldev.bedWarsQuests.models.database.WeeklyObjectiveData;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySqlDatabase implements IDatabase {
    private HikariDataSource _dataSource;
    private BWQConfiguration _config;
    private final PluginLogger _logger = BedWarsQuests.Logger().WithModule(MySqlDatabase.class);

    @Override
    public void load() {
        _config = BedWarsQuests.Config();
        _dataSource = CreateDataSource();
    }

    @Override
    public void unload() {
        if (_dataSource != null) {
            if (!_dataSource.isClosed())
                _dataSource.close();
        }
    }

    public HikariDataSource CreateDataSource() {
        try
        {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", _config.storageHost,_config.storagePort, _config.storageDatabase));
            config.setUsername(_config.storageUsername);
            config.setPassword(_config.storagePassword);
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
    public void checkSchema() {
        try (Connection connection = _dataSource.getConnection())
        {
            // PlayerData
            String sql = String.format("CREATE TABLE IF NOT EXISTS %s_playerData (" +
                    "PlayerId VARCHAR(36) PRIMARY KEY, " +
                    "AchievementPoints BIGINT, " +
                    "CompletedDailyObjectives INT(11), " +
                    "CompletedWeeklyObjectives INT(11));",
                    _config.storageTablePrefix
            );
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.executeUpdate();

            // Completed Achievements
            sql = String.format("CREATE TABLE IF NOT EXISTS %s_comp_achievements (" +
                            "PlayerId VARCHAR(36), " +
                            "AchievementId VARCHAR(64));",
                    _config.storageTablePrefix
            );
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();

            // Daily Objectives
            sql = String.format("CREATE TABLE IF NOT EXISTS %s_daily_obj (" +
                            "PlayerId VARCHAR(36), " +
                            "ObjectiveId VARCHAR(64), " +
                            "IsCompleted BOOLEAN);",
                    _config.storageTablePrefix
            );
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();

            // Weekly Objectives
            sql = String.format("CREATE TABLE IF NOT EXISTS %s_weekly_obj (" +
                            "PlayerId VARCHAR(36), " +
                            "ObjectiveId VARCHAR(64), " +
                            "IsCompleted BOOLEAN);",
                    _config.storageTablePrefix
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
    public void addPlayerData(UUID playerId) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("INSERT INTO %s_playerData (PlayerId, AchievementPoints, CompletedDailyObjectives, CompletedWeeklyObjectives) " +
                            "VALUES (?, ?, ?, ?);",
                    _config.storageTablePrefix);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameters for the prepared statement
                statement.setString(1, playerId.toString());
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
    public void updatePlayerData(UUID playerId, long achievementPoints, int completedDailyObjectives, int completedWeeklyObjectives) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("UPDATE %s_playerData SET AchievementPoints=?, CompletedDailyObjectives=?, CompletedWeeklyObjectives=? WHERE PlayerId=?;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, achievementPoints);
                statement.setInt(2, completedDailyObjectives);
                statement.setInt(3, completedWeeklyObjectives);
                statement.setString(4, playerId.toString());
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while updating the playerData table...\n%s", ex.getMessage()));
        }
    }

    @Override
    public void wipePlayerData() {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("TRUNCATE %s_playerData;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while wiping playerData...\n%s", ex.getMessage()));
        }
    }

    @Override
    public void increaseAchievementPoints(UUID playerId, long points) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("UPDATE %s_playerData SET AchievementPoints=AchievementPoints+? WHERE PlayerId=?;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, points);
                statement.setString(2, playerId.toString());
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while increasing achievement points in playerData table...\n%s", ex.getMessage()));
        }
    }

    @Override
    public void increaseCompletedDailyObjectives(UUID playerId) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("UPDATE %s_playerData SET CompletedDailyObjectives=CompletedDailyObjectives+1 WHERE PlayerId=?;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while increasing completed daily objectives in playerData table...\n%s", ex.getMessage()));
        }
    }

    @Override
    public void increaseCompletedWeeklyObjectives(UUID playerId) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("UPDATE %s_playerData SET CompletedWeeklyObjectives=CompletedWeeklyObjectives+1 WHERE PlayerId=?;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while increasing completed weekly objectives in playerData table...\n%s", ex.getMessage()));
        }
    }

    @Override
    public @Nullable PlayerData getPlayerData(UUID playerId) {
        PlayerData data = null;
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_playerData WHERE PlayerId=? LIMIT 1;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
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
    public void addPlayerDailyObjective(UUID playerId, String objectiveId) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("INSERT INTO %s_daily_obj (PlayerId, ObjectiveId, IsCompleted) " +
                            "VALUES (?, ?, ?);",
                    _config.storageTablePrefix);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameters for the prepared statement
                statement.setString(1, playerId.toString());
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
    public void updatePlayerDailyObjective(UUID playerId, String objectiveId, boolean isCompleted) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("UPDATE %s_daily_obj SET IsCompleted=? WHERE PlayerId=? AND ObjectiveId=?;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isCompleted);
                statement.setString(2, playerId.toString());
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
    public boolean hasPlayerCompletedDailyObjective(UUID playerId, String objectiveId) {
        boolean data = false;
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_daily_obj WHERE PlayerId=? AND ObjectiveId=? LIMIT 1;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
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
    public void wipePlayerDailyObjectives() {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("TRUNCATE %s_daily_obj;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while wiping dailyObjectiveData...\n%s", ex.getMessage()));
        }
    }

    @Override
    public List<DailyObjectiveData> getPlayerDailyObjectives(UUID playerId) {
        List<DailyObjectiveData> data = new ArrayList<>();
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_daily_obj WHERE PlayerId=?;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
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
    public void addPlayerWeeklyObjective(UUID playerId, String objectiveId) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("INSERT INTO %s_weekly_obj (PlayerId, ObjectiveId, IsCompleted) " +
                            "VALUES (?, ?, ?);",
                    _config.storageTablePrefix);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameters for the prepared statement
                statement.setString(1, playerId.toString());
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
    public void updatePlayerWeeklyObjective(UUID playerId, String objectiveId, boolean isCompleted) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("UPDATE %s_weekly_obj SET IsCompleted=? WHERE PlayerId=? AND ObjectiveId=?;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBoolean(1, isCompleted);
                statement.setString(2, playerId.toString());
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
    public boolean hasPlayerCompletedWeeklyObjective(UUID playerId, String objectiveId) {
        boolean data = false;
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_weekly_obj WHERE PlayerId=? AND ObjectiveId=? LIMIT 1;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
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
    public void wipePlayerWeeklyObjectives() {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("TRUNCATE %s_weekly_obj;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while wiping weeklyObjectiveData...\n%s", ex.getMessage()));
        }
    }

    @Override
    public List<WeeklyObjectiveData> getPlayerWeeklyObjectives(UUID playerId) {
        List<WeeklyObjectiveData> data = new ArrayList<>();
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_weekly_obj WHERE PlayerId=?;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
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
    public void addCompletedAchievement(UUID playerId, String achievementId) {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("INSERT INTO %s_comp_achievements (PlayerId, AchievementId) " +
                            "VALUES (?, ?);",
                    _config.storageTablePrefix);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set parameters for the prepared statement
                statement.setString(1, playerId.toString());
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
    public void wipeCompletedAchievements() {
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("TRUNCATE %s_comp_achievements;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            _logger.Error(String.format("Unknown error happened while wiping completedAchievementData...\n%s", ex.getMessage()));
        }
    }

    @Override
    public boolean hasPlayerCompletedAchievement(UUID playerId, String achievementId) {
        boolean data = false;
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_comp_achievements WHERE PlayerId=? AND AchievementId=? LIMIT 1;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
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
    public List<CompletedAchievementData> getPlayerCompletedAchievements(UUID playerId) {
        List<CompletedAchievementData> data = new ArrayList<>();
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_comp_achievements WHERE PlayerId=?;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
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
