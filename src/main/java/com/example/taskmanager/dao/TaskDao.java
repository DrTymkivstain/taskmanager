package com.example.taskmanager.dao;

import com.example.taskmanager.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskDao {
    Task create(Task task);

    List<Task> getAll();

    Optional<Task> getById(Long id);

    int delete(Long id);

    int update(Task task);
}
