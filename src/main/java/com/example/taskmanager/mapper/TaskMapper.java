package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.TaskRequestDto;
import com.example.taskmanager.dto.TaskResponseDto;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;

public class TaskMapper {
    public static Task toTask(TaskRequestDto taskRequestDto, Long userId) {
        return Task.builder()
                .title(taskRequestDto.getTitle())
                .description(taskRequestDto.getDescription())
                .status(TaskStatus.fromString(taskRequestDto.getStatus()))
                .userId(userId)
                .build();
    }

    public static TaskResponseDto toTaskResponseDto(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().getTitle())
                .build();
    }
}
