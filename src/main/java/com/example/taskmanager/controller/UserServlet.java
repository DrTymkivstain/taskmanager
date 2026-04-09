package com.example.taskmanager.controller;

import com.example.taskmanager.dao.impl.UserDaoJdbcImpl;
import com.example.taskmanager.dto.UserRequestDto;
import com.example.taskmanager.dto.UserResponseDto;
import com.example.taskmanager.services.UserService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@WebServlet("/users/*")
public class UserServlet extends AbstractServlet {
    private final UserService userService = new UserService(new UserDaoJdbcImpl());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        UserRequestDto userRequestDto = getRequestDto(req, UserRequestDto.class);
        UserResponseDto userResponseDto = userService.createUser(userRequestDto);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        sendJson(resp, userResponseDto);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        Long id = getUserFromRequest(req).getId();
        UserRequestDto userRequestDto = getRequestDto(req, UserRequestDto.class);
        UserResponseDto userResponseDto = userService.updateCurrentUser(id, userRequestDto);
        resp.setStatus(HttpServletResponse.SC_OK);
        sendJson(resp, userResponseDto);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        Long id = getUserFromRequest(req).getId();
        userService.deleteUser(id);
        req.getSession().invalidate();
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        Long userId = getUserFromRequest(req).getId();
        Long id = extractIdFromPath(req);

        if (id == null) {
            handleGetAll(resp, userId);
            return;
        }

        handleGetById(resp, id, userId);
    }

    private void handleGetById(HttpServletResponse resp, Long id, Long userId) {
        UserResponseDto userResponseDto = userService.getById(id);
        sendJson(resp, userResponseDto);
    }

    private void handleGetAll(HttpServletResponse resp, Long userId) {
        List<UserResponseDto> users = userService.getAll();
        sendJson(resp, users);
    }
}
