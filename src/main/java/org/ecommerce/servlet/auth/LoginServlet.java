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

public class LoginServlet extends HttpServlet {

    private final UserRepository userRepo = new UserRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String body = req.getReader().lines().collect(Collectors.joining());
        JsonObject json = JsonUtil.fromJson(body, JsonObject.class);

        String email = json.has("email")    ? json.get("email").getAsString().trim()    : "";
        String pass  = json.has("password") ? json.get("password").getAsString()        : "";

        if (email.isEmpty() || pass.isEmpty()) {
            JsonUtil.sendError(res, 400, "Correo y contraseña son obligatorios");
            return;
        }

        try {
            User user = userRepo.findByEmail(email);
            if (user == null || !PasswordUtil.verify(pass, user.getPasswordHash())) {
                JsonUtil.sendError(res, 401, "Credenciales incorrectas");
                return;
            }
            if (!user.isActive()) {
                JsonUtil.sendError(res, 403, "Tu cuenta está desactivada. Contacta al administrador.");
                return;
            }

            String token = JWTUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
            CookieUtil.setJwtCookie(res, token);

            Map<String, Object> result = new HashMap<>();
            result.put("id",    user.getId());
            result.put("name",  user.getName());
            result.put("email", user.getEmail());
            result.put("role",  user.getRole());
            JsonUtil.sendJson(res, 200, result);
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, "Error en el servidor: " + e.getMessage());
        }
    }
}
