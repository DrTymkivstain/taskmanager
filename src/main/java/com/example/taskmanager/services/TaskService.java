package com.example.taskmanager.services;

import com.example.taskmanager.dao.TaskDao;
import com.example.taskmanager.dto.PageRequestDto;
import com.example.taskmanager.dto.PageResponseDto;
import com.example.taskmanager.dto.TaskRequestDto;
import com.example.taskmanager.dto.TaskResponseDto;
import com.example.taskmanager.exception.EntityNotFoundException;
import com.example.taskmanager.mapper.TaskMapper;
import com.example.taskmanager.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class TaskService {
    private final TaskDao taskDao;
    private final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private static final Map<String, String> SORT_COLUMNS = Map.of("title", "title",
            "description", "description",
            "createdAt", "created_at",
            "updatedAt", "updated_at",
            "id", "id",
            "status", "CASE " +
            "  WHEN status = 'NOT_STARTED' THEN 1 " +
            "  WHEN status = 'IN_PROGRESS' THEN 2 " +
            "  WHEN status = 'COMPLETED' THEN 3 " +
            "  ELSE 4 END");

    public TaskService(TaskDao taskDao) {
        this.taskDao = taskDao;
    }


    public TaskResponseDto create(TaskRequestDto taskRequestDto, Long userId) {
        Task task = TaskMapper.toTask(taskRequestDto, userId);
        Task created = taskDao.create(task);
        logger.info("Task created with id: {} by user: {}", created.getId(), created.getUserId());
        return TaskMapper.toTaskResponseDto(created);
    }

    public PageResponseDto<TaskResponseDto> getTasksByUserId(Long userId, PageRequestDto  pageRequestDto) {
        logger.debug("Getting tasks by userId: {}", userId);

        String sortColumn = SORT_COLUMNS.getOrDefault(pageRequestDto.getSortBy(), "title");
        int limit = pageRequestDto.getSize();
        int offset = (pageRequestDto.getPage() - 1) * limit;

        List<TaskResponseDto> tasks = taskDao.getTasksByUserId(userId, limit, offset, sortColumn, pageRequestDto.getSortOrder()).stream()
                .map(TaskMapper::toTaskResponseDto)
                .toList();

        long totalElements = taskDao.countTasksByUserId(userId);
        PageResponseDto<TaskResponseDto> resp = new PageResponseDto<>(tasks, pageRequestDto.getPage(), pageRequestDto.getSize(), totalElements);
        logger.info("Successfully fetched {} tasks for user ID: {}", tasks.size(), userId);
        return resp;
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
        Task fromDb = taskDao.getById(id,userId).orElseThrow(() -> new EntityNotFoundException("Task not found or access denied!"));
        if(taskRequestDto.getTitle() != null) {
            fromDb.setTitle(taskRequestDto.getTitle());
        }
        if(taskRequestDto.getDescription() != null) {
            fromDb.setDescription(taskRequestDto.getDescription());
        }
        if(taskRequestDto.getDescription() != null) {
            fromDb.setDescription(taskRequestDto.getDescription());
        }
        int updated = taskDao.update(fromDb);
        if(updated == 0) {throw new EntityNotFoundException("Task not found  or access denied");}
        logger.info("Successfully updated task by id: {} by user: {}", id, userId);
        return TaskMapper.toTaskResponseDto(fromDb);
    }
}
