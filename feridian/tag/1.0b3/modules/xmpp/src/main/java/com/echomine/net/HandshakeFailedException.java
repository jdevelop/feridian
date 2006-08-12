package com.echomine.net;

/**
 * The exception thrown when connection handshake fails. This only applies to
 * those classes that works with HandshakeableSocketHandler
 */
public class HandshakeFailedException extends ConnectionException {
    private static final long serialVersionUID = 8665676278693066882L;

    public HandshakeFailedException() {
    }

    public HandshakeFailedException(String msg) {
        super(msg);
    }

    public HandshakeFailedException(String message, Throwable cause) {
        super(message, cause);
        
    }

    public HandshakeFailedException(Throwable cause) {
        super(cause);
        
    }
    
    
}
