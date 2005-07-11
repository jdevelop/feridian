package com.echomine.net;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * a handler that writes a string to the remote connection and disconnects
 * immediately.
 */
public class StringSocketWriterHandler extends StringSocketHandler {
    public StringSocketWriterHandler() {
    }

    public StringSocketWriterHandler(String data) {
        setData(data);
        setMaxLength(data.length());
    }

    /**
     * writes out a string. The connection will be closed by the caller of this
     * method. Any exception will also be handled by the caller.
     */
    public void handle(Socket socket) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream(), SOCKETBUF);
        bos.write(data.getBytes(), 0, data.length());
        // must manually flush
        bos.flush();
    }
}
