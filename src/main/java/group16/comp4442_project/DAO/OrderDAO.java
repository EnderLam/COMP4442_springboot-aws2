package group16.comp4442_project.dao;

import group16.comp4442_project.model.Order;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderDAO {
    private final DBUtil dbUtil;

    public OrderDAO(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public boolean createOrderAndDecreaseStock(Order order) {
        String safeStatus = order.getStatus().replace("'", "''");
        String decreaseStockSql = "UPDATE products SET stock = stock - " + order.getQuantity()
                + " WHERE id = " + order.getProductId() + " AND stock >= " + order.getQuantity();
        String createOrderSql = "INSERT INTO orders(user_id, product_id, quantity, status) VALUES("
                + order.getUserId() + "," + order.getProductId() + "," + order.getQuantity() + ",'"
                + safeStatus + "')";
        try (Connection conn = dbUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                int updated = stmt.executeUpdate(decreaseStockSql);
                if (updated == 0) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    return false;
                }
                stmt.executeUpdate(createOrderSql);

                conn.commit();
                conn.setAutoCommit(true);
                return true;
            } catch (Exception e) {
                conn.rollback();
                conn.setAutoCommit(true);
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create order", e);
        }
    }

    public List<Order> findAll() {
        return queryOrders("SELECT * FROM orders ORDER BY id DESC");
    }

    public List<Order> findByUserId(int userId) {
        return queryOrders("SELECT * FROM orders WHERE user_id = " + userId + " ORDER BY id DESC");
    }

    public List<Order> findBySeller(String seller) {
        String safeSeller = seller.replace("'", "''");
        return queryOrders("SELECT o.* FROM orders o JOIN products p ON o.product_id = p.id WHERE p.seller = '"
                + safeSeller + "' ORDER BY o.id DESC");
    }

    public Optional<Order> findById(int id) {
        List<Order> orders = queryOrders("SELECT * FROM orders WHERE id = " + id);
        return orders.stream().findFirst();
    }

    public void delete(int id) {
        try (Connection conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM orders WHERE id = " + id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete order", e);
        }
    }

    private List<Order> queryOrders(String sql) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setProductId(rs.getInt("product_id"));
                order.setQuantity(rs.getInt("quantity"));
                order.setStatus(rs.getString("status"));
                orders.add(order);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to query orders", e);
        }
        return orders;
    }
}
