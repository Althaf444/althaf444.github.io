package com.mint.exception;

/**
 * Exception thrown when bank connection operations fail
 */
public class BankConnectionException extends RuntimeException {
    public BankConnectionException(String message) {
        super(message);
    }

    public BankConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}

