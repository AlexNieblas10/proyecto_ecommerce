package org.ecommerce.servlet.auth;

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

public class RegisterServlet extends HttpServlet {

    private final UserRepository userRepo = new UserRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String body = req.getReader().lines().collect(Collectors.joining());
        JsonObject json = JsonUtil.fromJson(body, JsonObject.class);

        String name    = getStr(json, "name");
        String email   = getStr(json, "email");
        String pass    = getStr(json, "password");
        String phone   = getStr(json, "phone");
        String address = getStr(json, "address");

        if (name == null || email == null || pass == null ||
            name.isBlank() || email.isBlank() || pass.isBlank()) {
            JsonUtil.sendError(res, 400, "Nombre, correo y contraseña son obligatorios");
            return;
        }
        if (pass.length() < 6) {
            JsonUtil.sendError(res, 400, "La contraseña debe tener al menos 6 caracteres");
            return;
        }

        try {
            if (userRepo.findByEmail(email) != null) {
                JsonUtil.sendError(res, 409, "Ya existe una cuenta con ese correo electrónico");
                return;
            }
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPasswordHash(PasswordUtil.hash(pass));
            user.setPhone(phone);
            user.setAddress(address);
            user.setRole("customer");
            int id = userRepo.save(user);
            user.setId(id);

            String token = JWTUtil.generateToken(id, email, "customer");
            CookieUtil.setJwtCookie(res, token);

            Map<String, Object> result = new HashMap<>();
            result.put("id", id);
            result.put("name", name);
            result.put("email", email);
            result.put("role", "customer");
            JsonUtil.sendJson(res, 201, result);
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, "Error al registrar usuario: " + e.getMessage());
        }
    }

    private String getStr(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) return null;
        String val = obj.get(key).getAsString().trim();
        return val.isEmpty() ? null : val;
    }
}
