package org.ecommerce.servlet.page;

import org.ecommerce.util.CookieUtil;
import org.ecommerce.util.JWTUtil;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class IndexServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        setUserAttributes(req);
        req.getRequestDispatcher("/WEB-INF/jsp/index.jsp").forward(req, res);
    }

    protected void setUserAttributes(HttpServletRequest req) {
        String token = CookieUtil.getJwtToken(req);
        if (token != null) {
            try {
                var claims = JWTUtil.validateToken(token);
                req.setAttribute("loggedUserId",   Integer.parseInt(claims.getSubject()));
                req.setAttribute("loggedUserEmail", claims.get("email", String.class));
                req.setAttribute("loggedUserRole",  claims.get("role",  String.class));
            } catch (Exception ignored) {}
        }
    }
}
