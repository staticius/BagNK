package dev.astatic.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DatabaseManager {

    private static HikariDataSource dataSource;

    public static void connect(String dbPath) {
        try {
            if (!dbPath.startsWith("jdbc:sqlite:")) {
                dbPath = "jdbc:sqlite:" + dbPath;
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbPath);
            config.setMaximumPoolSize(16);
            config.setMinimumIdle(12);
            config.setIdleTimeout(60000);
            config.setMaxLifetime(1200000);
            config.setConnectionTimeout(5000);
            config.setLeakDetectionThreshold(30000);
            config.setPoolName("Bag-Pool");
            config.setValidationTimeout(5000);

            dataSource = new HikariDataSource(config);
            createTables();
        } catch (Exception e) {
            throw new RuntimeException("Veritabanı bağlantısında hata: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("Veritabanı bağlantısı yok");
        }
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    private static void createTables() {
        executeUpdate("""
            CREATE TABLE IF NOT EXISTS Players (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE NOT NULL,
                bag_amount INTEGER DEFAULT 0 CHECK(bag_amount <= 2)
            );
        """);

        executeUpdate("""
            CREATE TABLE IF NOT EXISTS Bags (
                player_id INTEGER NOT NULL,
                bag_id INTEGER NOT NULL CHECK(bag_id IN (1, 2)),
                bag_contents TEXT,
                PRIMARY KEY (player_id, bag_id),
                FOREIGN KEY (player_id) REFERENCES Players(id) ON DELETE CASCADE
            );
        """);
    }

    public static void addPlayer(String name) {
        executeUpdate("INSERT INTO Players (name) VALUES (?)", name);
        int playerID = getPlayerID(name);
        if (playerID != -1) {
            addBag(playerID, "");
        }
    }

    public static int getPlayerID(String name) {
        final int[] playerID = {-1};
        executeQuery("SELECT id FROM Players WHERE name = ?", rs -> {
            try {
                if (rs.next()) {
                    playerID[0] = rs.getInt("id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, name);
        return playerID[0];
    }

    public static List<Integer> getBags(int playerID) {
        List<Integer> bagList = new ArrayList<>();
        executeQuery("SELECT bag_id FROM Bags WHERE player_id = ? ORDER BY bag_id", rs -> {
            try {
                while (rs.next()) {
                    bagList.add(rs.getInt("bag_id"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, playerID);
        return bagList;
    }

    public static boolean addBag(int playerID, String content) {
        int bagCount = getBagCount(playerID);

        if (bagCount >= 2) {
            return false;
        }

        int bagID = bagCount + 1;
        executeUpdate("INSERT INTO Bags (player_id, bag_id, bag_contents) VALUES (?, ?, ?)", playerID, bagID, content);
        executeUpdate("UPDATE Players SET bag_amount = ? WHERE id = ?", bagCount + 1, playerID);
        return true;
    }

    public static int getBagCount(int playerID) {
        final int[] count = {0};
        executeQuery("SELECT COUNT(*) AS count FROM Bags WHERE player_id = ?", rs -> {
            try {
                if (rs.next()) {
                    count[0] = rs.getInt("count");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, playerID);
        return count[0];
    }

    public static String getBagContent(int playerID, int bagID) {
        final String[] content = {""};
        executeQuery("SELECT bag_contents FROM Bags WHERE player_id = ? AND bag_id = ?", rs -> {
            try {
                if (rs.next()) {
                    content[0] = rs.getString("bag_contents");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, playerID, bagID);
        return content[0];
    }

    public static List<String> getPlayersWithBags() {
        List<String> players = new ArrayList<>();
        executeQuery("SELECT name FROM Players WHERE bag_amount > 0", rs -> {
            try {
                while (rs.next()) {
                    players.add(rs.getString("name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return players;
    }

    public static void saveBagContent(int playerID, int bagID, String content) {
        executeUpdate("UPDATE Bags SET bag_contents = ? WHERE player_id = ? AND bag_id = ?", content, playerID, bagID);
    }

    public static void deletePlayer(int playerID) {
        executeUpdate("DELETE FROM Players WHERE id = ?", playerID);
    }

    public static void deleteBag(int playerID, int bagID) {
        executeUpdate("DELETE FROM Bags WHERE player_id = ? AND bag_id = ?", playerID, bagID);
    }

    public static void executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("SQL güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void executeQuery(String sql, Consumer<ResultSet> consumer, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                consumer.accept(rs);
            }
        } catch (Exception e) {
            System.err.println("SQL sorgu hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}