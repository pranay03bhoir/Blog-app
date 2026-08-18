package com.substring.blogapp.exceptions;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException() {
        super("Already Exists");
    }

    public AlreadyExistsException(String message) {
        super(message);
    }
}
