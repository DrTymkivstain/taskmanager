package com.example.taskmanager.model;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Task extends BaseEntity {
    private String title;
    private String description;
    private TaskStatus status;
    private Long userId;
}
