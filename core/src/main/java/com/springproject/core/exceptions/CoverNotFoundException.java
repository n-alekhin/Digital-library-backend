package com.springproject.core.exceptions;

public class CoverNotFoundException extends RuntimeException {
    public CoverNotFoundException(String message) {
        super(message);
    }
}
