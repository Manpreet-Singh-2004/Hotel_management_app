package db;

import models.Promotion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO {
    private final Connection conn;

    public PromotionDAO() {
        this.conn = DBConnection.getInstance().getConnection();
    }

    public boolean addPromotion(Promotion promo) {
        String sql = "INSERT INTO Promotions (code, discount_type, discount_value, reservation_id, start_date, end_date, is_global) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, promo.getCode());
            stmt.setString(2, promo.getDiscountType());
            stmt.setDouble(3, promo.getDiscountValue());

            if (promo.isGlobal()) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, promo.getReservationId());
            }

            stmt.setString(5, promo.getStartDate());
            stmt.setString(6, promo.getEndDate());
            stmt.setBoolean(7, promo.isGlobal());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error adding promotion");
            e.printStackTrace();
            return false;
        }
    }

    public List<Promotion> getAllPromotions() {
        List<Promotion> list = new ArrayList<>();
        String sql = "SELECT * FROM Promotions ORDER BY promo_id DESC";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Promotion p = new Promotion();
                p.setId(rs.getInt("promo_id"));
                p.setCode(rs.getString("code"));
                p.setDiscountType(rs.getString("discount_type"));
                p.setDiscountValue(rs.getDouble("discount_value"));
                p.setReservationId(rs.getInt("reservation_id"));
                p.setStartDate(rs.getString("start_date"));
                p.setEndDate(rs.getString("end_date"));
                p.setGlobal(rs.getBoolean("is_global"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean updatePromotion(Promotion promo) {
        String sql = "UPDATE Promotions SET code=?, discount_type=?, discount_value=?, reservation_id=?, start_date=?, end_date=?, is_global=? " +
                "WHERE promo_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, promo.getCode());
            stmt.setString(2, promo.getDiscountType());
            stmt.setDouble(3, promo.getDiscountValue());

            if (promo.isGlobal()) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, promo.getReservationId());
            }

            stmt.setString(5, promo.getStartDate());
            stmt.setString(6, promo.getEndDate());
            stmt.setBoolean(7, promo.isGlobal());
            stmt.setInt(8, promo.getId());

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("❌ Error updating promotion");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePromotion(int id) {
        String sql = "DELETE FROM Promotions WHERE promo_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("❌ Error deleting promotion");
            e.printStackTrace();
            return false;
        }
    }

    public Promotion getBestApplicablePromotion(int reservationId, String date) {
        Promotion best = null;
        double bestValue = 0;

        String sql = "SELECT * FROM Promotions WHERE (reservation_id = ? OR is_global = 1) " +
                "AND start_date <= ? AND end_date >= ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            stmt.setString(2, date);
            stmt.setString(3, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double discount = rs.getDouble("discount_value");
                String type = rs.getString("discount_type");

                double weight = "Percentage".equalsIgnoreCase(type) ? discount : discount * 1.0;

                if (weight > bestValue) {
                    Promotion p = new Promotion();
                    p.setId(rs.getInt("promo_id"));
                    p.setCode(rs.getString("code"));
                    p.setDiscountType(type);
                    p.setDiscountValue(discount);
                    p.setReservationId(rs.getInt("reservation_id"));
                    p.setStartDate(rs.getString("start_date"));
                    p.setEndDate(rs.getString("end_date"));
                    p.setGlobal(rs.getBoolean("is_global"));

                    best = p;
                    bestValue = weight;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return best;
    }
}
