package com.example.taskmanager.exception;

public class EntityNotFoundException extends AppException{

    public static final int STATUS_CODE = 404;

    public EntityNotFoundException(String message) {
        super(STATUS_CODE, message);
    }
}
