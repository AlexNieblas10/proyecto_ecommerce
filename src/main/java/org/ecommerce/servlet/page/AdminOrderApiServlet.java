/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.ecommerce.servlet.page;

import com.google.gson.Gson;
import org.ecommerce.model.Order;
import org.ecommerce.repository.OrderRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/admin/orders")
public class AdminOrderApiServlet extends HttpServlet {

    private OrderRepository orderRepo = new OrderRepository();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        
        try {
            List<Order> orders = orderRepo.findAllForAdmin();
            res.getWriter().write(gson.toJson(orders));
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"error\": \"Error interno al cargar los pedidos\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        
        try {
            Order updatedData = gson.fromJson(req.getReader(), Order.class);
            orderRepo.updateStatus(updatedData.getId(), updatedData.getStatus());
            res.getWriter().write("{\"message\": \"Estado actualizado correctamente\"}");
        } catch (Exception e) {
            e.printStackTrace();
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"error\": \"Error al actualizar el estado\"}");
        }
    }
}