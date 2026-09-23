package com.klu.springmvc.exception;

public class ItemUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ItemUnavailableException(String message) {
        super(message);
    }
}

