package com.echomine.net;

import alt.java.net.Socket;

/**
 * This interface adds an additional handshake method to the socket handler
 */
public interface HandshakeableSocketHandler extends SocketHandler {
    /**
     * the handler will do an initial handshake first before having
     * handle called.  Note that if handshaking problems occurs,
     * the socket does not necessarily have to be closed.  Rather,
     * the one calling this method will have to handle the closing
     * of the socket.
     * @throws HandshakeFailedException when error occurs during handshaking
     */
    void handshake(Socket socket) throws HandshakeFailedException;
}
