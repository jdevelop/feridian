package com.echomine.common;

/**
 * Whenever a message being sent didn't go through, this exception is thrown.
 */
public class SendMessageFailedException extends Exception {
    public SendMessageFailedException() {
    }

    public SendMessageFailedException(String message) {
        super(message);
    }

    public SendMessageFailedException(Throwable cause) {
        super(cause);
    }

    public SendMessageFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
