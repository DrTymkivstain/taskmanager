package com.example.taskmanager.controller;

import com.example.taskmanager.exception.ValidationException;
import com.example.taskmanager.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractServlet extends HttpServlet {
    protected ObjectMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void init() {
        this.mapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("Incoming request: {} {}", req.getMethod(), req.getRequestURI());
    }

    protected Long extractIdFromPath(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }

        String[] pathParts = pathInfo.split("/");
        try {
            return Long.parseLong(pathParts[pathParts.length - 1]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            logger.warn("Could not extract ID from path: {}", pathInfo);
            return null;
        }
    }

    protected User getUserFromRequest(HttpServletRequest req) {
        return (User) req.getAttribute("currentUser");
    }

    protected <T> T getRequestDto(HttpServletRequest req, Class<T> clazz) {
        try {
            return mapper.readValue(req.getReader(), clazz);
        } catch (IOException e) {
            throw new ValidationException("Invalid JSON format: " + e.getMessage());
        }
    }
}
