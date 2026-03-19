package com.example.accountmanager.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public static final String MESSAGE = "A customer with this email already exists.";
    public EmailAlreadyExistsException() {
        super(MESSAGE);
    }
}
