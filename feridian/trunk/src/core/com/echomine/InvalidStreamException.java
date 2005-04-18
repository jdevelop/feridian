package com.echomine;

/**
 * This exception indicates that some error occurred during processing
 * of an IO stream.
 */
public class InvalidStreamException extends MuseException {
    public InvalidStreamException() {
        super();
    }

    public InvalidStreamException(String message) {
        super(message);
    }

    public InvalidStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidStreamException(Throwable cause) {
        super(cause);
    }
}
