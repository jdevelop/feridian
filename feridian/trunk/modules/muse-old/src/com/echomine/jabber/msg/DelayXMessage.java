package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JabberJDOMMessage;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberMessage;
import com.echomine.jabber.JabberMessageParser;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.jdom.Element;

/**
 * This method add support for the jabber:x:delay message type.  Normally
 * this message extension is contained within some other message.  Currently
 * you cannot send delay message along with the presence message, so this object will only parse incoming data.
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0091.html">JEP-0091 Version 1.0</a></b></p>
 */
public class DelayXMessage extends JabberJDOMMessage {
    private String text;
    private String from;
    private String time;
    private SimpleDateFormat format;

    /** constructs a default Delay message (with the x element) */
    public DelayXMessage() {
        super(new Element("x", JabberCode.XMLNS_X_DELAY));
    }

    /** parses the message. The parser current just extracts out the attributes and text */
    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        //let the parent parse first
        super.parse(parser, msgTree);
        this.from = msgTree.getAttributeValue("from");
        time = msgTree.getAttributeValue("stamp");
        this.text = msgTree.getText();
        return this;
    }

    /**
     * The time when the message was sent by the sender, NOT the time received.
     * Note that the time is local to your time zone, not in UTC or the sender's
     * timezone.  Thus, if you are in PST (GMT-800), then you will get when the message was sent in PST time.
     * @return the time the message was sent, null if none exists
     */
    public Date getTimeInLocal() {
        if (time == null) return null;
        if (format == null) {
            format = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        Date date;
        date = format.parse(time, new ParsePosition(0));
        return date;
    }

    /**
     * The time stamp string as returned by the remote server. This time stamp is in UTC.
     * The time stamp is formatted as an ISO date format.  To parse it, you can use something like:
     * SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
     * @return the time stamp string
     */
    public String getTime() {
        return time;
    }

    /** @return the sender of the message, null if none exists */
    public String getFrom() {
        return from;
    }

    /**
     * the text that is optionally associated with the message.
     * @return the text message or null if none exists
     */
    public String getText() {
        return text;
    }

    public int getMessageType() {
        return JabberCode.MSG_X_DELAY;
    }
}
