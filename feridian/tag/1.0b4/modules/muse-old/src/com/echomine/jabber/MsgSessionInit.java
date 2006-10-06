package com.echomine.jabber;

/**
 * represents the initial message that gets sent out.  This message is the first message initiated by
 * the client to establish an active session.  Once session starts, the rest is parsed and handled accordingly.
 * <p><b>Conforms to XMPP 1.0 (Stream version 1.0)</b></p>
 */
public class MsgSessionInit extends JabberMessage {
    private String serverName;
    private String sessionID;
    private String version = "1.0";
    private boolean outgoing;

    /**
     * sets only the to.  This automatically indicates that this message is outgoing.
     * outgoing message also requires waiting for a reply.
     * @param to the server name that the client is connecting to
     */
    public MsgSessionInit(String to) {
        super();
        this.serverName = to;
        outgoing = true;
        setSynchronized(true);
    }

    /**
     * sets the from and session id.  This is normally the constructor used for incoming messages.
     * @param from the server name that is received from the server
     * @param sessionID the session id associated with this connection as received from the server
     * @param version the version number of the accepted stream, null if no version (1.0 will be assumed)
     */
    public MsgSessionInit(String from, String sessionID, String version) {
        super();
        this.serverName = from;
        this.sessionID = sessionID;
        if (version != null)
            this.version = version;
        outgoing = false;
    }

    /** encode is only used for outgoing messages only */
    public String encode() {
        StringBuffer buffer = new StringBuffer(150);
        buffer.append("<?xml version='1.0' encoding='UTF-8' ?>");
        buffer.append("<stream:stream to='" + serverName + "' version='" + version + "' ");
        buffer.append("xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>");
        return buffer.toString();
    }

    /** @return the message based on the type of the message it is */
    public String toString() {
        if (outgoing) {
            return encode();
        } else {
            return "<stream:stream from=\"" + serverName + "\" id=\"" + sessionID + "\">";
        }
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /** @return the unique session id sent by the server */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * the session init method uses the "to" and "from" as the main id, not the
     * generated id itself.  Thus, the method is overridden to work with this behavior.
     */
    public String getMessageID() {
        return serverName;
    }

    /** the default message type for message init is arbitrary and defined only in the Muse itself. */
    public int getMessageType() {
        return JabberCode.MSG_INIT;
    }

    /** @return the version of the stream */
    public String getVersion() {
        return version;
    }

    /** sets the version for the stream */
    public void setVersion(String version) {
        this.version = version;
    }
}
