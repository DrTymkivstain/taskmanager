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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private  ObjectMapper mapper;
    private  UserService userService;
    private  Logger logger;

    @Override
    public void init() {
        this.mapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
        this.logger = LoggerFactory.getLogger(getClass());
        this.userService = (UserService) getServletContext().getAttribute("userService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            LoginRequestDto loginRequestDto = mapper.readValue(req.getReader(), LoginRequestDto.class);

            User user = userService.authenticate(loginRequestDto.getEmail(), loginRequestDto.getPassword());

            HttpSession session = req.getSession(true);
            session.setMaxInactiveInterval(1800);
            session.setAttribute("user", user);
            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), "Welcome");
            logger.info("Welcome user with email: {}", user.getEmail());
        } catch (Exception e) {
            logger.error(e.getMessage());

            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            mapper.writeValue(resp.getWriter(), Collections.singletonMap("error", "Invalid email or password"));
        }
    }
}
