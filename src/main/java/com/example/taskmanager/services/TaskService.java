package com.example.taskmanager.services;

import com.example.taskmanager.dao.impl.TaskDaoJdbcImpl;
import com.example.taskmanager.dto.TaskRequestDto;
import com.example.taskmanager.dto.TaskResponseDto;
import com.example.taskmanager.exception.EntityNotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.model.Task;

import java.util.List;

public class TaskService {
    private final TaskDaoJdbcImpl taskDao;
    public TaskService(TaskDaoJdbcImpl taskDao) {
        this.taskDao = taskDao;
    }


    public TaskResponseDto create(TaskRequestDto taskRequestDto, Long userId) {
        Task task = TaskMapper.toTask(taskRequestDto, userId);
        return TaskMapper.toTaskResponseDto(taskDao.create(task));
    }

    public List<TaskResponseDto> getTasksByUserId(Long userId) {
        return taskDao.getTasksByUserId(userId).stream()
                .map(TaskMapper::toTaskResponseDto)
                .toList();
    }

    public TaskResponseDto getById(Long id, Long userId) {
        return TaskMapper.toTaskResponseDto(taskDao.getById(id, userId).orElseThrow(() -> new EntityNotFoundException("Task not found or access denied!")));
    }

    public void delete(Long id, Long userId) {
        int deleteCount = taskDao.delete(id, userId);
        if(deleteCount == 0) {throw new EntityNotFoundException("Task not found or access denied");}
    }

    public TaskResponseDto update(Long id, TaskRequestDto taskRequestDto, Long userId) {
        getById(id, userId);

        Task task = TaskMapper.toTask(taskRequestDto, userId);
        task.setId(id);
        int updated = taskDao.update(task);
        if(updated == 0) {throw new EntityNotFoundException("Task not found  or access denied");}
        return TaskMapper.toTaskResponseDto(task);
    }
}
