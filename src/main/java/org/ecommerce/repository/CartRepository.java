package org.ecommerce.repository;

import org.ecommerce.model.Cart;
import org.ecommerce.model.CartItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartRepository {

    public Cart findOrCreateByUser(int userId) throws SQLException {
        Cart cart = findByUser(userId);
        if (cart == null) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "INSERT INTO carts (user_id) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
                cart = new Cart();
                cart.setUserId(userId);
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) cart.setId(rs.getInt(1));
                }
            }
        }
        return cart;
    }

    private Cart findByUser(int userId) throws SQLException {
        String sql = "SELECT c.id, c.user_id, ci.id AS item_id, ci.product_id, ci.quantity, " +
                     "p.name AS product_name, p.price AS product_price, p.image_url " +
                     "FROM carts c " +
                     "LEFT JOIN cart_items ci ON c.id = ci.cart_id " +
                     "LEFT JOIN products p ON ci.product_id = p.id " +
                     "WHERE c.user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                Cart cart = null;
                List<CartItem> items = new ArrayList<>();
                while (rs.next()) {
                    if (cart == null) {
                        cart = new Cart();
                        cart.setId(rs.getInt("id"));
                        cart.setUserId(userId);
                    }
                    int itemId = rs.getInt("item_id");
                    if (itemId != 0) {
                        CartItem item = new CartItem();
                        item.setId(itemId);
                        item.setCartId(cart.getId());
                        item.setProductId(rs.getInt("product_id"));
                        item.setProductName(rs.getString("product_name"));
                        item.setProductPrice(rs.getBigDecimal("product_price"));
                        item.setImageUrl(rs.getString("image_url"));
                        item.setQuantity(rs.getInt("quantity"));
                        items.add(item);
                    }
                }
                if (cart != null) cart.setItems(items);
                return cart;
            }
        }
    }

    public void addItem(int cartId, int productId, int qty) throws SQLException {
        String sql = "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES (?,?,?) " +
                     "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            ps.executeUpdate();
        }
    }

    public void updateItem(int cartId, int productId, int qty) throws SQLException {
        String sql = "UPDATE cart_items SET quantity=? WHERE cart_id=? AND product_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setInt(2, cartId);
            ps.setInt(3, productId);
            ps.executeUpdate();
        }
    }

    public void removeItem(int cartId, int productId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE cart_id=? AND product_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    public void clearCart(int cartId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE cart_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        }
    }
}
