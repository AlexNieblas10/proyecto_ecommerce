package org.ecommerce.servlet.auth;

import org.ecommerce.util.CookieUtil;
import org.ecommerce.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        CookieUtil.clearJwtCookie(res);
        JsonUtil.sendSuccess(res, "Sesión cerrada correctamente");
    }
}
