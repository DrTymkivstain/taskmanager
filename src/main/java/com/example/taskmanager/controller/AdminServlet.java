package com.example.taskmanager.controller;

import com.example.taskmanager.dto.*;
import com.example.taskmanager.exception.AppException;
import com.example.taskmanager.model.Role;
import com.example.taskmanager.model.User;
import com.example.taskmanager.services.TaskService;
import com.example.taskmanager.services.UserService;
import com.example.taskmanager.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@WebServlet("/admin/*")
public class AdminServlet extends AbstractServlet {
    public static final String USERS_PATH = "users";
    public static final String TASKS_PATH = "tasks";
    private UserService userService;
    private TaskService taskService;


    @Override
    public void init() {
        super.init();
        this.userService = (UserService) getServletContext().getAttribute("userService");
        this.taskService = (TaskService) getServletContext().getAttribute("taskService");
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
        JsonUtil.sendJson(resp, responseDto);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {

            String pathInfo = req.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                throw new AppException(400, "Resource not specified");
            }

            String[] parts = pathInfo.split("/");
            String resource = parts[1];
            boolean hasId = parts.length > 2;

            if (USERS_PATH.equals(resource)) {
                if (hasId) {
                    Long id = extractIdFromPath(req);
                    UserWithTasksResponseDto user = userService.getUserWithTasks(id);
                    JsonUtil.sendJson(resp, user);
                    return;
                }
                List<UserResponseDto> userResponseDto = userService.getAll();
                JsonUtil.sendJson(resp, userResponseDto);
                return;
            }

            if (TASKS_PATH.equals(resource)) {
                String queryParam = req.getParameter("userId");
                PageRequestDto pageRequestDto = PageRequestDto.createPageRequestDto(req);
                if (queryParam != null) {
                    Long userId = Long.parseLong(queryParam);
                    PageResponseDto<TaskResponseDto> tasks = taskService.getTasksByUserId(userId, pageRequestDto);
                    JsonUtil.sendJson(resp, tasks);
                    return;
                }
                throw new AppException(400, "Please specify userId to see their tasks: /admin/tasks?userId=15");
            }

            throw new AppException(404, "Not Found");
    }
}
