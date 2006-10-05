package com.echomine.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Mock socket that overrides certain methods that is used by the tests
 */
public class MockSocket extends Socket {
    InputStream is;
    OutputStream os;
    InetAddress address;
    int port;

    public MockSocket() {
        super();

    }

    public MockSocket(InetAddress address, int port) throws IOException {
        this.address = address;
        this.port = port;
    }

    /**
     * Creates a mock socket with the inet address of localhost
     * 
     * @param port the port number to use
     * @throws UnknownHostException
     * @throws IOException
     */
    public MockSocket(int port) throws UnknownHostException, IOException {
        this.address = InetAddress.getLocalHost();
        this.port = port;
    }

    /**
     * obtains an input stream. If the input stream is empty, then an empty
     * StringBufferInputStream is created.
     */
    public InputStream getInputStream() throws IOException {
        if (is == null)
            is = new ByteArrayInputStream("".getBytes());
        return is;
    }

    /**
     * obtains an output stream. If the output stream is empty, then a default
     * ByteArrayOutputStream is created.
     */
    public OutputStream getOutputStream() throws IOException {
        if (os == null)
            os = new ByteArrayOutputStream();
        return os;
    }

    public void setInputStream(InputStream stream) throws IOException {
        this.is = stream;
    }

    public void setOutputStream(OutputStream stream) throws IOException {
        this.os = stream;
    }

    public InetAddress getInetAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
