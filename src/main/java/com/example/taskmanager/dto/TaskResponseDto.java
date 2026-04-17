package com.example.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private String status;
    String createdAt;
    String updatedAt;
}
