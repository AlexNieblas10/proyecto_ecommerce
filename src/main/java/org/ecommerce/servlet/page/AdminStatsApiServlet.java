/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.ecommerce.servlet.page;

import com.google.gson.Gson;
import org.ecommerce.repository.ProductRepository;
import org.ecommerce.repository.UserRepository;
import org.ecommerce.repository.CategoryRepository; 

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/products", "/api/admin/users", "/api/categories"})
public class AdminStatsApiServlet extends HttpServlet {

    private Gson gson = new Gson();
    private ProductRepository prodRepo = new ProductRepository();
    private UserRepository userRepo = new UserRepository(); 
    private CategoryRepository catRepo = new CategoryRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        String path = req.getServletPath();

        try {
            if (path.contains("products")) {
                res.getWriter().write(gson.toJson(prodRepo.findAllForAdmin()));
            } else if (path.contains("users")) {
                res.getWriter().write(gson.toJson(userRepo.findAll()));
            } else if (path.contains("categories")) {
                res.getWriter().write(gson.toJson(catRepo.findAll()));
            }
        } catch (Exception e) {
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}