package db;

import models.Invoice;
import models.GroupReservation;
import models.GroupReservationRoom;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GroupReservationBillingDAO {
    private final Connection conn;

    public GroupReservationBillingDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    // Generate a single invoice for an entire group (shared billing)
    public boolean generateGroupInvoice(int groupId, String issueDate, double totalAmount) {
        GroupReservationDAO groupDAO = new GroupReservationDAO();
        GroupReservation group = groupDAO.getGroupReservationById(groupId);
        
        if (group == null) {
            return false;
        }
        
        String sql = "INSERT INTO Invoices (reservation_id, guest_id, issue_date, total_amount, group_id) " +
                     "VALUES (NULL, -1, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, issueDate);
            stmt.setDouble(2, totalAmount);
            stmt.setInt(3, groupId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error generating group invoice");
            e.printStackTrace();
            return false;
        }
    }

    // Generate individual invoices for each room in a group
    public boolean generateIndividualInvoices(int groupId, String issueDate) {
        GroupReservationDAO groupDAO = new GroupReservationDAO();
        List<GroupReservationRoom> rooms = groupDAO.getRoomsForGroup(groupId);
        RoomDAO roomDAO = new RoomDAO();
        
        if (rooms.isEmpty()) {
            return false;
        }
        
        String sql = "INSERT INTO Invoices (reservation_id, guest_id, issue_date, total_amount, group_id, room_id) " +
                     "VALUES (NULL, ?, ?, ?, ?, ?)";
        
        try {
            conn.setAutoCommit(false);
            
            for (GroupReservationRoom roomEntry : rooms) {
                models.Room room = roomDAO.getRoomById(roomEntry.getRoomId());
                if (room == null) continue;
                
                // Calculate the stay duration and cost
                java.time.LocalDate checkIn = java.time.LocalDate.parse(roomEntry.getCheckIn());
                java.time.LocalDate checkOut = java.time.LocalDate.parse(roomEntry.getCheckOut());
                long days = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
                double roomCost = room.getPrice() * days;
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, roomEntry.getGuestId());
                    stmt.setString(2, issueDate);
                    stmt.setDouble(3, roomCost);
                    stmt.setInt(4, groupId);
                    stmt.setInt(5, roomEntry.getRoomId());
                    
                    stmt.executeUpdate();
                }
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("❌ Error generating individual invoices");
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

    // Get all invoices for a group
    public List<Invoice> getInvoicesForGroup(int groupId) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM Invoices WHERE group_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("invoice_id"));
                invoice.setReservationId(rs.getInt("reservation_id"));
                invoice.setGuestId(rs.getInt("guest_id"));
                invoice.setIssueDate(rs.getString("issue_date"));
                invoice.setTotalAmount(rs.getDouble("total_amount"));
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching group invoices");
            e.printStackTrace();
        }
        
        return invoices;
    }
}