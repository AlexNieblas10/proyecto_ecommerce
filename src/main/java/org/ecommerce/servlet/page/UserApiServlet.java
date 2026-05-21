/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package org.ecommerce.servlet.page;

import com.google.gson.Gson;
import org.ecommerce.repository.UserRepository;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/admin/users")
public class UserApiServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        try {
            res.getWriter().write(new Gson().toJson(new UserRepository().findAll()));
        } catch (Exception e) { res.setStatus(500); }
    }
}