package io.github.tavstaldev.bedWarsQuests.database;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.tavstaldev.bedWarsQuests.BWQConfiguration;
import io.github.tavstaldev.bedWarsQuests.BedWarsQuests;
import io.github.tavstaldev.bedWarsQuests.models.database.ObjectiveData;
import io.github.tavstaldev.bedWarsQuests.models.database.PlayerData;
import io.github.tavstaldev.minecorelib.core.PluginLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// TODO: Add documentation
public class MySqlDatabase implements IDatabase {
    private HikariDataSource _dataSource;
    private BWQConfiguration _config;
    private final PluginLogger _logger = BedWarsQuests.Logger().withModule(MySqlDatabase.class);
    //#region Caches
    // Note: cache durations should be reduced if there are multiple servers using the same database
    private final Cache<@NotNull UUID, PlayerData> _playerCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    private final Cache<@NotNull UUID, List<String>> _completedAchievementCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    private final Cache<@NotNull UUID, List<ObjectiveData>> _dailyObjectiveCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    private final Cache<@NotNull UUID, List<ObjectiveData>> _weeklyObjectiveCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    //#endregion

    @Override
    public void load() {
        _config = BedWarsQuests.Config();
        _dataSource = createDataSource();
    }

    @Override
    public void unload() {
        if (_dataSource != null) {
            if (!_dataSource.isClosed())
                _dataSource.close();
        }
    }

    public HikariDataSource createDataSource() {
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
            _logger.error(String.format("Unknown error happened during the creation of database connection...\n%s", ex.getMessage()));
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
            _logger.error(String.format("Unknown error happened while creating tables...\n%s", ex.getMessage()));
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

            _playerCache.put(playerId, new PlayerData(playerId, 0, 0, 0));
        }
        catch (Exception ex)
        {
            _logger.error(String.format("Unknown error happened while adding playerData...\n%s", ex.getMessage()));
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

            _playerCache.put(playerId, new PlayerData(playerId, achievementPoints, completedDailyObjectives, completedWeeklyObjectives));
        }
        catch (Exception ex)
        {
            _logger.error(String.format("Unknown error happened while updating the playerData table...\n%s", ex.getMessage()));
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

            _playerCache.invalidateAll();
        }
        catch (Exception ex)
        {
            _logger.error(String.format("Unknown error happened while wiping playerData...\n%s", ex.getMessage()));
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
            _logger.error(String.format("Unknown error happened while increasing achievement points in playerData table...\n%s", ex.getMessage()));
            return;
        }

        var playerData = _playerCache.getIfPresent(playerId);
        if (playerData != null) {
            playerData.AchievementPoints += points;
            _playerCache.put(playerId, playerData);
        }
        else {
            PlayerData newData = getPlayerData(playerId);
            if (newData != null) {
                _playerCache.put(playerId, newData);
            }
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
            _logger.error(String.format("Unknown error happened while increasing completed daily objectives in playerData table...\n%s", ex.getMessage()));
            return;
        }

        var playerData = _playerCache.getIfPresent(playerId);
        if (playerData != null) {
            playerData.CompletedDailyObjectives += 1;
            _playerCache.put(playerId, playerData);
        }
        else {
            PlayerData newData = getPlayerData(playerId);
            if (newData != null) {
                _playerCache.put(playerId, newData);
            }
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
            _logger.error(String.format("Unknown error happened while increasing completed weekly objectives in playerData table...\n%s", ex.getMessage()));
            return;
        }

        var playerData = _playerCache.getIfPresent(playerId);
        if (playerData != null) {
            playerData.CompletedWeeklyObjectives += 1;
            _playerCache.put(playerId, playerData);
        }
        else {
            PlayerData newData = getPlayerData(playerId);
            if (newData != null) {
                _playerCache.put(playerId, newData);
            }
        }
    }

    @Override
    public @Nullable PlayerData getPlayerData(UUID playerId) {
        var cachedData = _playerCache.getIfPresent(playerId);
        if (cachedData != null) {
            return cachedData;
        }

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
            _logger.error(String.format("Unknown error happened while finding playerData...\n%s", ex.getMessage()));
            return null;
        }

        if (data != null) {
            _playerCache.put(playerId, data);
        }
        return data;
    }
    //#endregion

    @Override
    public @Nullable Boolean isWeeklyObjective(UUID playerId, String objectiveId) {
        var weeklyObjectives = getPlayerWeeklyObjectives(playerId);
        for (var obj : weeklyObjectives) {
            if (obj.ObjectiveId.equals(objectiveId)) {
                return true;
            }
        }

        var dailyObjectives = getPlayerDailyObjectives(playerId);
        for (var obj : dailyObjectives) {
            if (obj.ObjectiveId.equals(objectiveId)) {
                return false;
            }
        }
        return null;
    }

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
            _logger.error(String.format("Unknown error happened while adding dailyObjective...\n%s", ex.getMessage()));
            return;
        }

        var objectives = _dailyObjectiveCache.getIfPresent(playerId);
        if (objectives != null) {
            objectives.add(new ObjectiveData(playerId, objectiveId, false));
            _dailyObjectiveCache.put(playerId, objectives);
        }
        else {
            List<ObjectiveData> newObjectives = getPlayerDailyObjectives(playerId);
            if (newObjectives == null) {
                newObjectives = new ArrayList<>();
            }
            newObjectives.add(new ObjectiveData(playerId, objectiveId, false));
            _dailyObjectiveCache.put(playerId, newObjectives);
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
            _logger.error(String.format("Unknown error happened while updating the dailyObjectiveData table...\n%s", ex.getMessage()));
            return;
        }

        var objectives = _dailyObjectiveCache.getIfPresent(playerId);
        if (objectives != null) {
            for (var obj : objectives) {
                if (obj.ObjectiveId.equals(objectiveId)) {
                    obj.IsCompleted = isCompleted;
                    break;
                }
            }
            _dailyObjectiveCache.put(playerId, objectives);
        }
        else {
            List<ObjectiveData> newObjectives = getPlayerDailyObjectives(playerId);
            if (newObjectives == null) {
                newObjectives = new ArrayList<>();
            }
            newObjectives.add(new ObjectiveData(playerId, objectiveId, isCompleted));
            _dailyObjectiveCache.put(playerId, newObjectives);
        }
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
            _dailyObjectiveCache.invalidateAll();
        }
        catch (Exception ex)
        {
            _logger.error(String.format("Unknown error happened while wiping dailyObjectiveData...\n%s", ex.getMessage()));
        }
    }

    @Override
    public List<ObjectiveData> getPlayerDailyObjectives(UUID playerId) {
        var dailyObjectives = _dailyObjectiveCache.getIfPresent(playerId);
        if (dailyObjectives != null) {
            return dailyObjectives;
        }

        List<ObjectiveData> data = new ArrayList<>();
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_daily_obj WHERE PlayerId=?;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        data.add(new ObjectiveData(
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
            _logger.error(String.format("Unknown error happened while getting dailyObjectiveData...\n%s", ex.getMessage()));
            return null;
        }

        _dailyObjectiveCache.put(playerId, data);
        return data;
    }

    @Override
    public boolean isDailyObjectiveCompleted(UUID playerId, String objectiveId) {
        var objectives = getPlayerDailyObjectives(playerId);
        for (var obj : objectives) {
            if (obj.ObjectiveId.equals(objectiveId)) {
                return obj.IsCompleted;
            }
        }

        return false;
    }

    @Override
    public boolean isDailyObjectiveExists(UUID playerId, String objectiveId) {
        var objectives = getPlayerDailyObjectives(playerId);
        for (var obj : objectives) {
            if (obj.ObjectiveId.equals(objectiveId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void generateDailyObjectives(UUID playerId) {
        final var currentDailyObjectives = getPlayerDailyObjectives(playerId);
        // Wipe existing daily objectives from cache
        _dailyObjectiveCache.invalidate(playerId);

        var objectives = BedWarsQuests.ObjectiveManager().getObjectives();
        if (!currentDailyObjectives.isEmpty()) {
            for (var weeklyObj : currentDailyObjectives) {
                objectives.removeIf(obj -> obj.Id.equals(weeklyObj.ObjectiveId));
            }
        }

        Collections.shuffle(objectives);
        int numToTake = Math.min(3, objectives.size());
        for (var item : objectives.subList(0, numToTake)) {
            addPlayerDailyObjective(playerId, item.Id);
        }
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
            _logger.error(String.format("Unknown error happened while adding weeklyObjective...\n%s", ex.getMessage()));
            return;
        }

        var objectives = _weeklyObjectiveCache.getIfPresent(playerId);
        if (objectives != null) {
            objectives.add(new ObjectiveData(playerId, objectiveId, false));
            _weeklyObjectiveCache.put(playerId, objectives);
        }
        else {
            List<ObjectiveData> newObjectives = getPlayerWeeklyObjectives(playerId);
            if (newObjectives == null) {
                newObjectives = new ArrayList<>();
            }
            newObjectives.add(new ObjectiveData(playerId, objectiveId, false));
            _weeklyObjectiveCache.put(playerId, newObjectives);
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
            _logger.error(String.format("Unknown error happened while updating the objectiveData table...\n%s", ex.getMessage()));
            return;
        }

        var objectives = _weeklyObjectiveCache.getIfPresent(playerId);
        if (objectives != null) {
            for (var obj : objectives) {
                if (obj.ObjectiveId.equals(objectiveId)) {
                    obj.IsCompleted = isCompleted;
                    break;
                }
            }
            _weeklyObjectiveCache.put(playerId, objectives);
        }
        else {
            List<ObjectiveData> newObjectives = getPlayerWeeklyObjectives(playerId);
            if (newObjectives == null) {
                newObjectives = new ArrayList<>();
            }
            newObjectives.add(new ObjectiveData(playerId, objectiveId, isCompleted));
            _weeklyObjectiveCache.put(playerId, newObjectives);
        }
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

            _weeklyObjectiveCache.invalidateAll();
        }
        catch (Exception ex)
        {
            _logger.error(String.format("Unknown error happened while wiping objectiveData...\n%s", ex.getMessage()));
        }
    }

    @Override
    public List<ObjectiveData> getPlayerWeeklyObjectives(UUID playerId) {
        var weeklyObjectives = _weeklyObjectiveCache.getIfPresent(playerId);
        if (weeklyObjectives != null) {
            return weeklyObjectives;
        }
        List<ObjectiveData> data = new ArrayList<>();
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_weekly_obj WHERE PlayerId=?;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        data.add(new ObjectiveData(
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
            _logger.error(String.format("Unknown error happened while getting objectiveData...\n%s", ex.getMessage()));
            return null;
        }

        _weeklyObjectiveCache.put(playerId, data);
        return data;
    }

    @Override
    public boolean isWeeklyObjectiveCompleted(UUID playerId, String objectiveId) {
        var objectives = getPlayerWeeklyObjectives(playerId);
        for (var obj : objectives) {
            if (obj.ObjectiveId.equals(objectiveId)) {
                return obj.IsCompleted;
            }
        }
        return false;
    }

    @Override
    public boolean isWeeklyObjectiveExists(UUID playerId, String objectiveId) {
        var objectives = getPlayerWeeklyObjectives(playerId);
        for (var obj : objectives) {
            if (obj.ObjectiveId.equals(objectiveId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void generateWeeklyObjectives(UUID playerId) {
        final var currentWeeklyObjectives = getPlayerWeeklyObjectives(playerId);
        // Wipe existing weekly objectives from cache
        _weeklyObjectiveCache.invalidate(playerId);

        var objectives = BedWarsQuests.ObjectiveManager().getObjectives();
        if (!currentWeeklyObjectives.isEmpty()) {
            for (var weeklyObj : currentWeeklyObjectives) {
                objectives.removeIf(obj -> obj.Id.equals(weeklyObj.ObjectiveId));
            }
        }

        Collections.shuffle(objectives);
        int numToTake = Math.min(3, objectives.size());
        for (var item : objectives.subList(0, numToTake)) {
            addPlayerWeeklyObjective(playerId, item.Id);
        }
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
            _logger.error(String.format("Unknown error happened while adding completedAchievement...\n%s", ex.getMessage()));
            return;
        }

        var completedAchievements = _completedAchievementCache.getIfPresent(playerId);
        if (completedAchievements != null) {
            completedAchievements.add(achievementId);
            _completedAchievementCache.put(playerId, completedAchievements);
        }
        else {
            List<String> newCompletedAchievements = getPlayerCompletedAchievements(playerId);
            if (newCompletedAchievements == null) {
                newCompletedAchievements = new ArrayList<>();
            }
            newCompletedAchievements.add(achievementId);
            _completedAchievementCache.put(playerId, newCompletedAchievements);
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
            _completedAchievementCache.invalidateAll();
        }
        catch (Exception ex)
        {
            _logger.error(String.format("Unknown error happened while wiping completedAchievementData...\n%s", ex.getMessage()));
        }
    }

    @Override
    public List<String> getPlayerCompletedAchievements(UUID playerId) {
        var completedAchievements = _completedAchievementCache.getIfPresent(playerId);
        if (completedAchievements != null) {
            return completedAchievements;
        }

        List<String> data = new ArrayList<>();
        try (Connection connection = _dataSource.getConnection())
        {
            String sql = String.format("SELECT * FROM %s_comp_achievements WHERE PlayerId=?;",
                    _config.storageTablePrefix);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, playerId.toString());
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        data.add(result.getString("AchievementId"));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            _logger.error(String.format("Unknown error happened while getting completedAchievementData...\n%s", ex.getMessage()));
            return null;
        }

        _completedAchievementCache.put(playerId, data);
        return data;
    }

    @Override
    public boolean isAchievementCompleted(UUID playerId, String achievementId) {
        var completedList = getPlayerCompletedAchievements(playerId);
        for (var achId : completedList) {
            if (achId.equals(achievementId)) {
                return true;
            }
        }
        return  false;
    }
    //#endregion
}
