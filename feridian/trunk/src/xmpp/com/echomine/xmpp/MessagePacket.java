package com.echomine.xmpp;

/**
 * <p>
 * This is the base message for working with private IM messages, group chats,
 * and anything that is sent through the 'message' tag.
 * </p>
 * <p>
 * The message body may come in different formats. For instance, it can come in
 * as XHTML for better style support. This is an extension message. To display
 * XHTML data in Java is rather easy. You can simply use a JTextPane or
 * JEditorPane, set the Content MIME Type to text/html, and then just set the
 * Text Pane's text to contained XHTML content.
 * </p>
 * <p>
 * Processing of extended Messages are supported, but it is up to the
 * developer to implement capabilities to work with custom Message types.
 * </p>
 * <p>
 * Thread IDs is easy to work with. Normally, if you initiate a chat for the
 * first time with a JID, you should set the Thread ID to a new ID (you can
 * obtain a new ID from the generateThreadID() method, which will return a GUID
 * 32-byte hex string). However, if you are replying to a message, you should
 * set your reply message's Thread ID to the ID of the message that you're
 * replying to. ie. reply.setThreadID(origMsg.getThreadID()). The developer is
 * responsible for setting the Thread IDs for ALL messages.
 * </p>
 * <p>
 * <b>Current Implementation: XMPP IM and Presence RFC </b>
 * </p>
 * FIXME: message should support multiple "body" elements for different
 * languages. It should allow the user to retrieve the default locale in some
 * way.
 */
public class MessagePacket extends StanzaPacketBase {
    public static final String TYPE_CHAT = "chat";
    public static final String TYPE_GROUPCHAT = "groupchat";
    public static final String TYPE_HEADLINE = "headline";
    public static final String TYPE_NORMAL = "normal";
    private String subject;
    private String body;
    private String threadID;

    public MessagePacket() {
        super();
        type = TYPE_NORMAL;
    }

    /**
     * @return Returns the body.
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body The body to set.
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return Returns the subject.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject The subject to set.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return Returns the threadID.
     */
    public String getThreadID() {
        return threadID;
    }

    /**
     * @param threadID The threadID to set.
     */
    public void setThreadID(String threadID) {
        this.threadID = threadID;
    }
}
