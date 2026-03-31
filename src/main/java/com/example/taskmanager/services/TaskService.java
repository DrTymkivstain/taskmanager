package com.example.taskmanager.services;

import com.example.taskmanager.dao.impl.TaskDaoJdbcImpl;
import com.example.taskmanager.dto.TaskRequestDto;
import com.example.taskmanager.dto.TaskResponseDto;
import com.example.taskmanager.exception.EntityNotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;

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

    public List<TaskResponseDto> getAll() {
        return taskDao.getAll().stream()
                .map(TaskMapper::toTaskResponseDto)
                .toList();
    }

    public TaskResponseDto getById(Long id) {
        return TaskMapper.toTaskResponseDto(taskDao.getById(id).orElseThrow(() -> new EntityNotFoundException("Task not found")));
    }

    public void delete(Long id) {
        int deleteCount = taskDao.delete(id);
        if(deleteCount == 0) {throw new EntityNotFoundException("Task not found");}
    }
    public TaskResponseDto update(Long id, TaskRequestDto taskRequestDto) {
        getById(id);

        Task task = TaskMapper.toTask(taskRequestDto, id);
        int updated = taskDao.update(task);
        if(updated == 0) {throw new EntityNotFoundException("User not found");}
        return TaskMapper.toTaskResponseDto(task);
    }
}
