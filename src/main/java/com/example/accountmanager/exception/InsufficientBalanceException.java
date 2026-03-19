package com.example.accountmanager.exception;

public class InsufficientBalanceException extends RuntimeException {
    public static final String MESSAGE = "Transaction failed. Insufficient funds on the account.";
    public InsufficientBalanceException() {
        super(MESSAGE);
    }
}
