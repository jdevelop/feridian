package com.echomine.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

/** Convenience methods that closes IO Streams */
public class IOUtil {
    public static void closeStream(InputStream is) {
        try {
            if (is != null)
                is.close();
        } catch (IOException ex) {
        }
    }

    public static void closeStream(OutputStream os) {
        try {
            if (os != null)
                os.close();
        } catch (IOException ex) {
        }
    }

    public static void closeStream(Reader is) {
        try {
            if (is != null)
                is.close();
        } catch (IOException ex) {
        }
    }

    public static void closeStream(Writer os) {
        try {
            if (os != null)
                os.close();
        } catch (IOException ex) {
        }
    }

    public static void closeSocket(Socket socket) {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException ex) {
        }
    }

    public static void closeSocket(ServerSocket socket) {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException ex) {
        }
    }

    /**
     * reads from the stream into the buffer until it read in the entire line or
     * the length is reached. The CRLF are not included as part of the buffer
     * 
     * @param is the InputStream to read the data from
     * @param bytebuf the byte buffer where the data goes to
     * @param offset the offset from which to start writing in the buffer
     * @param length the maximum length to read if CRLF is not reached yet
     */
    public static int readToCRLF(InputStream is, byte[] bytebuf, int offset, int length) throws IOException {
        int ch;
        boolean nearEnd = false;
        int bytesread = 0;
        while (bytesread < length) {
            ch = is.read();
            if (ch == 0 || ch == -1) {
                throw new IOException("Connection closed.");
            }
            bytebuf[offset + bytesread] = (byte) ch;
            bytesread++;
            //look for \r\n or \n\n
            if (nearEnd) {
                if (ch == (int) '\n') {
                    // minus 2 to remove the \r\n or \n\n.
                    return bytesread - 2;
                } else {
                    nearEnd = false;
                }
            }
            if ((ch == (int) '\n') || (ch == (int) '\r')) {
                nearEnd = true;
            }
        }
        throw new IOException("Out of buffer before reaching CRLF");
    }

    /**
     * reads from the stream into the buffer until it read in the entire line or
     * the length is reached. The LF is not included as part of the buffer
     * 
     * @param is the InputStream to read the data from
     * @param bytebuf the byte buffer where the data goes to
     * @param offset the offset from which to start writing in the buffer
     * @param length the maximum length to read if CRLF is not reached yet
     */
    public static int readToLF(InputStream is, byte[] bytebuf, int offset, int length) throws IOException {
        while (length > 0) {
            int ch = is.read();
            if (ch == 0 || ch == -1) {
                throw new IOException("Connection closed by remote host.");
            }
            bytebuf[offset++] = (byte) ch;
            length--;
            // look for \n
            if (ch == (int) '\n') {
                // minus 1 to remove the \n.
                return offset - 1;
            }
        }
        throw new IOException("Out of buffer");
    }
}
