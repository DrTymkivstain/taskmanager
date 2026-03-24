package com.example.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TaskResponseDto {
    private Long id;
    private String title;
    private boolean completed;

}
