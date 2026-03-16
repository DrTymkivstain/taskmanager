package com.example.taskmanager.services;

import com.example.taskmanager.dao.TaskDao;
import com.example.taskmanager.model.Task;

import java.util.List;

public class TaskService {
    private final TaskDao taskDao;


    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public Task create(Task task) {
        return taskDao.create(task);
    }

    public List<Task> getAll() {
        return taskDao.getAll();
    }

    public Task getById(Long id) {
        return taskDao.getById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public void delete(Long id) {
        taskDao.delete(id);
    }
    public Task update(Long id, Task task) {
        return taskDao.update(id, task);
    }
}
