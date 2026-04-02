package com.example.taskmanager.controller;

import com.example.taskmanager.exception.AppException;
import com.example.taskmanager.exception.ValidationException;
import com.example.taskmanager.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public abstract class AbstractServlet extends HttpServlet {
    protected final ObjectMapper mapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("Incoming request: {} {}", req.getMethod(), req.getRequestURI());
       handleRequest(resp, () -> {super.service(req, resp);});
    }

    @FunctionalInterface
    protected interface ServletLogic {
        void execute() throws ServletException, IOException;
    }

    protected void handleRequest(HttpServletResponse resp, ServletLogic logic) {
        try {
            logic.execute();

        } catch (AppException e) {
            logger.warn("Business error: status {}, message: {}", e.getStatusCode(), e.getMessage());
            resp.setStatus(e.getStatusCode());
            sendJson(resp, Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("SYSTEM ERROR: ", e);
            resp.setStatus(500);
            sendJson(resp, Map.of("error", "Internal Server Error: " + e.getMessage()));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        Long userId = ((User) req.getAttribute("CurrentUserId")).getId();
        Long id = extractId(req);

        if (id == null) {
            handleGetAll(resp, userId);
            return;
        }

        handleGetById(resp, id, userId);
    }

    protected abstract void handleGetById(HttpServletResponse resp, Long id, Long userId);

    protected abstract void handleGetAll(HttpServletResponse resp, Long userId);

    protected void sendJson(HttpServletResponse resp, Object body) {
        try {
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write(mapper.writeValueAsString(body));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Long extractId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }

        try {
            return Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid ID format: " + pathInfo.substring(1));
        }
    }

    protected <T> T getRequestDto(HttpServletRequest req, Class<T> clazz) {
        try {
            return mapper.readValue(req.getReader(), clazz);
        } catch (IOException e) {
            throw new ValidationException("Invalid JSON format: " + e.getMessage());
        }
    }
}
