package com.echomine.xmpp;

/**
 * <p>
 * The client context hold information set by the local entity that is used to
 * communicate with the remote entity. Note that this context is different from
 * XMPPConnectionContext, which holds session-specific data during negotiation.
 * </p>
 * <p>
 * A note regarding resource name. Under XMPP, it is recommended that resource
 * binding is used to set a resource for the client. If such a feature is
 * supported and announced by the server, then resource binding must be done. If
 * the resource name is not set, then Muse will request that the server send a
 * server-generated resource name. If this is not what you want, you can set the
 * resource name to request that the server use your desired resource. The
 * server should try to accomodate your request, but may instead generate its
 * own resource JID for you. Thus, after session is established, you should
 * double check to see if the requested resource has been changed.
 * <p>
 * Default port is 5222 (official registered port for Jabber) <br>
 * </p>
 */
public class XMPPClientContext {
    public static final int DEFAULT_PORT = 5222;
    private String username;
    private String resource;
    private String host;

    public XMPPClientContext() {
    }

    /**
     * @param username the username
     * @param serverName the server to connect to
     */
    public XMPPClientContext(String username, String serverName) {
        this.username = username;
        this.host = serverName;
    }

    /**
     * @return Returns the host to connect to.
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host The host to connect to.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return Returns the username for authentication.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username The username used to authenticate.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return Returns the resource.
     */
    public String getResource() {
        return resource;
    }

    /**
     * @param resource The resource to set.
     */
    public void setResource(String resource) {
        this.resource = resource;
    }
}
