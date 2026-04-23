package com.example.taskmanager.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JsonUtil {
    protected static ObjectMapper mapper = new ObjectMapper();

    public static void sendJson(HttpServletResponse resp, Object body) {
        try {
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write(mapper.writeValueAsString(body));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
