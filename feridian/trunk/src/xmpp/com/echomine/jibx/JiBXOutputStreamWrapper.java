package com.echomine.jibx;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This stream wrappers is specifically designed to work with XMPPStreamWriter
 * such that when close() is called, the underlying stream is intentionally not
 * closed, but rather only flushed. The idea behind this is that during
 * handshake negotiation, changing sockets from normal socket to TLS should not
 * close the underlying socket because TLS relies on the underlying socket.
 */
public class JiBXOutputStreamWrapper extends OutputStream {
    OutputStream os;

    public JiBXOutputStreamWrapper(OutputStream os) {
        super();
        this.os = os;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) throws IOException {
        os.write(b);
    }

    /*
     * This close method is intentionally overridden to do absolutely nothing.
     * 
     * @see java.io.OutputStream#close()
     */
    public void close() throws IOException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#flush()
     */
    public void flush() throws IOException {
        os.flush();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] b, int off, int len) throws IOException {
        os.write(b, off, len);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(byte[])
     */
    public void write(byte[] b) throws IOException {
        os.write(b);
    }
}
