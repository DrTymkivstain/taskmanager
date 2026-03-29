package com.example.taskmanager.model;

import lombok.Getter;

@Getter
public enum TaskStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String title;

    TaskStatus(String title) {
        this.title = title;
    }

    public static TaskStatus fromString(String value) {
        if (value == null || value.isBlank()) return NOT_STARTED;

        String normalizedValue = value.trim().replace(" ", "_");

        for (TaskStatus status : TaskStatus.values()) {
            if (status.name().equalsIgnoreCase(normalizedValue)) {
                return status;
            }
        }
        return NOT_STARTED;
    }

}
