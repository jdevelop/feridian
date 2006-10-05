package com.echomine.jabber.msg;

import org.jdom.Element;
import com.echomine.jabber.JabberJDOMMessage;
import com.echomine.jabber.JabberCode;

/**
 * <p>This extension is a simple flag identifying that a message has a limited lifetime. If the message is stored offline, the
 * server will not deliver the message and simply delete it after the expiration time has passed. A client may also optionally
 * support removing the message after expiration if it has been received but not yet been viewed. The seconds attribute is
 * always relative to the time in which the message was sent/received (now). The server will adjust the value if it was stored
 * for some time but not expired on the server.</p>
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0023.html">JEP-0023 Version 1.0</a></b></p>
 */
public class ExpireXMessage extends JabberJDOMMessage {
    /** constructs a default message */
    public ExpireXMessage() {
        super(new Element("x", JabberCode.XMLNS_X_EXPIRE));
    }

    /** @return the seconds when message should be expired, or 0 if no seconds exists */
    public long getExpireTimeout() {
        String val = getDOM().getAttributeValue("seconds");
        if (val == null) return 0;
        return Long.parseLong(val);
    }

    /** sets the seconds for expiration for a message as a notification to the server or the remote client. */
    public void setExpireTimeout(long seconds) {
        //the seconds should always be positive
        if (seconds < 0) seconds = 0;
        getDOM().setAttribute("seconds", String.valueOf(seconds));
    }

    /**
     * retrieves the stored time.  The stored time can be subtracted from
     * System.currentTimeInMillis() to find out how much time has passed
     * and check it against the expire timeout to make sure that the message
     * is still considered "fresh".
     * @return the time when the message was stored, 0 if no attribute exists
     */
    public long getStoredTimeInMillis() {
        String val = getDOM().getAttributeValue("stored");
        if (val == null) return 0;
        return Long.parseLong(val);
    }

    /** sets the time when this message is stored */
    public void setStoredTimeInMillis(long time) {
        //the seconds should always be positive
        if (time < 0) time = 0;
        getDOM().setAttribute("stored", String.valueOf(time));
    }

    public int getMessageType() {
        return JabberCode.MSG_X_EXPIRE;
    }
}
