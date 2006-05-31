package alt.java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;

public interface Socket {
    InetAddress getInetAddress();

    InetAddress getLocalAddress();

    int getPort();

    int getLocalPort();

    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;

    void setTcpNoDelay(boolean on) throws SocketException;

    boolean getTcpNoDelay() throws SocketException;

    void setSoLinger(boolean on, int linger) throws SocketException;

    int getSoLinger() throws SocketException;

    void setSoTimeout(int timeout) throws SocketException;

    int getSoTimeout() throws SocketException;

    void setSendBufferSize(int size)
            throws SocketException;

    int getSendBufferSize() throws SocketException;

    void setReceiveBufferSize(int size)
            throws SocketException;

    int getReceiveBufferSize()
            throws SocketException;

    void setKeepAlive(boolean on) throws SocketException;

    boolean getKeepAlive() throws SocketException;

    void close() throws IOException;

    void shutdownInput() throws IOException;

    void shutdownOutput() throws IOException;
}
