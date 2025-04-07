package db;

import models.Invoice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {
    private final Connection conn;

    public InvoiceDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean addInvoice(Invoice invoice) {
        String sql = "INSERT INTO Invoices (reservation_id, guest_id, issue_date, total_amount) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoice.getReservationId());
            stmt.setInt(2, invoice.getGuestId());
            stmt.setString(3, invoice.getIssueDate());
            stmt.setDouble(4, invoice.getTotalAmount());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Failed to add invoice");
            e.printStackTrace();
            return false;
        }
    }

    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM Invoices ORDER BY invoice_id DESC";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Invoice inv = new Invoice(
                        rs.getInt("reservation_id"),
                        rs.getInt("guest_id"),
                        rs.getString("issue_date"),
                        rs.getDouble("total_amount")
                );
                inv.setInvoiceId(rs.getInt("invoice_id"));
                invoices.add(inv);
            }
        } catch (SQLException e) {
            System.out.println("❌ Failed to fetch invoices");
            e.printStackTrace();
        }

        return invoices;
    }
}
