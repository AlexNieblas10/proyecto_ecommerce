package org.ecommerce.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.ecommerce.util.CookieUtil;
import org.ecommerce.util.JsonUtil;
import org.ecommerce.util.JWTUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String token = CookieUtil.getJwtToken(req);
        if (token == null) {
            String authHeader = req.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        if (token == null) {
            sendForbidden(req, res, "Acceso denegado: autenticación requerida");
            return;
        }

        try {
            Claims claims = JWTUtil.validateToken(token);
            String role = claims.get("role", String.class);
            if (!"admin".equals(role)) {
                sendForbidden(req, res, "Acceso denegado: se requiere rol administrador");
                return;
            }
            req.setAttribute("userId", Integer.parseInt(claims.getSubject()));
            req.setAttribute("userEmail", claims.get("email", String.class));
            req.setAttribute("userRole", role);
            chain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException e) {
            sendForbidden(req, res, "Token inválido o expirado");
        }
    }

    private void sendForbidden(HttpServletRequest req, HttpServletResponse res, String msg)
            throws IOException {
        String accept = req.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            JsonUtil.sendError(res, 403, msg);
        } else {
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}
