package com.echomine.xmpp;

/**
 * This exception is used to identify errors when the API connects to a remote
 * client/server that is not XMPP compliant.  What this normally means is that 
 * during initial handshaking, the "version" attribute is not detected or 
 * is lower than the version that is supported by this API.
 * 
 * @author ckchris
 */
public class NonCompliantXMPPServerException extends XMPPException {
    private static final long serialVersionUID = -8104026362100212877L;

    public NonCompliantXMPPServerException() {
        super();
    }

    public NonCompliantXMPPServerException(String message) {
        super(message);
    }

    public NonCompliantXMPPServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonCompliantXMPPServerException(Throwable cause) {
        super(cause);
    }
}
