/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.ecommerce.servlet.order;

import com.google.gson.JsonObject;
import org.ecommerce.model.Order;
import org.ecommerce.model.OrderItem;
import org.ecommerce.repository.OrderRepository;
import org.ecommerce.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AdminOrderServlet extends HttpServlet {

    private final OrderRepository orderRepo = new OrderRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String userRole = (String) req.getAttribute("userRole");
        if (!"admin".equals(userRole)) {
            JsonUtil.sendError(res, 403, "Acceso denegado. Solo administradores.");
            return;
        }

        try {
            List<Order> orders = orderRepo.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Order o : orders) result.add(orderToMap(o));
            JsonUtil.sendJson(res, 200, result);
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String userRole = (String) req.getAttribute("userRole");
        if (!"admin".equals(userRole)) {
            JsonUtil.sendError(res, 403, "Acceso denegado. Solo administradores.");
            return;
        }

        String pathInfo = req.getPathInfo(); // Debe ser /{id}/status
        if (pathInfo == null || !pathInfo.matches("/\\d+/status")) {
            JsonUtil.sendError(res, 400, "Ruta inválida. Use /api/admin/orders/{id}/status");
            return;
        }

        int orderId = Integer.parseInt(pathInfo.split("/")[1]);
        String body = req.getReader().lines().collect(Collectors.joining());
        JsonObject json = JsonUtil.fromJson(body, JsonObject.class);

        if (!json.has("status")) {
            JsonUtil.sendError(res, 400, "El campo 'status' es obligatorio");
            return;
        }

        String status = json.get("status").getAsString().trim();
        List<String> validStatuses = Arrays.asList("pending", "shipped", "delivered");
        
        if (!validStatuses.contains(status)) {
            JsonUtil.sendError(res, 400, "Estado inválido (pending, shipped, delivered)");
            return;
        }

        try {
            orderRepo.updateStatus(orderId, status);
            JsonUtil.sendSuccess(res, "Estado del pedido actualizado a: " + status);
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    private Map<String, Object> orderToMap(Order o) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",              o.getId());
        m.put("orderNumber",     o.getOrderNumber());
        m.put("userName",        o.getUserName());
        m.put("userEmail",       o.getUserEmail());
        m.put("shippingAddress", o.getShippingAddress());
        m.put("paymentMethod",   o.getPaymentMethod());
        m.put("total",           o.getTotal());
        m.put("status",          o.getStatus());
        m.put("createdAt",       o.getCreatedAt() != null ? o.getCreatedAt().toString() : null);
        return m;
    }
}