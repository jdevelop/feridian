package com.echomine.net;

/**
 * The exception thrown when connection handshake fails.  This only
 * applies to those classes that works with HandshakeableSocketHandler
 */
public class HandshakeFailedException extends ConnectionException {
    public HandshakeFailedException() {
    }

    public HandshakeFailedException(String msg) {
        super(msg);
    }
}
