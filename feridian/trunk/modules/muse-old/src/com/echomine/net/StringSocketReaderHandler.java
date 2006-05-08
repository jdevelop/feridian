package com.echomine.net;

import alt.java.net.Socket;

import java.io.BufferedInputStream;
import java.io.IOException;

/** an easy handler that reads in a string and then disconnects immediately */
public class StringSocketReaderHandler extends StringSocketHandler {
    public StringSocketReaderHandler() {
    }

    public StringSocketReaderHandler(int length) {
        setMaxLength(length);
    }

    /**
     * reads in a string.  The connection will be closed
     * by the caller of this method.  Any exception will also be handled
     * by the caller.
     */
    public void handle(Socket socket) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream(), SOCKETBUF);
        byte[] buffer = new byte[getMaxLength()];
        int bytesread;
        if ((bytesread = bis.read(buffer, 0, getMaxLength())) != -1)
            data = new String(buffer, 0, bytesread);
    }
}
