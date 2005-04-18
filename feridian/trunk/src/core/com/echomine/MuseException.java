package com.echomine;


public class MuseException extends Exception {
    public MuseException() {
        super();
    }

    public MuseException(String message) {
        super(message);
    }

    public MuseException(String message, Throwable cause) {
        super(message, cause);
    }

    public MuseException(Throwable cause) {
        super(cause);
    }
}
