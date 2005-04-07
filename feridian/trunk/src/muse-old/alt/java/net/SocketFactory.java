package alt.java.net;

import java.io.IOException;
import java.net.UnknownHostException;

public interface SocketFactory {
    Socket createSocket(String aHost, int aPort) throws UnknownHostException,
            IOException;
}
