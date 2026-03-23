package com.example.taskmanager.controller;

import com.example.taskmanager.dao.impl.UserDaoJdbcImpl;
import com.example.taskmanager.dto.LoginRequestDto;
import com.example.taskmanager.model.User;
import com.example.taskmanager.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserService userService = new UserService(new UserDaoJdbcImpl());


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            LoginRequestDto loginRequestDto = mapper.readValue(req.getReader(), LoginRequestDto.class);

            User user = userService.authenticate(loginRequestDto.getEmail(), loginRequestDto.getPassword());

            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), "Welcome");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            mapper.writeValue(resp.getWriter(), Collections.singletonMap("error", "Invalid email or password"));
        }
    }
}
