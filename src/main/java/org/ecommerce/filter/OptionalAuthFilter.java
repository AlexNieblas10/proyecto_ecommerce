package org.ecommerce.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.ecommerce.util.CookieUtil;
import org.ecommerce.util.JWTUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class OptionalAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        String token = CookieUtil.getJwtToken(req);
        if (token == null) {
            String authHeader = req.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        if (token != null) {
            try {
                Claims claims = JWTUtil.validateToken(token);
                req.setAttribute("userId",    Integer.parseInt(claims.getSubject()));
                req.setAttribute("userEmail", claims.get("email", String.class));
                req.setAttribute("userRole",  claims.get("role",  String.class));
            } catch (JwtException | IllegalArgumentException ignored) {
            }
        }

        chain.doFilter(request, response);
    }

    @Override public void init(FilterConfig fc) {}
    @Override public void destroy() {}
}
