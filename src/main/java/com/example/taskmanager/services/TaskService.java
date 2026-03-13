package com.example.taskmanager.services;

import com.example.taskmanager.dao.TaskDao;
import com.example.taskmanager.model.Task;

import java.util.List;
import java.util.NoSuchElementException;

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
        return taskDao.getById(id).orElseThrow();
    }

    public void delete(Long id) {
        taskDao.delete(id);
    }
    public Task update(Long id, Task task) {
        Task oldtask = taskDao
                .getById(id)
                .orElseThrow(() -> new NoSuchElementException("task not found"));
        oldtask.setCompleted(task.isCompleted());
        oldtask.setTitle(task.getTitle());

        Task updatedTask = taskDao.update(task);
        return updatedTask;
    }
}
