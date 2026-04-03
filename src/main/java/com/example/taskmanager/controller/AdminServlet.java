package com.example.taskmanager.controller;

import com.example.taskmanager.dao.impl.TaskDaoJdbcImpl;
import com.example.taskmanager.dao.impl.UserDaoJdbcImpl;
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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
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
