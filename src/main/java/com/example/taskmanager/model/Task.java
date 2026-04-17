package com.example.taskmanager.model;

import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Task extends BaseEntity {
    private String title;
    private String description;
    private TaskStatus status;
    private Long userId;
}
