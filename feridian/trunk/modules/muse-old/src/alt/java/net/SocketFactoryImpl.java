package alt.java.net;

import java.io.IOException;
import java.net.UnknownHostException;

public class SocketFactoryImpl implements SocketFactory {

    public Socket createSocket(String host, int port)
            throws UnknownHostException, IOException {

        return new SocketImpl(new java.net.Socket(host, port));
    }
}
