package com.example.taskmanager.dao;

import com.example.taskmanager.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskDao {
    Task create(Task task);

    List<Task> getTasksByUserId(Long userId);

    Optional<Task> getById(Long id, Long userId);

    int delete(Long id, Long userId);

    int update(Task task);
}
