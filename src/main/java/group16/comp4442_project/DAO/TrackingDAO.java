package group16.comp4442_project.dao;

import group16.comp4442_project.model.Tracking;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

@Repository
public class TrackingDAO {
    private final DBUtil dbUtil;

    public TrackingDAO(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    // Insert a new tracking record
    public void create(Tracking tracking) {
        String sql = "INSERT INTO trackings(order_id, location_description, latitude, longitude, status) VALUES(?,?,?,?,?)";
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tracking.getOrderId());
            ps.setString(2, tracking.getLocationDescription());
            ps.setDouble(3, tracking.getLatitude());
            ps.setDouble(4, tracking.getLongitude());
            ps.setString(5, tracking.getStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create tracking", e);
        }
    }

    // Find tracking by order ID
    public Optional<Tracking> findByOrderId(int orderId) {
        String sql = "SELECT * FROM trackings WHERE order_id = ?";
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to find tracking by order id", e);
        }
        return Optional.empty();
    }

    // Update tracking status and location (simulate real-time movement)
    public void updateTracking(int orderId, String locationDesc, double lat, double lng, String status) {
        String sql = "UPDATE trackings SET location_description=?, latitude=?, longitude=?, status=?, last_update=CURRENT_TIMESTAMP WHERE order_id=?";
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, locationDesc);
            ps.setDouble(2, lat);
            ps.setDouble(3, lng);
            ps.setString(4, status);
            ps.setInt(5, orderId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update tracking", e);
        }
    }

    private Tracking mapRow(ResultSet rs) throws SQLException {
        Tracking t = new Tracking();
        t.setId(rs.getInt("id"));
        t.setOrderId(rs.getInt("order_id"));
        t.setLocationDescription(rs.getString("location_description"));
        t.setLatitude(rs.getDouble("latitude"));
        t.setLongitude(rs.getDouble("longitude"));
        t.setStatus(rs.getString("status"));
        t.setLastUpdate(rs.getTimestamp("last_update"));
        return t;
    }
}