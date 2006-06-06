package com.echomine.jabber;

/**
 * contains a set of types that are used by the Presence message.  This is here to be for convenience
 * since it uses more descriptive "words" to describe the types of commands
 */
public interface PresenceCode {
    public final static String SHOW_ONLINE = "online";
    public final static String SHOW_CHAT = "chat";
    public final static String SHOW_AWAY = "away";
    public final static String SHOW_EXTENDED_AWAY = "xa";
    public final static String SHOW_DO_NOT_DISTURB = "dnd";
    public final static String TYPE_SUBSCRIBE = "subscribe";
    public final static String TYPE_SUBSCRIBED = "subscribed";
    public final static String TYPE_UNSUBSCRIBE = "unsubscribe";
    public final static String TYPE_UNSUBSCRIBED = "unsubscribed";
    public final static String TYPE_AVAILABLE = "available";
    public final static String TYPE_UNAVAILABLE = "unavailable";
    public final static String TYPE_PROBE = "probe";
    public final static String TYPE_INVISIBLE = "invisible";
}
