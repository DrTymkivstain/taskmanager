package com.example.taskmanager.config;

import com.example.taskmanager.dao.TaskDao;
import com.example.taskmanager.dao.UserDao;
import com.example.taskmanager.dao.impl.TaskDaoJdbcImpl;
import com.example.taskmanager.dao.impl.UserDaoJdbcImpl;
import com.example.taskmanager.services.TaskService;
import com.example.taskmanager.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.annotation.WebListener;
import lombok.Getter;


@WebListener
public class ServletContextListener implements jakarta.servlet.ServletContextListener {
    @Getter
    private static HikariDataSource dataSource;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ObjectMapper mapper = new ObjectMapper();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/taskmanager");
        config.setUsername("postgres");
        config.setPassword("1");
        config.setDriverClassName("org.postgresql.Driver");


        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(10000);
        config.setIdleTimeout(15000);

        dataSource = new HikariDataSource(config);

        TaskDao taskDao = new TaskDaoJdbcImpl(dataSource);
        UserDao userDao = new UserDaoJdbcImpl(dataSource);

        UserService userService = new UserService(userDao, taskDao, dataSource);
        TaskService taskService = new TaskService(taskDao);


        ServletContext context = sce.getServletContext();
        context.setAttribute("objectMapper", mapper);
        context.setAttribute("taskDao", taskDao);
        context.setAttribute("userDao", userDao);
        context.setAttribute("userService", userService);
        context.setAttribute("taskService", taskService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null) {
            dataSource.close();
        }
        jakarta.servlet.ServletContextListener.super.contextDestroyed(sce);
    }
}
