package alt.java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;

public class SocketImpl implements Socket {
    private final java.net.Socket socket;

    public SocketImpl(java.net.Socket socket) {
        this.socket = socket;
    }

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    public InetAddress getLocalAddress() {
        return socket.getLocalAddress();
    }

    public int getPort() {
        return socket.getPort();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public void setTcpNoDelay(boolean on) throws SocketException {
        socket.setTcpNoDelay(on);
    }

    public boolean getTcpNoDelay() throws SocketException {
        return socket.getTcpNoDelay();
    }

    public void setSoLinger(boolean on, int linger) throws SocketException {
        socket.setSoLinger(on, linger);
    }

    public int getSoLinger() throws SocketException {
        return socket.getSoLinger();
    }

    public synchronized void setSoTimeout(int timeout) throws SocketException {
        socket.setSoTimeout(timeout);
    }

    public synchronized int getSoTimeout() throws SocketException {
        return socket.getSoTimeout();
    }

    public synchronized void setSendBufferSize(int size)
            throws SocketException {
        socket.setSendBufferSize(size);
    }

    public synchronized int getSendBufferSize() throws SocketException {
        return socket.getSendBufferSize();
    }

    public synchronized void setReceiveBufferSize(int size)
            throws SocketException {
        socket.setReceiveBufferSize(size);
    }

    public synchronized int getReceiveBufferSize()
            throws SocketException {
        return socket.getReceiveBufferSize();
    }

    public void setKeepAlive(boolean on) throws SocketException {
        socket.setKeepAlive(on);
    }

    public boolean getKeepAlive() throws SocketException {
        return socket.getKeepAlive();
    }

    public synchronized void close() throws IOException {
        socket.close();
    }

    public void shutdownInput() throws IOException {
        socket.shutdownInput();
    }

    public void shutdownOutput() throws IOException {
        socket.shutdownOutput();
    }

}
