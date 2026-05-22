package org.ecommerce.servlet.product;

import com.google.gson.JsonObject;
import org.ecommerce.model.Product;
import org.ecommerce.repository.ProductRepository;
import org.ecommerce.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ProductServlet extends HttpServlet {

    private final ProductRepository productRepo = new ProductRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            String name        = req.getParameter("name");
            String categoryStr = req.getParameter("categoryId");
            String minPriceStr = req.getParameter("minPrice");
            String maxPriceStr = req.getParameter("maxPrice");

            Integer categoryId = categoryStr != null && !categoryStr.isEmpty()
                    ? Integer.parseInt(categoryStr) : null;
            BigDecimal minPrice = minPriceStr != null && !minPriceStr.isEmpty()
                    ? new BigDecimal(minPriceStr) : null;
            BigDecimal maxPrice = maxPriceStr != null && !maxPriceStr.isEmpty()
                    ? new BigDecimal(maxPriceStr) : null;

            String userRole  = (String) req.getAttribute("userRole");
            boolean adminView = "admin".equals(userRole) && "true".equals(req.getParameter("admin"));
            try {
                List<Product> products;
                if (adminView) {
                    products = productRepo.findAllForAdmin();
                } else {
                    products = productRepo.findAll(name, categoryId, minPrice, maxPrice);
                }
                JsonUtil.sendJson(res, 200, toMapList(products));
            } catch (Exception e) {
                JsonUtil.sendError(res, 500, e.getMessage());
            }
        } else {
            try {
                int id = Integer.parseInt(pathInfo.replaceFirst("/", ""));
                Product p = productRepo.findById(id);
                if (p == null) { JsonUtil.sendError(res, 404, "Producto no encontrado"); return; }
                JsonUtil.sendJson(res, 200, toMap(p));
            } catch (NumberFormatException e) {
                JsonUtil.sendError(res, 400, "ID de producto inválido");
            } catch (Exception e) {
                JsonUtil.sendError(res, 500, e.getMessage());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String userRole = (String) req.getAttribute("userRole");
        if (!"admin".equals(userRole)) {
            JsonUtil.sendError(res, 403, "Solo administradores pueden crear productos");
            return;
        }
        String body = req.getReader().lines().collect(Collectors.joining());
        JsonObject json = JsonUtil.fromJson(body, JsonObject.class);
        try {
            Product p = fromJson(json);
            int id = productRepo.save(p);
            p.setId(id);
            JsonUtil.sendJson(res, 201, toMap(p));
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String userRole = (String) req.getAttribute("userRole");
        if (!"admin".equals(userRole)) {
            JsonUtil.sendError(res, 403, "Solo administradores pueden editar productos");
            return;
        }
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendError(res, 400, "Especifica el ID del producto");
            return;
        }
        try {
            int id = Integer.parseInt(pathInfo.replaceFirst("/", ""));
            Product existing = productRepo.findById(id);
            if (existing == null) { JsonUtil.sendError(res, 404, "Producto no encontrado"); return; }

            String body = req.getReader().lines().collect(Collectors.joining());
            JsonObject json = JsonUtil.fromJson(body, JsonObject.class);
            Product updated = fromJson(json);
            updated.setId(id);
            productRepo.update(updated);
            JsonUtil.sendJson(res, 200, toMap(updated));
        } catch (NumberFormatException e) {
            JsonUtil.sendError(res, 400, "ID inválido");
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String userRole = (String) req.getAttribute("userRole");
        if (!"admin".equals(userRole)) {
            JsonUtil.sendError(res, 403, "Solo administradores pueden eliminar productos");
            return;
        }
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) { JsonUtil.sendError(res, 400, "Especifica el ID"); return; }
        try {
            int id = Integer.parseInt(pathInfo.replaceFirst("/", ""));
            productRepo.delete(id);
            JsonUtil.sendSuccess(res, "Producto desactivado correctamente");
        } catch (NumberFormatException e) {
            JsonUtil.sendError(res, 400, "ID inválido");
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    private Product fromJson(JsonObject json) {
        Product p = new Product();
        if (json.has("name"))           p.setName(json.get("name").getAsString());
        if (json.has("description"))    p.setDescription(json.get("description").getAsString());
        if (json.has("price"))          p.setPrice(json.get("price").getAsBigDecimal());
        if (json.has("stock"))          p.setStock(json.get("stock").getAsInt());
        if (json.has("imageUrl"))       p.setImageUrl(json.get("imageUrl").getAsString());
        if (json.has("categoryId"))     p.setCategoryId(json.get("categoryId").getAsInt());
        if (json.has("specifications")) p.setSpecifications(json.get("specifications").getAsString());
        return p;
    }

    private Map<String, Object> toMap(Product p) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",             p.getId());
        m.put("name",           p.getName());
        m.put("description",    p.getDescription());
        m.put("price",          p.getPrice());
        m.put("stock",          p.getStock());
        m.put("imageUrl",       p.getImageUrl());
        m.put("categoryId",     p.getCategoryId());
        m.put("categoryName",   p.getCategoryName());
        m.put("specifications", p.getSpecifications());
        m.put("active",         p.isActive());
        return m;
    }

    private List<Map<String, Object>> toMapList(List<Product> products) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Product p : products) list.add(toMap(p));
        return list;
    }
}
