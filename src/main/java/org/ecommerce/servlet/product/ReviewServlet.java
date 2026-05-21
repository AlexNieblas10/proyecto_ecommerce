/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.ecommerce.servlet.product;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ecommerce.model.Review;
import org.ecommerce.repository.ReviewRepository;
import org.ecommerce.util.JsonUtil;

/**
 *
 * @author Gael
 */

public class ReviewServlet extends HttpServlet {

    private final ReviewRepository reviewRepo = new ReviewRepository();

    // Obtener reseñas de un producto
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String productIdStr = req.getParameter("productId");
        if (productIdStr == null) {
            JsonUtil.sendError(res, 400, "Se requiere productId");
            return;
        }
        try {
            int productId = Integer.parseInt(productIdStr);
            List<Review> reviews = reviewRepo.findByProductId(productId);
            JsonUtil.sendJson(res, 200, reviews);
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    // Crear o actualizar una reseña (Requiere AuthFilter)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Integer userId = (Integer) req.getAttribute("userId");
        if (userId == null) {
            JsonUtil.sendError(res, 401, "Debes iniciar sesión para dejar una reseña");
            return;
        }

        String body = req.getReader().lines().collect(Collectors.joining());
        JsonObject json = JsonUtil.fromJson(body, JsonObject.class);

        try {
            Review review = new Review();
            review.setUserId(userId);
            review.setProductId(json.get("productId").getAsInt());
            review.setRating(json.get("rating").getAsInt());
            review.setComment(json.has("comment") ? json.get("comment").getAsString() : "");
            
            reviewRepo.save(review);
            JsonUtil.sendSuccess(res, "Reseña guardada exitosamente");
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }

    // Eliminar una rese
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String userRole = (String) req.getAttribute("userRole");
        if (!"admin".equals(userRole)) {
            JsonUtil.sendError(res, 403, "Solo administradores pueden eliminar reseñas");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            JsonUtil.sendError(res, 400, "Especifica el ID de la reseña");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.replaceFirst("/", ""));
            reviewRepo.delete(id);
            JsonUtil.sendSuccess(res, "Reseña eliminada");
        } catch (Exception e) {
            JsonUtil.sendError(res, 500, e.getMessage());
        }
    }
}
