package org.ecommerce.servlet.user;

import com.google.gson.JsonObject;
import org.ecommerce.model.User;
import org.ecommerce.repository.UserRepository;
import org.ecommerce.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ProfileServlet extends HttpServlet {

    private final UserRepository userRepo = new UserRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        int userId = (int) req.getAttribute("userId");
        try {
            User user = userRepo.findById(userId);
            if (user == null) { JsonUtil.sendError(res, 404, "Usuario no encontrado"); return; }
            JsonUtil.sendJson(res, 200, toMap(user));
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

        try {
            User user = userRepo.findById(userId);
            if (user == null) { JsonUtil.sendError(res, 404, "Usuario no encontrado"); return; }

            if (json.has("name")    && !json.get("name").isJsonNull())    user.setName(json.get("name").getAsString().trim());
            if (json.has("phone")   && !json.get("phone").isJsonNull())   user.setPhone(json.get("phone").getAsString().trim());
            if (json.has("address") && !json.get("address").isJsonNull()) user.setAddress(json.get("address").getAsString().trim());

            if (json.has("newPassword") && !json.get("newPassword").isJsonNull()) {
                String newPass = json.get("newPassword").getAsString();
                if (newPass.length() < 6) {
                    JsonUtil.sendError(res, 400, "La nueva contraseña debe tener al menos 6 caracteres");
                    return;
                }
                if (!json.has("currentPassword") || json.get("currentPassword").isJsonNull()) {
                    JsonUtil.sendError(res, 400, "Se requiere la contraseña actual");
                    return;
                }
                String current = json.get("currentPassword").getAsString();
                if (!PasswordUtil.verify(current, user.getPasswordHash())) {
                    JsonUtil.sendError(res, 400, "Contraseña actual incorrecta");
                    return;
                }
                user.setPasswordHash(PasswordUtil.hash(newPass));
            }

            userRepo.update(user);
            JsonUtil.sendJson(res, 200, toMap(user));
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    private Map<String, Object> toMap(User u) {
        Map<String, Object> m = new HashMap<>();
        m.put("id",      u.getId());
        m.put("name",    u.getName());
        m.put("email",   u.getEmail());
        m.put("phone",   u.getPhone());
        m.put("address", u.getAddress());
        m.put("role",    u.getRole());
        m.put("active",  u.isActive());
        return m;
    }
}
