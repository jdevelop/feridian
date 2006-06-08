package com.echomine.xmpp.packet;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

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
 * Processing of extended Messages are supported, but it is up to the developer
 * to implement capabilities to work with extensions types. Simply retrieve the
 * extension you recognize (ie. delay, event, etc) and deal with each
 * appropriately.
 * </p>
 * <p>
 * Thread ID -- Normally, if you initiate a chat for the first time with a JID,
 * you should set the Thread ID to a new ID (you can obtain a new ID from the
 * IDGenerator method. Thread IDs are opaque -- they contain no meaning and are
 * simply meant for comparison purposes. However, if you are replying to a
 * message, you should set your reply message's Thread ID to the ID of the
 * message that you're replying to. ie.
 * reply.setThreadID(origMsg.getThreadID()). The developer is responsible for
 * setting the Thread IDs for ALL messages.
 * </p>
 * <p>
 * International, Locale, and xml:lang considerations -- This presence packet
 * supports xml:lang. Stanza-level locale (setLocale()) overrides stream-level
 * locale and will set the overall default locale for all children. The children
 * can further override higher-level locales by setting its own individual
 * locale.
 * </p>
 * <p>
 * <b>Current Implementation: XMPP IM and Presence RFC </b>
 * </p>
 */
public class MessagePacket extends IMPacket {
    public static final String TYPE_CHAT = "chat";
    public static final String TYPE_GROUPCHAT = "groupchat";
    public static final String TYPE_HEADLINE = "headline";
    public static final String TYPE_NORMAL = "normal";
    private LinkedHashMap<Locale, String> subjects = new LinkedHashMap<Locale, String>();
    private LinkedHashMap<Locale, String> bodies = new LinkedHashMap<Locale, String>();
    private String threadID;

    public MessagePacket() {
        super();
        setType(TYPE_NORMAL);
    }

    /**
     * This will return the body for the default (ie. no xml:lang) locale. If no
     * such body exists, then it will check for the packet's default locale.
     * Failing that, a null is returned.
     * 
     * @return Returns the body or null if none exists
     */
    public String getBody() {
        String body = getBody(null);
        if (body == null && getLocale() != null)
            body = getBody(getLocale());
        return body;
    }

    /**
     * This will return the body for the specified locale. If no such body
     * exists, then null is returned.
     * 
     * @param locale the locale, null to specify default (ie. no xml:lang)
     * @return Returns the body or null if none exists
     */
    public String getBody(Locale locale) {
        return bodies.get(locale);
    }

    /**
     * sets the body for the "null" locale.
     * 
     * @param body The body to set.
     */
    public void setBody(String body) {
        setBody(body, null);
    }

    /**
     * Sets the body for the specified locale. If locale is "null", then this is
     * the empty/default body.
     * 
     * @param body the body text
     * @param locale optional locale. null to specify default (ie. no xml:lang)
     */
    public void setBody(String body, Locale locale) {
        bodies.put(locale, body);
    }

    /**
     * obtain an unmodifiable hash map of subjects. The key is an Locale object,
     * the value a String. The null locale stores the default (no xml:lang)
     * subject.
     * 
     * @return an unmodifiable hash map of subjects
     */
    public Map getBodies() {
        return Collections.unmodifiableMap(bodies);
    }

    /**
     * This will return the subject for the default (ie. no xml:lang) locale. If
     * no such body exists, then it will check for the packet's default locale.
     * Failing that, a null is returned.
     * 
     * @return Returns the subject or null if none exists
     */
    public String getSubject() {
        String subject = getSubject(null);
        if (subject == null && getLocale() != null)
            subject = getSubject(getLocale());
        return subject;
    }

    /**
     * This will return the subject for the specified locale. If no such body
     * exists, then null is returned.
     * 
     * @param locale the locale, optionally null for no xml:lang
     * @return Returns the body or null if none exists
     */
    public String getSubject(Locale locale) {
        return subjects.get(locale);
    }

    /**
     * sets the subject for the "null" locale.
     * 
     * @param subject The subject to set.
     */
    public void setSubject(String subject) {
        setSubject(subject, null);
    }

    /**
     * Sets the subject for the specified locale. If locale is "null", then this
     * is the empty/default body.
     * 
     * @param subject the subject text
     * @param locale optional locale. null to specify default (ie. no xml:lang)
     */
    public void setSubject(String subject, Locale locale) {
        subjects.put(locale, subject);
    }

    /**
     * obtain an unmodifiable hash map of subjects. The key is an Locale object,
     * the value a String. The null locale stores the default (no xml:lang)
     * subject.
     * 
     * @return an unmodifiable hash map of subjects
     */
    public Map getSubjects() {
        return Collections.unmodifiableMap(subjects);
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
