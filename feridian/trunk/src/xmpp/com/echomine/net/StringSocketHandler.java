package com.echomine.net;

/**
 * <p>
 * The idea of a String handler is to connect either to send some of data or
 * receive some sort of data. It should only be used once and is run
 * synchronously. There isn't even any event firing method because of its
 * simplicity (hence you can't run it asynchronously anyways).
 * </p>
 * <p>
 * The use of this class is very simple. It is usually used to test connections
 * or to do a one-time request deal such as connecting to a server that returns
 * a request and closes the connection (ie. connecting to a time server to
 * retrieve the current time). For more advanced functionality, you can add your
 * own.
 * </p>
 */
abstract public class StringSocketHandler implements SocketHandler {
    public static final int MAXLENGTH = 65535;
    public static final int SOCKETBUF = 8192;
    protected int maxLength;
    protected String data;

    public void setMaxLength(int length) {
        maxLength = length;
    }

    public int getMaxLength() {
        return (maxLength <= 0 ? MAXLENGTH : maxLength);
    }

    /**
     * Does nothing since there's nothing to shutdown as the connection is being
     * run synchronously.
     */
    public void shutdown() {
    }

    public void start() {
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data.trim();
    }

    public String toString() {
        return data;
    }
}
