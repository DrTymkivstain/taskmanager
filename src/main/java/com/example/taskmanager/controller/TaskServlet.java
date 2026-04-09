package com.example.taskmanager.controller;

import com.example.taskmanager.dao.impl.TaskDaoJdbcImpl;
import com.example.taskmanager.dto.TaskRequestDto;
import com.example.taskmanager.dto.TaskResponseDto;
import com.example.taskmanager.model.User;
import com.example.taskmanager.services.TaskService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@WebServlet("/tasks/*")
public class TaskServlet extends AbstractServlet {
    private final TaskService taskService = new TaskService(new TaskDaoJdbcImpl());


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        TaskRequestDto request = getRequestDto(req, TaskRequestDto.class);
        Long userId = ((User) req.getAttribute("currentUser")).getId();
        TaskResponseDto taskResponseDto = taskService.create(request, userId);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        sendJson(resp, taskResponseDto);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        Long userId = ((User) req.getAttribute("currentUser")).getId();
        Long id = extractIdFromPath(req);

        TaskRequestDto taskRequestDto = getRequestDto(req, TaskRequestDto.class);
        TaskResponseDto taskResponseDto = taskService.update(id, taskRequestDto, userId);
        resp.setStatus(HttpServletResponse.SC_OK);
        sendJson(resp, taskResponseDto);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        Long userId = ((User) req.getAttribute("currentUser")).getId();
        Long id = extractIdFromPath(req);
        taskService.delete(id, userId);
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

    private void handleGetAll(HttpServletResponse resp, Long userId) {
        List<TaskResponseDto> tasks = taskService.getTasksByUserId(userId);
        sendJson(resp, tasks);
    }

    private void handleGetById(HttpServletResponse resp, Long id, Long userId) {
        TaskResponseDto taskResponseDto = taskService.getById(id, userId);
        sendJson(resp, taskResponseDto);
    }
}
