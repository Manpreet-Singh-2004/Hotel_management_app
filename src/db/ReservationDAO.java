package db;

import models.Reservation;
import models.HousekeepingTask;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private final Connection conn;

    public ReservationDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    public boolean addReservation(Reservation r) {
        String sql = "INSERT INTO Reservations (guest_id, room_id, check_in, check_out, payment_status, special_requests) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, r.getGuestId());
            stmt.setInt(2, r.getRoomId());
            stmt.setString(3, r.getCheckIn());
            stmt.setString(4, r.getCheckOut());
            stmt.setString(5, r.getPaymentStatus());
            stmt.setString(6, r.getSpecialRequests());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Error inserting reservation");
            e.printStackTrace();
            return false;
        }
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM Reservations";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Reservation r = new Reservation();
                r.setReservationId(rs.getInt("reservation_id"));
                r.setGuestId(rs.getInt("guest_id"));
                r.setRoomId(rs.getInt("room_id"));
                r.setCheckIn(rs.getString("check_in"));
                r.setCheckOut(rs.getString("check_out"));
                r.setPaymentStatus(rs.getString("payment_status"));
                r.setSpecialRequests(rs.getString("special_requests"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching reservations");
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateReservation(Reservation r) {
        String sql = "UPDATE Reservations SET guest_id=?, room_id=?, check_in=?, check_out=?, payment_status=?, special_requests=? WHERE reservation_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, r.getGuestId());
            stmt.setInt(2, r.getRoomId());
            stmt.setString(3, r.getCheckIn());
            stmt.setString(4, r.getCheckOut());
            stmt.setString(5, r.getPaymentStatus());
            stmt.setString(6, r.getSpecialRequests());
            stmt.setInt(7, r.getReservationId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error updating reservation");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReservation(int reservationId) {
        String fetchRoomSql = "SELECT room_id FROM Reservations WHERE reservation_id = ?";
        String deleteSql = "DELETE FROM Reservations WHERE reservation_id = ?";

        try (PreparedStatement fetchStmt = conn.prepareStatement(fetchRoomSql)) {
            fetchStmt.setInt(1, reservationId);
            ResultSet rs = fetchStmt.executeQuery();

            if (rs.next()) {
                int roomId = rs.getInt("room_id");

                // Add housekeeping task before deleting the reservation
                HousekeepingDAO housekeepingDAO = new HousekeepingDAO(conn);
                HousekeepingTask task = new HousekeepingTask(0, roomId, "Pending", false, false);
                housekeepingDAO.addTask(task);
            }

            // Now delete the reservation
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, reservationId);
                return deleteStmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error deleting reservation and triggering housekeeping");
            e.printStackTrace();
            return false;
        }
    }

    public List<Reservation> searchReservations(String keyword) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM Reservations WHERE CAST(reservation_id AS TEXT) LIKE ? OR CAST(guest_id AS TEXT) LIKE ? OR CAST(room_id AS TEXT) LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String query = "%" + keyword + "%";
            stmt.setString(1, query);
            stmt.setString(2, query);
            stmt.setString(3, query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Reservation r = new Reservation();
                r.setReservationId(rs.getInt("reservation_id"));
                r.setGuestId(rs.getInt("guest_id"));
                r.setRoomId(rs.getInt("room_id"));
                r.setCheckIn(rs.getString("check_in"));
                r.setCheckOut(rs.getString("check_out"));
                r.setPaymentStatus(rs.getString("payment_status"));
                r.setSpecialRequests(rs.getString("special_requests"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error searching reservations");
            e.printStackTrace();
        }
        return list;
    }
}
