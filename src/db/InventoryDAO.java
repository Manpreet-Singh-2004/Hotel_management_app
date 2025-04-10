package db;

import models.InventoryItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {
    private final Connection conn;

    public InventoryDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    public boolean addItem(InventoryItem item) {
        String sql = "INSERT INTO Inventory (item_name, category, quantity, threshold) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getCategory());
            stmt.setInt(3, item.getQuantity());
            stmt.setInt(4, item.getThreshold());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error inserting inventory item");
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateItem(InventoryItem item) {
        String sql = "UPDATE Inventory SET item_name=?, category=?, quantity=?, threshold=? WHERE item_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getCategory());
            stmt.setInt(3, item.getQuantity());
            stmt.setInt(4, item.getThreshold());
            stmt.setInt(5, item.getItemId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error updating inventory item");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM Inventory WHERE item_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error deleting inventory item");
            e.printStackTrace();
            return false;
        }
    }

    public List<InventoryItem> getAllItems() {
        List<InventoryItem> list = new ArrayList<>();
        String sql = "SELECT * FROM Inventory";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                InventoryItem item = new InventoryItem();
                item.setItemId(rs.getInt("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setCategory(rs.getString("category"));
                item.setQuantity(rs.getInt("quantity"));
                item.setThreshold(rs.getInt("threshold"));
                list.add(item);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching inventory items");
            e.printStackTrace();
        }
        return list;
    }
}
