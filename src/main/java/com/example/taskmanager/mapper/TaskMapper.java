package com.example.taskmanager.mapper;

import com.example.taskmanager.dto.TaskRequestDto;
import com.example.taskmanager.dto.TaskResponseDto;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskStatus;

import java.time.format.DateTimeFormatter;

public class TaskMapper {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
                .createdAt(task.getCreationDate().format(ISO_FORMATTER  ))
                .updatedAt(task.getModificationDate().format(ISO_FORMATTER))
                .build();
    }
}
