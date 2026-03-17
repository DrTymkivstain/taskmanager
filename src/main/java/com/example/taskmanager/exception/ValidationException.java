package com.example.taskmanager.exception;

public class ValidationException extends AppException{

    public static final int STATUS_CODE = 400;

    public ValidationException(String message) {
        super(STATUS_CODE, message);
    }
}

