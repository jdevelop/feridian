package com.echomine.jabber;

import com.echomine.common.ParseException;
import com.echomine.jabber.msg.DelayXMessage;
import com.echomine.jabber.msg.PGPSignedXMessage;
import org.jdom.Element;

/**
 * <p>This parses the presence message and makes it easy to retrieve the information.  The Presence Message support X
 * Namespaces and by default will add those X Namespaces into outgoing messages.</p>
 * <p>This is how presence works.  You declare yourself to be available, then you can set your state to away, extended away,
 * do not disturb, etc.  If you set yourself unavailable, then no one will see you online (essentially, you are "invisible").
 * If you set your state to one of the mentioned states, then you can set a descriptive text by setting the status.</p>
 * <p>The type indicates what kind of presence this is, either to be available or unavailable, or if it's a subscription
 * request.</p> <p>The to and from are used to indicate where the presence is going to or where it's coming from,
 * respectively.  These two fields are not always available, depending on what the type is.</p>
 * <p>The show tells others about you current state, whether you are away, extended away, etc.</p>
 * <p>The status is the descriptive text that tells others what you're doing. You may put anything for status when you set
 * yourself to away/extended away/do not disturb.</p> <p>The priority is used when you have multiple logins.  The "default" is
 * the highest priority and will receive the private messages.  Negative priority is a preference that the sender should not
 * be used for direct or immediate contact.</p>
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0018.html">JEP-0018 Version 0.1</a></b></p>
 * <p><b>Conforms with: XMPP Presence protocol</b></p>
 */
public class JabberPresenceMessage extends AbstractJabberMessage implements PresenceCode, JabberCode {
    private String showState;
    private String status;
    private int priority = 0;

    /**
     * Normally used for creating an outgoing message
     */
    public JabberPresenceMessage(String type) {
        super(type, new Element("presence", XMLNS_PRESENCE));
    }

    /**
     * constructs a default type of AVAILABLE
     */
    public JabberPresenceMessage() {
        this(TYPE_AVAILABLE);
    }

    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        //let the parent class parse out the normal core attributes
        super.parse(parser, msgTree);
        if (getType() == null)
            setType(TYPE_AVAILABLE);
        String prior = getDOM().getChildText("priority", XMLNS_PRESENCE);
        if (prior == null)
            priority = 0;
        else
            try {
                priority = Integer.parseInt(prior);
            } catch (NumberFormatException ex) {
                throw new ParseException("<priority> inside <presence> element is not an integer as it should be");
            }
        return this;
    }

    /**
     * The method is overridden to conform properly with XMPP protocol standards.
     * In XMPP, when a presence is available, it should simply remove the type
     * from being sent.
     *
     * @param type the type of presence (available, unavailable, dnd, etc)
     */
    public void setType(String type) {
        if (TYPE_AVAILABLE.equals(type))
            super.setType(null);
        else
            super.setType(type);
    }

    /**
     * sets the status string when available. Set status to null to remove the status
     */
    public void setStatus(String status) {
        this.status = status;
        getDOM().removeChild("status", XMLNS_PRESENCE);
        if (status != null) {
            Element temp = new Element("status", XMLNS_PRESENCE);
            temp.setText(status);
            getDOM().addContent(temp);
        }
    }

    /**
     * @return the status string of the presence message, null if none exists
     */
    public String getStatus() {
        if (status != null) return status;
        status = getDOM().getChildText("status", XMLNS_PRESENCE);
        return status;
    }

    /**
     * Retrieve the show string that is included with the presence message.
     * If no show state exist, the default SHOW_ONLINE will
     * be returned as default per XMPP protocol specs.
     *
     * @return the show state, never null
     */
    public String getShowState() {
        if (showState != null) return showState;
        showState = getDOM().getChildText("show", XMLNS_PRESENCE);
        if (showState == null) return SHOW_ONLINE;
        return showState;
    }

    /**
     * sets the string for show state.  Null to remove the show state string
     */
    public void setShowState(String showState) {
        this.showState = showState;
        getDOM().removeChild("show", XMLNS_PRESENCE);
        if (showState != null) {
            Element temp = new Element("show", XMLNS_PRESENCE);
            temp.setText(showState);
            getDOM().addContent(temp);
        }
    }

    /**
     * @return the priority of the message, 0 is default
     */
    public int getPriority() {
        return priority;
    }

    /**
     * The "default" is the highest priority and will receive the private messages.  Negative priority is a preference that
     * the sender should not be used for direct or immediate contact.  It must be an integer value between -127 to +127.
     * A 0 value is the default.
     */
    public void setPriority(int priority) {
        this.priority = priority;
        getDOM().removeChild("priority", XMLNS_PRESENCE);
        Element temp = new Element("priority", XMLNS_PRESENCE);
        temp.setText("" + priority);
        getDOM().addContent(temp);
    }

    public int getMessageType() {
        return MSG_PRESENCE;
    }

    /**
     * convenience method to retrieve the Delay X Message (you can get the message by calling getXMessage() as well)
     *
     * @return the delay message, null if none exists
     */
    public DelayXMessage getDelayMessage() {
        return (DelayXMessage) getXMessage(XMLNS_X_DELAY.getURI());
    }

    /**
     * Convenience method to retrieve the signature of the presence message
     * signed in PGP.
     */
    public PGPSignedXMessage getPGPSignedMessage() {
        return (PGPSignedXMessage) getXMessage(XMLNS_X_PGP_SIGNED.getURI());
    }
}
