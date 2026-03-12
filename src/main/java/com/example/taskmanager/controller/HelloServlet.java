package com.example.taskmanager.controller;

import com.example.taskmanager.dto.HelloRequest;
import com.example.taskmanager.dto.HelloResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HelloResponse response = new HelloResponse("Hello");
        resp.setContentType("application/json");
        String json = objectMapper.writeValueAsString(response);

        resp.getWriter().write(json);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();

        HelloRequest request = mapper.readValue(req.getInputStream(), HelloRequest.class);
        HelloResponse response = new HelloResponse("Hello" + request.getName());
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getWriter().write(mapper.writeValueAsString(response));
    }
}
