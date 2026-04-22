package com.example.taskmanager.dao;

import com.example.taskmanager.model.Task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TaskDao {
    Task create(Task task);

    List<Task> getTasksByUserId(Long userId, int limit, int offset, String sortBy, String sortOrder);

    List<Task> getTasksByUserId(Long userId);

    Optional<Task> getById(Long id, Long userId);

    int delete(Long id, Long userId);

    int update(Task task);

    void softDeleteAllTasksByUserId(Long userId, Connection connection) throws SQLException;

    long countTasksByUserId(Long userId);
}
