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
}
