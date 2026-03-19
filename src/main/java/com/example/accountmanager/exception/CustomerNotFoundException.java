package com.example.accountmanager.exception;

public class CustomerNotFoundException extends RuntimeException {
    public static final String MESSAGE = "Customer not found. Try again with a different id ...";

    public CustomerNotFoundException() {
        super(MESSAGE);
    }
}
