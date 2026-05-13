package org.ecommerce.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create();

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    public static void sendJson(HttpServletResponse response, int status, Object data) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(GSON.toJson(data));
    }

    public static void sendError(HttpServletResponse response, int status, String message) throws IOException {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        sendJson(response, status, error);
    }

    public static void sendSuccess(HttpServletResponse response, String message) throws IOException {
        Map<String, String> ok = new HashMap<>();
        ok.put("message", message);
        sendJson(response, 200, ok);
    }
}
