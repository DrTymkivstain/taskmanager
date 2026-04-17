package com.example.taskmanager.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {
    private Long id;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;
}
