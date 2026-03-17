package com.example.taskmanager.controller;

import com.example.taskmanager.dao.impl.UserDaoJdbcImpl;
import com.example.taskmanager.dto.UserRequestDto;
import com.example.taskmanager.dto.UserResponseDto;
import com.example.taskmanager.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/users/*")
public class UserServlet extends AbstractServlet {
    private final UserService userService = new UserService(new UserDaoJdbcImpl());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(resp, () -> {
            UserRequestDto userRequestDto = getUserRequestDto(req);
            UserResponseDto userResponseDto = userService.createUser(userRequestDto);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            sendJson(resp, userResponseDto);
        });
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(resp, () -> {
            Long id = extractId(req);
            UserRequestDto userRequestDto = getUserRequestDto(req);
            UserResponseDto userResponseDto = userService.updateUser(id, userRequestDto);
            resp.setStatus(HttpServletResponse.SC_OK);
            sendJson(resp, userResponseDto);
        });
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest(resp, () -> {
            Long id = extractId(req);
            userService.deleteUser(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        });
    }

    @Override
    protected void handleGetById(HttpServletResponse resp, Long id) throws IOException {
        UserResponseDto userResponseDto = userService.getById(id);
        sendJson(resp, userResponseDto);
    }

    @Override
    protected void handleGetAll(HttpServletResponse resp) throws Exception {
        List<UserResponseDto> users = userService.getAll();
        sendJson(resp, users);
    }
}
