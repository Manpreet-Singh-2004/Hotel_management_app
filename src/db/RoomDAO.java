package db;

import models.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private final Connection conn;

    public RoomDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    public boolean addRoom(Room r) {
        String sql = "INSERT INTO Rooms (room_number, type, price, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, r.getRoomNumber());
            stmt.setString(2, r.getType());
            stmt.setDouble(3, r.getPrice());
            stmt.setString(4, r.getStatus());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error inserting room");
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateRoom(Room r) {
        String sql = "UPDATE Rooms SET room_number = ?, type = ?, price = ?, status = ? WHERE room_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, r.getRoomNumber());
            stmt.setString(2, r.getType());
            stmt.setDouble(3, r.getPrice());
            stmt.setString(4, r.getStatus());
            stmt.setInt(5, r.getRoomId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error updating room");
            e.printStackTrace();
            return false;
        }
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Rooms";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setType(rs.getString("type"));
                r.setPrice(rs.getDouble("price"));
                r.setStatus(rs.getString("status"));
                rooms.add(r);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching rooms");
            e.printStackTrace();
        }
        return rooms;
    }

    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM Rooms WHERE room_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setType(rs.getString("type"));
                room.setPrice(rs.getDouble("price"));
                room.setStatus(rs.getString("status"));
                return room;
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching room by ID");
            e.printStackTrace();
        }
        return null;
    }
    public Room getRoomByNumber(String number) {
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Rooms WHERE room_number = ?");
            stmt.setString(1, number);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Room(rs.getInt("room_id"), rs.getString("room_number"),
                        rs.getString("type"), rs.getDouble("price"), rs.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
