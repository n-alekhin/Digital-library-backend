package com.springproject.core.exceptions;

public class InvalidAuthException extends RuntimeException{
    public InvalidAuthException(String message) {
        super(message);
    }
}
