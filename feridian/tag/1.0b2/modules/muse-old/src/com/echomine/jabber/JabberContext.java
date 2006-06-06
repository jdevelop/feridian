package com.echomine.jabber;

import com.echomine.common.ParseException;

/**
 * <p>The session context.  It contains information such as login, password, and other session specific information.</p>
 * <p>Because the context contains session information, it also stores the session ID.  The session ID is the ID sent by the
 * server after connection is established. </p> <p>If you set the username and password both to null, Jabber User Service will
 * create an anonymous resource if the server supports it. If you don't know what it is, don't worry about it.</p>
 * <p>Also, this is the place to set whether to use SSL to connect to the jabber server for better security</p>
 */
public class JabberContext {
    public static final String DEFAULT_SERVER = "jabber.org";
    public static final int DEFAULT_PORT = 5222;
    public static final int DEFAULT_SSL_PORT = 5223;
    private String username;
    private String password;
    private String serverName;
    private String sessionID;
    private JID serverNameJID;
    private String resource = "Home";
    private boolean secure = false;                    // SSL or not - default false

    /**
     * the default required parameters used by Jabber.  The server name here is the canonical name
     * for the server you're connecting to.  Note that this should be the same as the
     * hostname you're connecting to.  However, if you happen to use an IP rather than the server name,
     * you will not be able to login.  This is a security check done by Jabber server itself.
     * Thus, for instance, if you want to connect to jabber.org, then you would set the serverName
     * as jabber.org.  If you use www.jabber.org (or a different name that maps to the same IP as jabber.org,
     * and you used www.jabber.org as the server name, then you will not be able to login to the Jabber
     * server itself.  Thus, in a way the server name is tied to the hostname you're connecting to but it
     * may be totally different as well.
     * @param username the username
     * @param password the password for the user
     * @param serverName the canonical name of the server name as specificied by the Jabber server itself.
     */
    public JabberContext(String username, String password, String serverName) {
        this.username = username;
        this.password = password;
        this.serverName = serverName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /** switches the connection between secure and in-secure (default) connection */
    public void setSSL(boolean secure) {
        this.secure = secure;
    }

    /** replies true if this is a SSL connection */
    public boolean isSSL() {
        return secure;
    }

    /** @return the server name's JID.  null if the server name has not been set */
    public JID getServerNameJID() {
        return serverNameJID;
    }

    /**
     * normally, this information can be obtained from the connection model.
     * however, a canonical name (not an IP or reverse-lookup name) is needed
     * by the Session Service, so the host information is kept here.
     * @return the name of the server to connect to
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * sets the server name that this session will connect to. If the server name cannot be properly parse,
     * an exception will be thrown.
     */
    public void setServerName(String serverName) throws ParseException {
        serverNameJID = new JID(serverName);
        this.serverName = serverName;
    }

    /** @return the session id sent by the server, null if session hasn't been established yet */
    public String getSessionID() {
        return sessionID;
    }

    /** sets the session ID (normally used only during handshake) and should not be changed */
    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    /** @return the resource name associated with this session */
    public String getResource() {
        return resource;
    }

    /**
     * sets the resource name associated with this session.  You can think of resource
     * as a location or some sort of information describing your session.  Examples are Home, Office, Laptop, etc.
     */
    public void setResource(String resource) {
        this.resource = resource;
    }
}
