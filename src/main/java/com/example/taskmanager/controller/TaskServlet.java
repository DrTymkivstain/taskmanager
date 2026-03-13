package com.example.taskmanager.controller;

import com.example.taskmanager.dao.impl.TaskDaoImpl;
import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.dto.TaskUpdateRequest;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.services.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
@WebServlet("/tasks/*")
public class TaskServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final TaskService  taskService = new TaskService(new TaskDaoImpl());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Long id = extractId(req);

        if (id == null) {
            handleGetAll(resp);
            return;
        }

        handleGetById(resp, id);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TaskRequest request = mapper.readValue(req.getInputStream(), TaskRequest.class);

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setCompleted(false);
        Task created = taskService.create(task);
        TaskResponse response = new TaskResponse(
                created.getId(),
                created.getTitle(),
                created.isCompleted());
        sendJson(resp, response);
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = extractId(req);
        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;}
        TaskUpdateRequest request = mapper.readValue(req.getInputStream(), TaskUpdateRequest.class);
        Task task = new Task();
        task.setId(id);
        task.setTitle(request.getTitle());
        task.setCompleted(request.isCompleted());
        try {
            Task updated = taskService.update(id, task);
            TaskResponse response = new TaskResponse(
                    updated.getId(),
                    updated.getTitle(),
                    updated.isCompleted());
            resp.setStatus(HttpServletResponse.SC_OK);
            sendJson(resp, response);
        } catch (NoSuchElementException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long id = extractId(req);

        if(id == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
           taskService.delete(id);
           resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    private Long extractId(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }

        try {
            return Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }


    private void handleGetAll(HttpServletResponse resp) throws IOException {
        List<TaskResponse> tasks = taskService.getAll()
                .stream()
                .map(task -> new TaskResponse(
                        task.getId(),
                        task.getTitle(),
                        task.isCompleted()))
                .collect(Collectors.toList());

        sendJson(resp, tasks);
    }

    private void handleGetById(HttpServletResponse resp, Long id) throws IOException {
        try {
            Task task = taskService.getById(id);

            TaskResponse response = new TaskResponse(
                    task.getId(),
                    task.getTitle(),
                    task.isCompleted()
            );

            sendJson(resp, response);

        } catch (NoSuchElementException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    private void sendJson(HttpServletResponse resp, Object body) throws IOException {
        resp.setContentType("application/json");
        resp.getWriter().write(mapper.writeValueAsString(body));
    }
}
