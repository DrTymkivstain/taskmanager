package com.example.taskmanager.dto;

import lombok.*;

@Getter
@Setter
public class TaskRequestDto {
    private String title;
    private Boolean completed;
}
