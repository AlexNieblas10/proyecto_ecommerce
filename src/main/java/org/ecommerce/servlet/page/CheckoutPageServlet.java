package org.ecommerce.servlet.page;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class CheckoutPageServlet extends IndexServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        setUserAttributes(req);
        req.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(req, res);
    }
}
