package group16.comp4442_project.dao;

import group16.comp4442_project.model.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDAO {
    private final DBUtil dbUtil;

    public UserDAO(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public void save(User user){
        String username = user.getUsername().replace("'", "''");
        String password = user.getPassword().replace("'", "''");
        String role = user.getRole().replace("'", "''");
        String sql = "INSERT INTO users(username,password,role) VALUES('"
                + username + "','" + password + "','" + role + "')";
        try (Connection conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public Optional<User> findByUsername(String username) {
        String safeUsername = username.replace("'", "''");
        String sql = "SELECT id, username, password, role FROM users WHERE username='" + safeUsername + "'";
        try (Connection conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return Optional.of(mapUser(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by username", e);
        }
        return Optional.empty();
    }

    public Optional<User> findById(int id) {
        String sql = "SELECT id, username, password, role FROM users WHERE id=" + id;
        try (Connection conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return Optional.of(mapUser(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by id", e);
        }
        return Optional.empty();
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, username, password, role FROM users ORDER BY id")) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to list users", e);
        }
        return users;
    }

    public void updateRole(int userId, String role) {
        String safeRole = role.replace("'", "''");
        String sql = "UPDATE users SET role='" + safeRole + "' WHERE id=" + userId;
        try (Connection conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update role", e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        return user;
    }
}