package com.example.taskmanager.services;

import com.example.taskmanager.dao.impl.TaskDaoJdbcImpl;
import com.example.taskmanager.dto.TaskRequestDto;
import com.example.taskmanager.dto.TaskResponseDto;
import com.example.taskmanager.exception.EntityNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;

import java.util.List;

public class TaskService {
    private final TaskDaoJdbcImpl taskDao;
    public TaskService(TaskDaoJdbcImpl taskDao) {
        this.taskDao = taskDao;
    }


    public TaskResponseDto create(TaskRequestDto taskRequestDto, Long userId) {
        Task task = new Task(null,
                taskRequestDto.getTitle(),
                taskRequestDto.getDescription(),
                TaskStatus.fromString(taskRequestDto.getStatus()),
                userId);
        return toDto(taskDao.create(task));
    }

    public List<TaskResponseDto> getAll() {
        return taskDao.getAll().stream()
                .map(TaskService::toDto)
                .toList();
    }

    public TaskResponseDto getById(Long id) {
        return toDto(taskDao.getById(id).orElseThrow(() -> new EntityNotFoundException("Task not found")));
    }

    public void delete(Long id) {
        int deleteCount = taskDao.delete(id);
        if(deleteCount == 0) {throw new EntityNotFoundException("Task not found");}
    }
    public TaskResponseDto update(Long id, TaskRequestDto taskRequestDto) {
        getById(id);

        Task task = new Task(id, taskRequestDto.getTitle(), taskRequestDto.getCompleted());
        int updated = taskDao.update(task);
        if(updated == 0) {throw new EntityNotFoundException("User not found");}
        return toDto(task);
    }

    private static TaskResponseDto toDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().getTitle());
    }
}
