package com.example.taskmanager.exception;

public class AlreadyExistException extends AppException{

    public static final int STATUS_CODE = 409;

    public AlreadyExistException(String message) {
        super(STATUS_CODE, message);
    }
}
