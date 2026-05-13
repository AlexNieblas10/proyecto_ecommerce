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

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String token = CookieUtil.getJwtToken(req);
        if (token == null) {
            // Check Authorization header as fallback
            String authHeader = req.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        if (token == null) {
            handleUnauthorized(req, res, "Se requiere autenticación");
            return;
        }

        try {
            Claims claims = JWTUtil.validateToken(token);
            req.setAttribute("userId", Integer.parseInt(claims.getSubject()));
            req.setAttribute("userEmail", claims.get("email", String.class));
            req.setAttribute("userRole", claims.get("role", String.class));
            chain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException e) {
            handleUnauthorized(req, res, "Token inválido o expirado");
        }
    }

    protected void handleUnauthorized(HttpServletRequest req, HttpServletResponse res, String msg)
            throws IOException {
        String accept = req.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            JsonUtil.sendError(res, 401, msg);
        } else {
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}
