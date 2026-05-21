package org.ecommerce.servlet.page;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class AdminPageServlet extends IndexServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        setUserAttributes(req);
        String pathInfo = req.getPathInfo();
        String jsp;
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
            jsp = "/WEB-INF/jsp/admin/dashboard.jsp";
        } else if (pathInfo.equals("/products")) {
            jsp = "/WEB-INF/jsp/admin/products.jsp";
        } else if (pathInfo.equals("/users")) {
            jsp = "/WEB-INF/jsp/admin/users.jsp";
        } else if (pathInfo.equals("/categories")) {
            jsp = "/WEB-INF/jsp/admin/categories.jsp";
        } else if (pathInfo.equals("/orders")) {
            jsp = "/WEB-INF/jsp/admin/orders.jsp";
        } else if (pathInfo.equals("/orders")) {
            jsp = "/WEB-INF/jsp/admin/orders.jsp";
        } else {
            jsp = "/WEB-INF/jsp/admin/dashboard.jsp";
        }
        req.getRequestDispatcher(jsp).forward(req, res);
    }
}
