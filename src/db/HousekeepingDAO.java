// src/db/HousekeepingDAO.java
package db;

import models.HousekeepingTask;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HousekeepingDAO {
    private Connection connection;

    public HousekeepingDAO(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void addTask(HousekeepingTask task) throws SQLException {
        String sql = "INSERT INTO housekeeping (room_id, status, is_deep_clean, is_maintenance) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, task.getRoomId());
            stmt.setString(2, task.getStatus());
            stmt.setBoolean(3, task.isDeepClean());
            stmt.setBoolean(4, task.isMaintenance());
            stmt.executeUpdate();
        }
    }

    public List<HousekeepingTask> getAllTasks() throws SQLException {
        List<HousekeepingTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM housekeeping";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tasks.add(new HousekeepingTask(
                        rs.getInt("id"),
                        rs.getInt("room_id"),
                        rs.getString("status"),
                        rs.getBoolean("is_deep_clean"),
                        rs.getBoolean("is_maintenance")
                ));
            }
        }
        return tasks;
    }

    public void updateTask(HousekeepingTask task) throws SQLException {
        String sql = "UPDATE housekeeping SET status = ?, is_deep_clean = ?, is_maintenance = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, task.getStatus());
            stmt.setBoolean(2, task.isDeepClean());
            stmt.setBoolean(3, task.isMaintenance());
            stmt.setInt(4, task.getTaskId());
            stmt.executeUpdate();
        }
    }

    public void deleteTask(int taskId) throws SQLException {
        String sql = "DELETE FROM housekeeping WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, taskId);
            stmt.executeUpdate();
        }
    }
}