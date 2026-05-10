package com.mint.exception;

/**
 * Exception thrown when connection timeout occurs
 */
public class ConnectionTimeoutException extends RuntimeException {
    public ConnectionTimeoutException(String message) {
        super(message);
    }

    public ConnectionTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}

