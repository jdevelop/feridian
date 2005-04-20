package com.echomine.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Mock socket that overrides certain methods that is used by the tests
 */
public class MockSocket extends Socket {
    InputStream is;
    OutputStream os;

    /*
     * (non-Javadoc)
     * 
     * @see java.net.Socket#getInputStream()
     */
    public InputStream getInputStream() throws IOException {
        return is;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.net.Socket#getOutputStream()
     */
    public OutputStream getOutputStream() throws IOException {
        return os;
    }

    public void setInputStream(InputStream stream) throws IOException {
        this.is = stream;
    }

    public void setOutputStream(OutputStream stream) throws IOException {
        this.os = stream;
    }
}
