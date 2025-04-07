package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReportDAO {
    private final Connection conn;

    public ReportDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public int getTotalGuests() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Guests")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getTotalRooms() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Rooms")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double getTotalIncome() {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(total_amount) FROM Invoices")) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
