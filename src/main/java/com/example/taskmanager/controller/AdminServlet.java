package com.example.taskmanager.controller;

import com.example.taskmanager.dao.impl.TaskDaoJdbcImpl;
import com.example.taskmanager.dao.impl.UserDaoJdbcImpl;
import com.example.taskmanager.dto.UserRequestDto;
import com.example.taskmanager.dto.UserResponseDto;
import com.example.taskmanager.exception.AppException;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.services.TaskService;
import com.example.taskmanager.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebServlet("/admin/*")
public class AdminServlet extends AbstractServlet {
    private final UserService userService;
    private final TaskService taskService;

    public AdminServlet() {
        taskService = new TaskService(new TaskDaoJdbcImpl());
        userService = new UserService(new UserDaoJdbcImpl());
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        Long id = extractIdFromPath(req);
        check(req, id);
        userService.deleteUser(id);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private static void check(HttpServletRequest req, Long id) {
        User user = (User) req.getAttribute("currentUser");
        if(id.equals(user.getId())) { throw new AppException(400, "Administrators cannot change their own role or delete their account");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        Long userId = extractIdFromPath(req);
        check(req, userId);
        UserRequestDto userRequestDto = getRequestDto(req, UserRequestDto.class);
        String roleParam = req.getParameter("role");
        Role newRole = (roleParam != null) ? Role.valueOf(roleParam.toUpperCase()) : null;
        UserResponseDto responseDto = userService.updateUserByAdmin(userId, userRequestDto, newRole);
        resp.setStatus(HttpServletResponse.SC_OK);
        sendJson(resp, responseDto);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        super.doGet(req, resp);
    }

    @Override
    protected void handleGetById(HttpServletResponse resp, Long id, Long userId) {

    }

    @Override
    protected void handleGetAll(HttpServletResponse resp, Long userId) {

    }
}
