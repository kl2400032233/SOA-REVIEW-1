package com.klu.springmvc.exception;

public class UnauthorizedOrderAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedOrderAccessException(String message) {
        super(message);
    }
}
