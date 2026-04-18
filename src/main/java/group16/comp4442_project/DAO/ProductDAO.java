package group16.comp4442_project.dao;

import group16.comp4442_project.model.Product;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDAO {
    private final DBUtil dbUtil;

    public ProductDAO(DBUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM products ORDER BY id DESC")) {
            while (rs.next()) {
                products.add(mapProduct(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to list products", e);
        }
        return products;
    }

    public Optional<Product> findById(int id) {
        String sql = "SELECT * FROM products WHERE id=" + id;
        try (Connection conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return Optional.of(mapProduct(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to find product by id", e);
        }
        return Optional.empty();
    }

    public void save(Product product){
        String name = product.getName().replace("'", "''");
        String imageUrl = (product.getImageUrl() == null ? "" : product.getImageUrl()).replace("'", "''");
        String seller = product.getSeller().replace("'", "''");
        String sql = "INSERT INTO products(name,price,stock,image_url,seller_user_id,seller) VALUES('"
                + name + "'," + product.getPrice() + "," + product.getStock() + ",'"
                + imageUrl + "'," + product.getSellerUserId() + ",'" + seller + "')";
        try (Connection conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save product", e);
        }
    }

    public void update(Product product) {
        String name = product.getName().replace("'", "''");
        String sql = "UPDATE products SET name='" + name + "', price=" + product.getPrice()
                + ", stock=" + product.getStock() + " WHERE id=" + product.getId();
        try (Connection conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product", e);
        }
    }

    public void delete(int id){
        String sql = "DELETE FROM products WHERE id=" + id;
        try (Connection conn = dbUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getDouble("price"));
        product.setStock(rs.getInt("stock"));
        product.setImageUrl(rs.getString("image_url"));
        product.setSellerUserId(rs.getInt("seller_user_id"));
        product.setSeller(rs.getString("seller"));
        return product;
    }
}