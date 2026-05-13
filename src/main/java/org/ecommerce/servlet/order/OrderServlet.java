package org.ecommerce.servlet.order;

import com.google.gson.JsonObject;
import org.ecommerce.model.*;
import org.ecommerce.repository.*;
import org.ecommerce.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class OrderServlet extends HttpServlet {

    private final OrderRepository orderRepo = new OrderRepository();
    private final CartRepository  cartRepo  = new CartRepository();
    private final UserRepository  userRepo  = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        int userId = (int) req.getAttribute("userId");
        try {
            List<Order> orders = orderRepo.findByUser(userId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Order o : orders) result.add(orderToMap(o));
            JsonUtil.sendJson(res, 200, result);
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        int userId = (int) req.getAttribute("userId");
        String body = req.getReader().lines().collect(Collectors.joining());
        JsonObject json = JsonUtil.fromJson(body, JsonObject.class);

        if (!json.has("shippingAddress") || !json.has("paymentMethod")) {
            JsonUtil.sendError(res, 400, "Se requieren shippingAddress y paymentMethod");
            return;
        }
        String shippingAddress = json.get("shippingAddress").getAsString().trim();
        String paymentMethod   = json.get("paymentMethod").getAsString().trim();

        if (shippingAddress.isEmpty()) {
            JsonUtil.sendError(res, 400, "La dirección de envío no puede estar vacía");
            return;
        }
        List<String> validPayments = Arrays.asList("card", "transfer", "cash_on_delivery");
        if (!validPayments.contains(paymentMethod)) {
            JsonUtil.sendError(res, 400, "Método de pago inválido");
            return;
        }

        try {
            Cart cart = cartRepo.findOrCreateByUser(userId);
            if (cart.getItems().isEmpty()) {
                JsonUtil.sendError(res, 400, "El carrito está vacío");
                return;
            }

            List<OrderItem> items = new ArrayList<>();
            for (CartItem ci : cart.getItems()) {
                OrderItem oi = new OrderItem();
                oi.setProductId(ci.getProductId());
                oi.setProductName(ci.getProductName());
                oi.setQuantity(ci.getQuantity());
                oi.setUnitPrice(ci.getProductPrice());
                items.add(oi);
            }
            BigDecimal total = cart.getTotal();

            String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String rand = String.format("%04d", (int)(Math.random() * 10000));
            String orderNumber = "ORD-" + date + "-" + rand;

            Order order = new Order();
            order.setOrderNumber(orderNumber);
            order.setUserId(userId);
            order.setShippingAddress(shippingAddress);
            order.setPaymentMethod(paymentMethod);
            order.setTotal(total);

            Order created = orderRepo.create(order, items);

            cartRepo.clearCart(cart.getId());

            User user = userRepo.findById(userId);
            if (user != null) {
                created.setUserName(user.getName());
                created.setUserEmail(user.getEmail());
                EmailUtil.sendOrderConfirmation(user.getEmail(), created);
            }

            JsonUtil.sendJson(res, 201, orderToMap(created));
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, "Error al crear el pedido: " + e.getMessage());
        }
    }

    private Map<String, Object> orderToMap(Order o) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",              o.getId());
        m.put("orderNumber",     o.getOrderNumber());
        m.put("shippingAddress", o.getShippingAddress());
        m.put("paymentMethod",   o.getPaymentMethod());
        m.put("total",           o.getTotal());
        m.put("status",          o.getStatus());
        m.put("createdAt",       o.getCreatedAt() != null ? o.getCreatedAt().toString() : null);
        List<Map<String, Object>> itemList = new ArrayList<>();
        if (o.getItems() != null) {
            for (OrderItem oi : o.getItems()) {
                Map<String, Object> i = new HashMap<>();
                i.put("productName", oi.getProductName());
                i.put("quantity",    oi.getQuantity());
                i.put("unitPrice",   oi.getUnitPrice());
                i.put("subtotal",    oi.getSubtotal());
                itemList.add(i);
            }
        }
        m.put("items", itemList);
        return m;
    }
}
