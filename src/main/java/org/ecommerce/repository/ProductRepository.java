package org.ecommerce.repository;

import org.ecommerce.model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    public List<Product> findAll(String name, Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice) throws SQLException {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.*, c.name AS category_name FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "WHERE p.active = TRUE");

        if (name != null && !name.trim().isEmpty()) sql.append(" AND p.name LIKE ?");
        if (categoryId != null) sql.append(" AND p.category_id = ?");
        if (minPrice != null) sql.append(" AND p.price >= ?");
        if (maxPrice != null) sql.append(" AND p.price <= ?");
        sql.append(" ORDER BY p.created_at DESC");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (name != null && !name.trim().isEmpty()) ps.setString(idx++, "%" + name.trim() + "%");
            if (categoryId != null) ps.setInt(idx++, categoryId);
            if (minPrice != null) ps.setBigDecimal(idx++, minPrice);
            if (maxPrice != null) ps.setBigDecimal(idx, maxPrice);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Product findById(int id) throws SQLException {
        String sql = "SELECT p.*, c.name AS category_name FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.id " +
                     "WHERE p.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Product> findAllForAdmin() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS category_name FROM products p " +
                     "LEFT JOIN categories c ON p.category_id = c.id ORDER BY p.id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public int save(Product p) throws SQLException {
        String sql = "INSERT INTO products (name, description, price, stock, image_url, category_id, specifications) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getImageUrl());
            ps.setInt(6, p.getCategoryId());
            ps.setString(7, p.getSpecifications());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public void update(Product p) throws SQLException {
        String sql = "UPDATE products SET name=?, description=?, price=?, stock=?, image_url=?, category_id=?, specifications=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getImageUrl());
            ps.setInt(6, p.getCategoryId());
            ps.setString(7, p.getSpecifications());
            ps.setInt(8, p.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "UPDATE products SET active=FALSE WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStock(rs.getInt("stock"));
        p.setImageUrl(rs.getString("image_url"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setCategoryName(rs.getString("category_name"));
        p.setSpecifications(rs.getString("specifications"));
        p.setActive(rs.getBoolean("active"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
