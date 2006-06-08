package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberIQMessage;
import com.echomine.jabber.JabberMessage;
import com.echomine.jabber.JabberMessageParser;
import org.jdom.Element;

/**
 * <p>Supports the jabber:iq:last namespace.  This is a message that will retrieve the "last" information data
 * on servers and clients. For a server or service, this time represents the number of seconds since it started, or server
 * uptime. For a user account, this time represents the number of seconds since the user was last available, or when they last
 * logged out. For a connected client, if supported by that client, it represents the number of seconds since there was
 * user activity, or idle-time.</p> <p>Notice that for you to send a response message, you need to set the type as "result"
 * and you can only set the time (idle time) and not the message since there is no point to set the message based on the
 * current protocol specification.</p>
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0012.html">JEP-0012 Version 1.0</a></b></p>
 */
public class LastIQMessage extends JabberIQMessage implements JabberCode {
    private long seconds;
    private String msg;

    /**
     * this constructor is for messages with type.
     */
    public LastIQMessage(String type) {
        super(type);
        //add in the query element
        getDOM().addContent(new Element("query", XMLNS_IQ_LAST));
    }

    /**
     * defaults to iq type get
     */
    public LastIQMessage() {
        this(TYPE_GET);
    }

    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        //let the parent class parse out the normal core attributes
        super.parse(parser, msgTree);
        //parse out the iq message data
        Element query = msgTree.getChild("query", XMLNS_IQ_LAST);
        //check to make sure that there is a query tag
        if (query != null) {
            String secs = msgTree.getChild("query", XMLNS_IQ_LAST).getAttributeValue("seconds");
            msg = msgTree.getChildText("query", XMLNS_IQ_LAST);
            if (secs != null)
                seconds = Long.parseLong(secs);
        }
        return this;
    }

    /**
     * Retrieves how long ago a user logged out
     *
     * @return the time in seconds
     */
    public long getSeconds() {
        return seconds;
    }

    /**
     * retrieve the message (ie. last available message) if there is one.
     *
     * @return the "last" message, or null if there isn't one
     */
    public String getMessage() {
        return msg;
    }

    /**
     * sets the seconds since the "last" status.  This is normally used when you are
     * responding to a request that is asking for the idle time.  You simply set the idle time and send the message.
     */
    public void setSeconds(long seconds) {
        this.seconds = seconds;
        getDOM().getChild("query", XMLNS_IQ_LAST).setAttribute("seconds", String.valueOf(seconds));
    }

    public int getMessageType() {
        return MSG_IQ_LAST;
    }
}
