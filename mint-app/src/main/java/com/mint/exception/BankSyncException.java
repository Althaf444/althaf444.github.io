package com.mint.exception;

/**
 * Exception thrown when synchronization operations fail
 */
public class BankSyncException extends RuntimeException {
    public BankSyncException(String message) {
        super(message);
    }

    public BankSyncException(String message, Throwable cause) {
        super(message, cause);
    }
}

