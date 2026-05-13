package org.ecommerce.servlet.cart;

import com.google.gson.JsonObject;
import org.ecommerce.model.Cart;
import org.ecommerce.repository.CartRepository;
import org.ecommerce.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CartServlet extends HttpServlet {

    private final CartRepository cartRepo = new CartRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        int userId = (int) req.getAttribute("userId");
        try {
            Cart cart = cartRepo.findOrCreateByUser(userId);
            JsonUtil.sendJson(res, 200, cartToMap(cart));
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

        if (!json.has("productId") || !json.has("quantity")) {
            JsonUtil.sendError(res, 400, "Se requieren productId y quantity");
            return;
        }
        int productId = json.get("productId").getAsInt();
        int quantity  = json.get("quantity").getAsInt();
        if (quantity <= 0) { JsonUtil.sendError(res, 400, "La cantidad debe ser mayor que 0"); return; }

        try {
            Cart cart = cartRepo.findOrCreateByUser(userId);
            cartRepo.addItem(cart.getId(), productId, quantity);
            Cart updated = cartRepo.findOrCreateByUser(userId);
            JsonUtil.sendJson(res, 200, cartToMap(updated));
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        int userId = (int) req.getAttribute("userId");
        String body = req.getReader().lines().collect(Collectors.joining());
        JsonObject json = JsonUtil.fromJson(body, JsonObject.class);

        if (!json.has("productId") || !json.has("quantity")) {
            JsonUtil.sendError(res, 400, "Se requieren productId y quantity");
            return;
        }
        int productId = json.get("productId").getAsInt();
        int quantity  = json.get("quantity").getAsInt();

        try {
            Cart cart = cartRepo.findOrCreateByUser(userId);
            if (quantity <= 0) {
                cartRepo.removeItem(cart.getId(), productId);
            } else {
                cartRepo.updateItem(cart.getId(), productId, quantity);
            }
            Cart updated = cartRepo.findOrCreateByUser(userId);
            JsonUtil.sendJson(res, 200, cartToMap(updated));
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        int userId = (int) req.getAttribute("userId");
        String pathInfo = req.getPathInfo(); // /{productId}

        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendError(res, 400, "Especifica el ID del producto a eliminar");
            return;
        }
        try {
            int productId = Integer.parseInt(pathInfo.replaceFirst("/", ""));
            Cart cart = cartRepo.findOrCreateByUser(userId);
            cartRepo.removeItem(cart.getId(), productId);
            Cart updated = cartRepo.findOrCreateByUser(userId);
            JsonUtil.sendJson(res, 200, cartToMap(updated));
        } catch (NumberFormatException e) {
            JsonUtil.sendError(res, 400, "ID de producto inválido");
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    private Map<String, Object> cartToMap(Cart cart) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",        cart.getId());
        m.put("userId",    cart.getUserId());
        m.put("total",     cart.getTotal());
        m.put("itemCount", cart.getItemCount());
        List<Map<String, Object>> itemList = new ArrayList<>();
        cart.getItems().forEach(item -> {
            Map<String, Object> i = new HashMap<>();
            i.put("id",           item.getId());
            i.put("productId",    item.getProductId());
            i.put("productName",  item.getProductName());
            i.put("productPrice", item.getProductPrice());
            i.put("imageUrl",     item.getImageUrl());
            i.put("quantity",     item.getQuantity());
            i.put("subtotal",     item.getSubtotal());
            itemList.add(i);
        });
        m.put("items", itemList);
        return m;
    }
}
