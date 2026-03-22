package com.example.taskmanager.exception;

public class UnAuthorizedException extends AppException{

    public static final int STATUS_CODE = 401;

    public UnAuthorizedException(String message) {
        super(STATUS_CODE, message);
    }
}
