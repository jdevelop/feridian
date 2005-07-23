package com.echomine.xmpp;

import com.echomine.net.HandshakeableSocketHandler;

/**
 * The interface where all connection handlers should implement. This is the
 * main handler that performs all processing of incoming and outgoing packets.
 * The handler also transforms these data from packets to recognizable stanzas
 * by the XMPP system, and vice versa.
 */
public interface ISessionHandler extends HandshakeableSocketHandler {

    /**
     * This method will authenticate the session stream with the provided
     * information. Before authentication, there are only a few tasks that the
     * server can provide -- authenticate, stream negotation, and possibly
     * Jabber In-Band registration. The handler must be in a state to work with
     * these streams. Once the stream is authenticated, full stanza processing
     * can begin (asynchronous packet processing). What this means is that it is
     * still safe to not be processing random incoming packets since it is
     * assumed that the server will not send such packets before the session is
     * authenticated.
     * 
     * @param username the username
     * @param password the password
     * @param resource optional resource to bind to
     * @throws XMPPException
     */
    void authenticateSession(String username, char[] password, String resource) throws XMPPException;

    /**
     * Retrieves the context used by the handler containing any session specific
     * information.
     * 
     * @return the session context.
     */
    XMPPSessionContext getSessionContext();

    /**
     * Retrieves the stream context associated with the handler containing
     * current stream-level details
     */
    XMPPStreamContext getStreamContext();
}
