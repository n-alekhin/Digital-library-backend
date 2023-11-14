package com.springproject.core.exceptions;

public class InvalidBookTypeException extends RuntimeException {
    public InvalidBookTypeException(String message) {
        super(message);
    }
}
