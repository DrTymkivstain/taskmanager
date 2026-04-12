package com.example.taskmanager.config;

import com.example.taskmanager.dao.TaskDao;
import com.example.taskmanager.dao.UserDao;
import com.example.taskmanager.dao.impl.TaskDaoJdbcImpl;
import com.example.taskmanager.dao.impl.UserDaoJdbcImpl;
import com.example.taskmanager.services.TaskService;
import com.example.taskmanager.services.UserService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ServletContextListener implements jakarta.servlet.ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        TaskDao taskDao = new TaskDaoJdbcImpl();
        UserDao userDao = new UserDaoJdbcImpl();

        UserService userService = new UserService(userDao);
        TaskService taskService = new TaskService(taskDao);
        ServletContext context = sce.getServletContext();
        context.setAttribute("taskDao", taskDao);
        context.setAttribute("userDao", userDao);
        context.setAttribute("userService", userService);
        context.setAttribute("taskService", taskService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        jakarta.servlet.ServletContextListener.super.contextDestroyed(sce);
    }
}
