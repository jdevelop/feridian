package com.echomine.xmpp;

import java.io.IOException;
import java.net.Socket;

import com.echomine.net.ConnectionContext;
import com.echomine.net.HandshakeFailedException;

/**
 * This mock socket handler will emulate the server by simply streaming out
 * predefined xml data. It will gulp down the incoming xml data for retrieval
 * and assertion testing afterwards. This class is useful for testing connection
 * logic.
 */
public class MockXMPPConnectionHandler implements IConnectionHandler {
    boolean connected;
    boolean failHandshake;
    boolean failAuthentication;
    String sessionId;
    String version;
    XMPPSessionContext sessCtx = new XMPPSessionContext();
    XMPPStreamContext streamCtx = new XMPPStreamContext();

    public void handshake(Socket socket, ConnectionContext connCtx) throws HandshakeFailedException {
        if (failHandshake)
            throw new HandshakeFailedException("Simulated Handshake failure");
        connected = true;
        sessCtx.setHostName(socket.getInetAddress().getHostName());
        sessCtx.setSessionId(sessionId);
        sessCtx.setVersion(version);
    }

    public void handle(Socket socket, ConnectionContext connCtx) throws IOException {
    }

    /**
     * This will do a simulated authentication
     */
    public void authenticateSession(String username, char[] password, String resource) throws XMPPException {
        if (failAuthentication)
            throw new XMPPException("Unable to login -- Simulated authentication failure");
        //after authentication, session context data must be set
        sessCtx.setUsername(username);
        sessCtx.setResource(resource);
    }

    public boolean isConnected() {
        return connected;
    }

    public void shutdown() {
        connected = false;
    }

    public void start() {
        connected = false;
    }

    public XMPPSessionContext getSessionContext() {
        return sessCtx;
    }

    public XMPPStreamContext getStreamContext() {
        return streamCtx;
    }

    public void setFailHandshake(boolean fail) {
        this.failHandshake = fail;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    
    public void setFailAuthentication(boolean fail) {
        this.failAuthentication = fail;
    }
}
