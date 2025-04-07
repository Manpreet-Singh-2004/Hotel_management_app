package db;

import models.Guest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {
    private final Connection conn;

    public GuestDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    public boolean addGuest(Guest g) {
        String sql = "INSERT INTO Guests (full_name, email, phone, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, g.getFullName());
            stmt.setString(2, g.getEmail());
            stmt.setString(3, g.getPhone());
            stmt.setString(4, g.getAddress());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error inserting guest");
            e.printStackTrace();
            return false;
        }
    }

    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM Guests";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Guest g = new Guest();
                g.setGuestId(rs.getInt("guest_id"));
                g.setFullName(rs.getString("full_name"));
                g.setEmail(rs.getString("email"));
                g.setPhone(rs.getString("phone"));
                g.setAddress(rs.getString("address"));
                guests.add(g);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching guests");
            e.printStackTrace();
        }
        return guests;
    }

    public Guest getGuestById(int id) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Guests WHERE guest_id = ?");
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Guest(rs.getInt("guest_id"), rs.getString("full_name"),
                        rs.getString("email"), rs.getString("phone"), rs.getString("address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Guest getGuestByName(String name) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Guests WHERE full_name = ?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Guest(rs.getInt("guest_id"), rs.getString("full_name"),
                        rs.getString("email"), rs.getString("phone"), rs.getString("address"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
