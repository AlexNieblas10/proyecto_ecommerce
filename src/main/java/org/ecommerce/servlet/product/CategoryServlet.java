package org.ecommerce.servlet.product;

import com.google.gson.JsonObject;
import org.ecommerce.model.Category;
import org.ecommerce.repository.CategoryRepository;
import org.ecommerce.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CategoryServlet extends HttpServlet {

    private final CategoryRepository categoryRepo = new CategoryRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            List<Category> cats = categoryRepo.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Category c : cats) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", c.getId());
                m.put("name", c.getName());
                m.put("description", c.getDescription());
                result.add(m);
            }
            JsonUtil.sendJson(res, 200, result);
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String userRole = (String) req.getAttribute("userRole");
        if (!"admin".equals(userRole)) {
            JsonUtil.sendError(res, 403, "Solo administradores pueden crear categorías");
            return;
        }
        String body = req.getReader().lines().collect(Collectors.joining());
        JsonObject json = JsonUtil.fromJson(body, JsonObject.class);
        try {
            Category cat = new Category();
            cat.setName(json.get("name").getAsString());
            if (json.has("description")) cat.setDescription(json.get("description").getAsString());
            int id = categoryRepo.save(cat);
            cat.setId(id);
            Map<String, Object> m = Map.of("id", id, "name", cat.getName());
            JsonUtil.sendJson(res, 201, m);
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String userRole = (String) req.getAttribute("userRole");
        if (!"admin".equals(userRole)) {
            JsonUtil.sendError(res, 403, "Solo administradores pueden editar categorías");
            return;
        }
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) { JsonUtil.sendError(res, 400, "Especifica el ID"); return; }
        try {
            int id = Integer.parseInt(pathInfo.replaceFirst("/", ""));
            String body = req.getReader().lines().collect(Collectors.joining());
            JsonObject json = JsonUtil.fromJson(body, JsonObject.class);
            Category cat = new Category();
            cat.setId(id);
            cat.setName(json.get("name").getAsString());
            if (json.has("description")) cat.setDescription(json.get("description").getAsString());
            categoryRepo.update(cat);
            JsonUtil.sendSuccess(res, "Categoría actualizada");
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String userRole = (String) req.getAttribute("userRole");
        if (!"admin".equals(userRole)) {
            JsonUtil.sendError(res, 403, "Solo administradores pueden eliminar categorías");
            return;
        }
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) { JsonUtil.sendError(res, 400, "Especifica el ID"); return; }
        try {
            int id = Integer.parseInt(pathInfo.replaceFirst("/", ""));
            categoryRepo.delete(id);
            JsonUtil.sendSuccess(res, "Categoría eliminada");
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }
}
