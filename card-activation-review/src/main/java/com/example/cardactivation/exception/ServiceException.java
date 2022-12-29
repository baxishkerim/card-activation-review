package com.example.cardactivation.exception;


import lombok.Getter;
public class ServiceException extends RuntimeException {

    @Getter
    private static final int code = 21000;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }
}
