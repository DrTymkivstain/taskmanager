package com.example.taskmanager.controller;

import com.example.taskmanager.dto.UserRequestDto;
import com.example.taskmanager.exception.AppException;
import com.example.taskmanager.exception.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public abstract class AbstractServlet extends HttpServlet {
    protected final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(resp, () ->{
            Long id = extractId(req);

            if (id == null) {
                handleGetAll(resp);
                return;
            }

            handleGetById(resp, id);
        });
    }

    protected abstract void handleGetById(HttpServletResponse resp, Long id) throws Exception;

    protected abstract void handleGetAll(HttpServletResponse resp) throws Exception;

    protected void sendJson(HttpServletResponse resp, Object body) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(mapper.writeValueAsString(body));
    }

    @FunctionalInterface
    protected interface ServletLogic {
        void execute() throws Exception;
    }

    protected void handleRequest(HttpServletResponse resp, ServletLogic logic) {
        try {
            logic.execute();

        } catch (AppException e) {
            resp.setStatus(e.getStatusCode());
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

    protected Long extractId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }

        try {
            return Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid ID format: " + pathInfo.substring(1));;
        }
    }

    protected UserRequestDto getUserRequestDto(HttpServletRequest req) throws IOException {
        return mapper.readValue(req.getReader(), UserRequestDto.class);
    }


}
