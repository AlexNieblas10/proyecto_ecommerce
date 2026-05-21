package org.ecommerce.repository;

import org.ecommerce.model.Order;
import org.ecommerce.model.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {

    public Order create(Order order, List<OrderItem> items) throws SQLException {
        String insertOrder = "INSERT INTO orders (order_number, user_id, shipping_address, payment_method, total) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int orderId;
                try (PreparedStatement ps = con.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, order.getOrderNumber());
                    ps.setInt(2, order.getUserId());
                    ps.setString(3, order.getShippingAddress());
                    ps.setString(4, order.getPaymentMethod());
                    ps.setBigDecimal(5, order.getTotal());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        orderId = rs.getInt(1);
                    }
                }
                order.setId(orderId);
                String insertItem = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?,?,?,?)";
                try (PreparedStatement ps = con.prepareStatement(insertItem)) {
                    for (OrderItem item : items) {
                        ps.setInt(1, orderId);
                        ps.setInt(2, item.getProductId());
                        ps.setInt(3, item.getQuantity());
                        ps.setBigDecimal(4, item.getUnitPrice());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();
                order.setItems(items);
                return order;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public List<Order> findByUser(int userId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id=? ORDER BY created_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapOrder(rs));
            }
        }
        return list;
    }

    public Order findById(int id) throws SQLException {
        String sql = "SELECT o.*, u.name AS user_name, u.email AS user_email FROM orders o " +
                     "JOIN users u ON o.user_id = u.id WHERE o.id = ?";
        Order order = null;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = mapOrder(rs);
                    order.setUserName(rs.getString("user_name"));
                    order.setUserEmail(rs.getString("user_email"));
                }
            }
        }
        if (order != null) {
            order.setItems(findItemsByOrderId(id));
        }
        return order;
    }

    public List<Order> findAll() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.name AS user_name, u.email AS user_email FROM orders o " +
                     "JOIN users u ON o.user_id = u.id ORDER BY o.created_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order o = mapOrder(rs);
                o.setUserName(rs.getString("user_name"));
                o.setUserEmail(rs.getString("user_email"));
                list.add(o);
            }
        }
        return list;
    }

    public void updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE orders SET status=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private List<OrderItem> findItemsByOrderId(int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.name AS product_name FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getInt("id"));
                    item.setOrderId(orderId);
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setOrderNumber(rs.getString("order_number"));
        o.setUserId(rs.getInt("user_id"));
        o.setShippingAddress(rs.getString("shipping_address"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setTotal(rs.getBigDecimal("total"));
        o.setStatus(rs.getString("status"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        return o;
    }
    
    public List<Order> findAllForAdmin() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapOrder(rs));
            }
        }
        return list;
    }
}
