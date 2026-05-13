package org.ecommerce.servlet.user;

import com.google.gson.JsonObject;
import org.ecommerce.model.User;
import org.ecommerce.repository.UserRepository;
import org.ecommerce.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AdminUserServlet extends HttpServlet {

    private final UserRepository userRepo = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            List<User> users = userRepo.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (User u : users) result.add(toMap(u));
            JsonUtil.sendJson(res, 200, result);
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // URL: /api/admin/users/{id}/status
        String pathInfo = req.getPathInfo(); // /{id}/status
        if (pathInfo == null || !pathInfo.matches("/\\d+/status")) {
            JsonUtil.sendError(res, 400, "Ruta inválida. Use /api/admin/users/{id}/status");
            return;
        }
        int userId = Integer.parseInt(pathInfo.split("/")[1]);
        String body = req.getReader().lines().collect(Collectors.joining());
        JsonObject json = JsonUtil.fromJson(body, JsonObject.class);

        if (!json.has("active")) {
            JsonUtil.sendError(res, 400, "El campo 'active' es obligatorio");
            return;
        }
        boolean active = json.get("active").getAsBoolean();
        try {
            userRepo.setActive(userId, active);
            JsonUtil.sendSuccess(res, active ? "Usuario activado" : "Usuario desactivado");
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    private Map<String, Object> toMap(User u) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",        u.getId());
        m.put("name",      u.getName());
        m.put("email",     u.getEmail());
        m.put("phone",     u.getPhone());
        m.put("role",      u.getRole());
        m.put("active",    u.isActive());
        m.put("createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : null);
        return m;
    }
}
