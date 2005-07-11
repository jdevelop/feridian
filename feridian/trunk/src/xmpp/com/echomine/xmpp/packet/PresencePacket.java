package com.echomine.xmpp.packet;

import com.echomine.xmpp.StanzaPacketBase;

/**
 * <p>
 * This is how presence works. You declare yourself to be available, then you
 * can set your state to away, extended away, do not disturb, etc. If you set
 * yourself unavailable, then no one will see you online (essentially, you are
 * "invisible"). If you set your state to one of the mentioned states, then you
 * can set a descriptive text by setting the status.
 * </p>
 * <p>
 * The type indicates what kind of presence this is, either to be available or
 * unavailable, or if it's a subscription request. If no type is set, it is
 * assumed to be available. This means that sending a newly instantiated
 * presence packet will set your presence to online and available. Thus, if you
 * retrieve the type and it is null, then the entity presence is assumed to be
 * online and available.
 * </p>
 * <p>
 * The to and from are used to indicate where the presence is going to or where
 * it's coming from, respectively. These two fields are not always available,
 * depending on what the type is.
 * </p>
 * <p>
 * The show tells others about you current state, whether you are away, extended
 * away, etc. If show is not set, it is assumed to be online and available. This
 * should only be set when type is NOT set.
 * </p>
 * <p>
 * The status is the descriptive text that tells others what you're doing. You
 * may put anything for status when you set yourself to away/extended away/do
 * not disturb. This should only be set when type is NOT set.
 * </p>
 * <p>
 * The priority is used when you have multiple logins. The "default" is 0.
 * Negative priority is a preference that the sender should not be used for
 * direct or immediate contact. This should only be set when type is NOT set.
 * </p>
 * <p>
 * <b>Current Implementation: XMPP IM and Presence RFC </b> <br>
 * </p>
 */
public class PresencePacket extends StanzaPacketBase {
    public static final String TYPE_UNAVAILABLE = "unavailable";
    public static final String TYPE_SUBSCRIBE = "subscribe";
    public static final String TYPE_SUBSCRIBED = "subscribed";
    public static final String TYPE_UNSUBSCRIBE = "unsubscribe";
    public static final String TYPE_UNSUBSCRIBED = "unsubscribed";
    public static final String TYPE_PROBE = "probe";
    public static final String SHOW_AWAY = "away";
    public static final String SHOW_CHAT = "chat";
    public static final String SHOW_DND = "dnd";
    public static final String SHOW_XA = "xa";

    private String show;
    private String status;
    private int priority;

    public PresencePacket() {
        super();
    }

    /**
     * @return Returns the priority.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * The priority must only be between -127 and +127
     * 
     * @param priority The priority to set.
     * @throws IllegalArgumentException if priority isn't within the range
     */
    public void setPriority(int priority) {
        if (priority < -127 || priority > 127)
            throw new IllegalArgumentException("Priority must be between -127 and +127");
        this.priority = priority;
    }

    /**
     * @return Returns the show.
     */
    public String getShow() {
        return show;
    }

    /**
     * @param show The show to set.
     */
    public void setShow(String show) {
        this.show = show;
    }

    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
