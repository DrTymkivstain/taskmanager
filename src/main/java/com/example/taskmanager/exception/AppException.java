package com.example.taskmanager.exception;

import lombok.Getter;

@Getter
public abstract class AppException extends RuntimeException{
    private final int statusCode;

    public AppException(int statusCode, String message){
        super(message);
        this.statusCode = statusCode;
    }
}
