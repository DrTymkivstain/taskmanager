package com.example.taskmanager.exception;

public class EntityNotFoundException extends AppException{
    public EntityNotFoundException(String message) {
        super(404, message);
    }
}
