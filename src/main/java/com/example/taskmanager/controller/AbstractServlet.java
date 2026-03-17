package com.example.taskmanager.controller;

import com.example.taskmanager.exception.AppException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public abstract class AbstractServlet extends HttpServlet {
    protected final ObjectMapper mapper = new ObjectMapper();

    // Твій метод, трохи підправлений
    protected void sendJson(HttpServletResponse resp, Object body) throws IOException {
        resp.setContentType("application/json; charset=UTF-8"); // Додали кодування
        resp.getWriter().write(mapper.writeValueAsString(body));
    }

    @FunctionalInterface
    protected interface ServletLogic {
        void execute() throws Exception;
    }
    // Хендлер, який використовує sendJson для помилок
    protected void handleRequest(HttpServletResponse resp, ServletLogic logic) {
        try {
            logic.execute();

        } catch (AppException e) {
            resp.setStatus(e.getStatusCode());
            // Використовуємо твій метод для відправки помилки в JSON
            try {
                sendJson(resp, Map.of("error", e.getMessage()));
            } catch (IOException io) { io.printStackTrace(); }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(500);
            try {
                sendJson(resp, Map.of("error", "Internal Server Error: " + e.getMessage()));
            } catch (IOException io) { io.printStackTrace(); }
        }
    }
}
