package db;

import models.GroupReservation;
import models.GroupReservationRoom;
import models.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupReservationDAO {
    private final Connection conn;

    public GroupReservationDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    // Create a new group reservation
    public int addGroupReservation(GroupReservation group) {
        String sql = "INSERT INTO GroupReservations (group_name, contact_name, contact_email, contact_phone, shared_billing) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, group.getGroupName());
            stmt.setString(2, group.getContactName());
            stmt.setString(3, group.getContactEmail());
            stmt.setString(4, group.getContactPhone());
            stmt.setBoolean(5, group.isSharedBilling());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        } catch (SQLException e) {
            System.out.println("❌ Error creating group reservation");
            e.printStackTrace();
            return -1;
        }
    }

    // Add a room to a group reservation
    public boolean addRoomToGroup(GroupReservationRoom room) {
        String sql = "INSERT INTO GroupReservationRooms (group_id, room_id, guest_id, check_in, check_out) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, room.getGroupId());
            stmt.setInt(2, room.getRoomId());
            stmt.setInt(3, room.getGuestId());
            stmt.setString(4, room.getCheckIn());
            stmt.setString(5, room.getCheckOut());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error adding room to group");
            e.printStackTrace();
            return false;
        }
    }

    // Get all group reservations
    public List<GroupReservation> getAllGroupReservations() {
        List<GroupReservation> groups = new ArrayList<>();
        String sql = "SELECT * FROM GroupReservations";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                GroupReservation group = new GroupReservation(
                    rs.getInt("group_id"),
                    rs.getString("group_name"),
                    rs.getString("contact_name"),
                    rs.getString("contact_email"),
                    rs.getString("contact_phone"),
                    rs.getBoolean("shared_billing")
                );
                groups.add(group);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching group reservations");
            e.printStackTrace();
        }
        return groups;
    }

    // Get a specific group reservation
    public GroupReservation getGroupReservationById(int groupId) {
        String sql = "SELECT * FROM GroupReservations WHERE group_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new GroupReservation(
                    rs.getInt("group_id"),
                    rs.getString("group_name"),
                    rs.getString("contact_name"),
                    rs.getString("contact_email"),
                    rs.getString("contact_phone"),
                    rs.getBoolean("shared_billing")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching group reservation");
            e.printStackTrace();
        }
        return null;
    }

    // Get all rooms for a group reservation
    public List<GroupReservationRoom> getRoomsForGroup(int groupId) {
        List<GroupReservationRoom> rooms = new ArrayList<>();
        String sql = "SELECT * FROM GroupReservationRooms WHERE group_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                GroupReservationRoom room = new GroupReservationRoom(
                    rs.getInt("id"),
                    rs.getInt("group_id"),
                    rs.getInt("room_id"),
                    rs.getInt("guest_id"),
                    rs.getString("check_in"),
                    rs.getString("check_out")
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching rooms for group");
            e.printStackTrace();
        }
        return rooms;
    }

    // Update a group reservation
    public boolean updateGroupReservation(GroupReservation group) {
        String sql = "UPDATE GroupReservations SET group_name = ?, contact_name = ?, " +
                     "contact_email = ?, contact_phone = ?, shared_billing = ? WHERE group_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, group.getGroupName());
            stmt.setString(2, group.getContactName());
            stmt.setString(3, group.getContactEmail());
            stmt.setString(4, group.getContactPhone());
            stmt.setBoolean(5, group.isSharedBilling());
            stmt.setInt(6, group.getGroupId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error updating group reservation");
            e.printStackTrace();
            return false;
        }
    }

    // Delete a group reservation
    public boolean deleteGroupReservation(int groupId) {
        // First delete all rooms associated with this group
        String deleteRoomsSql = "DELETE FROM GroupReservationRooms WHERE group_id = ?";
        String deleteGroupSql = "DELETE FROM GroupReservations WHERE group_id = ?";
        
        try {
            conn.setAutoCommit(false);
            
            try (PreparedStatement deleteRoomsStmt = conn.prepareStatement(deleteRoomsSql)) {
                deleteRoomsStmt.setInt(1, groupId);
                deleteRoomsStmt.executeUpdate();
            }
            
            try (PreparedStatement deleteGroupStmt = conn.prepareStatement(deleteGroupSql)) {
                deleteGroupStmt.setInt(1, groupId);
                boolean result = deleteGroupStmt.executeUpdate() > 0;
                
                conn.commit();
                return result;
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("❌ Error deleting group reservation");
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Remove a room from a group
    public boolean removeRoomFromGroup(int roomEntryId) {
        String sql = "DELETE FROM GroupReservationRooms WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomEntryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error removing room from group");
            e.printStackTrace();
            return false;
        }
    }

    // Calculate total cost for a group
    public double calculateGroupTotal(int groupId) {
        double total = 0.0;
        List<GroupReservationRoom> rooms = getRoomsForGroup(groupId);
        RoomDAO roomDAO = new RoomDAO();
        
        for (GroupReservationRoom groupRoom : rooms) {
            Room room = roomDAO.getRoomById(groupRoom.getRoomId());
            if (room != null) {
                // Calculate days between check-in and check-out
                java.time.LocalDate checkIn = java.time.LocalDate.parse(groupRoom.getCheckIn());
                java.time.LocalDate checkOut = java.time.LocalDate.parse(groupRoom.getCheckOut());
                long days = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
                
                // Add room cost to total
                total += room.getPrice() * days;
            }
        }
        
        return total;
    }
}