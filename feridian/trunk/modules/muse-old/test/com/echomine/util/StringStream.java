package com.echomine.util;

import java.io.IOException;
import java.io.InputStream;

/** temporary string stream reader */
public class StringStream extends InputStream {
    byte[] data;
    int currentPos;

    public StringStream(String dataStr) {
        setString(dataStr);
    }

    public void setString(String dataStr) {
        this.data = dataStr.getBytes();
        currentPos = 0;
    }

    public boolean equals(byte[] inbuf, int offset, int length) {
        for (int i = offset; i < length; i++) {
            if (inbuf[i] != data[i]) return false;
        }
        return true;
    }

    public int read() throws IOException {
        byte[] d = new byte[1];
        if (read(d) == -1) return -1;
        return d[0];
    }

    /** reads the bytes from the string */
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    /** reads the bytes from the string */
    public int read(byte b[], int off, int len) throws IOException {
        int diff = data.length - currentPos;
        int readLen = len - off;
        if (currentPos == data.length) return -1;
        if (diff < readLen) {
            System.arraycopy(data, currentPos, b, off, diff);
            currentPos += diff;
            return diff;
        } else {
            System.arraycopy(data, currentPos, b, off, readLen);
            currentPos += readLen;
            return readLen;
        }
    }
}
