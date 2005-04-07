package com.echomine.net;

import alt.java.net.Socket;

import java.io.IOException;

/** Handler handles all connection details, including handshaking and data processing. */
public interface SocketHandler {
    /**
     * Handles the connection details, including handshaking and data processing.
     * The handler should NOT close the socket.  Rather, it is up to the
     * caller of this method to close the socket when all processing is done.
     * @throws IOException when any processing error occurs
     */
    void handle(Socket socket) throws IOException;

    /**
     * starts the connection.  This will give the handler a chance to reset
     * any information or create any instances before the handling begins.
     */
    void start();

    /**
     * shuts down/closes the connection.  This is to give the handler an option to close the connection nicely and have a
     * chance to do any cleanups before/after socket close.
     */
    void shutdown();
}
