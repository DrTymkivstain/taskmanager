package com.example.taskmanager.services;

import com.example.taskmanager.dao.TaskDao;
import com.example.taskmanager.dto.TaskRequestDto;
import com.example.taskmanager.dto.TaskResponseDto;
import com.example.taskmanager.exception.EntityNotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TaskService {
    private final TaskDao taskDao;
    private final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }


    public TaskResponseDto create(TaskRequestDto taskRequestDto, Long userId) {
        Task task = TaskMapper.toTask(taskRequestDto, userId);
        Task created = taskDao.create(task);
        logger.info("Task created with id: {} by user: {}", created.getId(), created.getUserId());
        return TaskMapper.toTaskResponseDto(created);
    }

    public List<TaskResponseDto> getTasksByUserId(Long userId) {
        logger.debug("Getting tasks by userId: {}", userId);
        List<TaskResponseDto> tasks = taskDao.getTasksByUserId(userId).stream()
                .map(TaskMapper::toTaskResponseDto)
                .toList();
        logger.info("Successfully fetched {} tasks for user ID: {}", tasks.size(), userId);
        return tasks;
    }

    public TaskResponseDto getById(Long id, Long userId) {
        logger.debug("Getting task by id: {} by user: {}", id, userId);
        return TaskMapper.toTaskResponseDto(taskDao.getById(id, userId).orElseThrow(() -> new EntityNotFoundException("Task not found or access denied!")));
    }

    public void delete(Long id, Long userId) {
        logger.debug("Deleting task by id: {} by user: {}", id, userId);
        int deleteCount = taskDao.delete(id, userId);
        if(deleteCount == 0) {throw new EntityNotFoundException("Task not found or access denied");}
        logger.info("Successfully deleted task by id: {} by user: {}", id, userId);
    }

    public TaskResponseDto update(Long id, TaskRequestDto taskRequestDto, Long userId) {
        logger.debug("Updating task by id: {} by user: {}", id, userId);

        Task task = TaskMapper.toTask(taskRequestDto, userId);
        task.setId(id);
        int updated = taskDao.update(task);
        if(updated == 0) {throw new EntityNotFoundException("Task not found  or access denied");}
        logger.info("Successfully updated task by id: {} by user: {}", id, userId);
        return TaskMapper.toTaskResponseDto(task);
    }
}
