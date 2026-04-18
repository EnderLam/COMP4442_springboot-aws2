package group16.comp4442_project.dao;

import group16.comp4442_project.model.Payment;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentDAO {
    private final DBUtil dbUtil;

    public PaymentDAO(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public void save(Payment payment, String cardLast4) {
        String safeStatus = payment.getStatus().replace("'", "''");
        String safeLast4 = cardLast4.replace("'", "''");
        String sql = "INSERT INTO payments(order_id, user_id, amount, status, card_last4) VALUES("
                + payment.getOrderId() + "," + payment.getUserId() + "," + payment.getAmount()
                + ",'" + safeStatus + "','" + safeLast4 + "')";
        try (var conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save payment", e);
        }
    }

    public List<Payment> findAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payments ORDER BY id DESC";
        try (var conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setOrderId(rs.getInt("order_id"));
                payment.setUserId(rs.getInt("user_id"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setStatus(rs.getString("status"));
                payments.add(payment);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to list payments", e);
        }
        return payments;
    }
}
