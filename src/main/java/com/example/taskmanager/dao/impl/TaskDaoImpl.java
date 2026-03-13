package com.example.taskmanager.dao.impl;

import com.example.taskmanager.dao.TaskDao;
import com.example.taskmanager.model.Task;

import java.util.*;

public class TaskDaoImpl implements TaskDao {
    private final Map<Long, Task> storage =  new HashMap<Long, Task>();
    private Long idCounter = 1L;

    @Override
    public Task create(Task task) {
        task.setId(idCounter++);
        storage.put(task.getId(), task);
        return task;
    }

    @Override
    public List<Task> getAll() {
        return new ArrayList<Task>(storage.values());
    }

    @Override
    public Optional<Task> getById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }

    @Override
    public Task update(Task task) {
        storage.put(task.getId(), task);
        return storage.get(task.getId());
    }
}
