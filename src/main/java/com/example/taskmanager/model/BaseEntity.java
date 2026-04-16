package com.example.taskmanager.model;

import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {
    private Long id;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
}
