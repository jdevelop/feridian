package com.echomine.xmpp;

/**
 * The class in which all authenticators must implement. An authenticator is
 * basically a stream with additional required methods. The process method is
 * where the main authentication should be done.
 */
public interface IXMPPAuthenticator extends IXMPPStream {
    /**
     * Before performing authentication, this method should be called to check
     * if this authenticator can actually authenticate based on server-provided
     * features.
     * 
     * @param sessCtx the session context
     * @param streamCtx the stream context
     * @return true if this authenticator can authenticate, false otherwise
     */
    boolean canAuthenticate(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx);

    /**
     * This method will return a value that indicates whether handshake should
     * be redone after authentication.
     * 
     * @return true if handshake should be redone, false otherwise
     */
    boolean redoHandshake();
}
