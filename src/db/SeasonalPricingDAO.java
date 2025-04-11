// src/db/SeasonalPricingDAO.java
package db;

import models.SeasonalPricing;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeasonalPricingDAO {
    private final Connection conn;

    public SeasonalPricingDAO(Connection conn) {
        this.conn = conn;
    }

    public void addSeasonalPricing(SeasonalPricing sp) throws SQLException {
        String sql = "INSERT INTO SeasonalPricing (start_date, end_date, price_multiplier, description) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sp.getStartDate());
            stmt.setString(2, sp.getEndDate());
            stmt.setDouble(3, sp.getPriceMultiplier());
            stmt.setString(4, sp.getDescription());
            stmt.executeUpdate();
        }
    }

    public List<SeasonalPricing> getAll() throws SQLException {
        List<SeasonalPricing> list = new ArrayList<>();
        String sql = "SELECT * FROM SeasonalPricing";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new SeasonalPricing(
                        rs.getInt("id"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getDouble("price_multiplier"),
                        rs.getString("description")
                ));
            }
        }
        return list;
    }

    public void updateSeasonalPricing(SeasonalPricing sp) throws SQLException {
        String sql = "UPDATE SeasonalPricing SET start_date=?, end_date=?, price_multiplier=?, description=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sp.getStartDate());
            stmt.setString(2, sp.getEndDate());
            stmt.setDouble(3, sp.getPriceMultiplier());
            stmt.setString(4, sp.getDescription());
            stmt.setInt(5, sp.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteSeasonalPricing(int id) throws SQLException {
        String sql = "DELETE FROM SeasonalPricing WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
