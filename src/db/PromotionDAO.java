package db;

import models.Promotion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {
    private final Connection conn;

    public PromotionDAO() {
        conn = DBConnection.getInstance().getConnection();
    }

    public boolean addPromotion(Promotion p) {
        String sql = "INSERT INTO Promotions (code, discount_type, discount_value, reservation_id, start_date, end_date, is_global) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getCode());
            stmt.setString(2, p.getDiscountType());
            stmt.setDouble(3, p.getDiscountValue());

            if (p.getReservationId() > 0) {
                stmt.setInt(4, p.getReservationId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setString(5, p.getStartDate());
            stmt.setString(6, p.getEndDate());
            stmt.setInt(7, p.isGlobal() ? 1 : 0);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("❌ Error adding promotion");
            e.printStackTrace();
            return false;
        }
    }

    public List<Promotion> getAllPromotions() {
        List<Promotion> list = new ArrayList<>();
        String sql = "SELECT * FROM Promotions";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Promotion p = extractPromotion(rs);
                list.add(p);
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching promotions");
            e.printStackTrace();
        }
        return list;
    }

    public Promotion getValidPromotionForReservation(int reservationId) {
        String reservationPromoSQL = "SELECT * FROM Promotions WHERE reservation_id = ?";
        String globalPromoSQL = "SELECT * FROM Promotions WHERE is_global = 1 " +
                "AND (start_date IS NULL OR start_date <= date('now')) " +
                "AND (end_date IS NULL OR end_date >= date('now')) LIMIT 1";

        try (PreparedStatement stmt = conn.prepareStatement(reservationPromoSQL)) {
            stmt.setInt(1, reservationId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractPromotion(rs);
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("❌ Error finding reservation-specific promotion");
            e.printStackTrace();
        }

        try (PreparedStatement stmt = conn.prepareStatement(globalPromoSQL)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractPromotion(rs);
            }
        } catch (Exception e) {
            System.out.println("❌ Error finding global promotion");
            e.printStackTrace();
        }

        return null;
    }


    private Promotion extractPromotion(ResultSet rs) throws Exception {
        Promotion p = new Promotion();
        p.setId(rs.getInt("promo_id"));
        p.setCode(rs.getString("code"));
        p.setDiscountType(rs.getString("discount_type"));
        p.setDiscountValue(rs.getDouble("discount_value"));
        p.setReservationId(rs.getInt("reservation_id"));
        p.setStartDate(rs.getString("start_date"));
        p.setEndDate(rs.getString("end_date"));
        p.setGlobal(rs.getInt("is_global") == 1);
        return p;
    }

}
