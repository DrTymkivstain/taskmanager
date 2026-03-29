package com.example.taskmanager.controller;

import com.example.taskmanager.dao.impl.TaskDaoJdbcImpl;
import com.example.taskmanager.dto.TaskRequestDto;
import com.example.taskmanager.dto.TaskResponseDto;
import com.example.taskmanager.dto.TaskUpdateRequest;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.services.TaskService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
@WebServlet("/tasks/*")
public class TaskServlet extends AbstractServlet {
    private final TaskService  taskService = new TaskService(new TaskDaoJdbcImpl());


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        TaskRequestDto request = getRequestDto(req, TaskRequestDto.class);
        Long userId = ((User)req.getAttribute("currentUser")).getId();
        TaskResponseDto taskResponseDto = taskService.create(request, userId);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        sendJson(resp, taskResponseDto);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        Long id = extractId(req);
        TaskRequestDto taskRequestDto = getRequestDto(req, TaskRequestDto.class);
        TaskResponseDto taskResponseDto = taskService.update(id, taskRequestDto);
        resp.setStatus(HttpServletResponse.SC_OK);
        sendJson(resp, taskResponseDto);
    }



    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        Long id = extractId(req);
        taskService.delete(id);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private static TaskResponseDto toResponse(Task updated) {
        return new TaskResponseDto(
                updated.getId(),
                updated.getTitle(),
                updated.isCompleted());
    }


    protected void handleGetAll(HttpServletResponse resp) {
        List<TaskResponseDto> tasks = taskService.getAll();
        sendJson(resp, tasks);
    }

    protected void handleGetById(HttpServletResponse resp, Long id) {
        TaskResponseDto taskResponseDto = taskService.getById(id);
        sendJson(resp, taskResponseDto);
    }

}
