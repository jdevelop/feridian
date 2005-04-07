package com.echomine.jabber.msg;

import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberIQMessage;
import org.jdom.Element;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Submits and parses a Client Time message.  The message will return the time of the recipient that you sent the message to.
 * This message seems to only work with the server and not when you send it to a
 * user (somehow not supported).  Thus, current implementation will not allow you to create a message that contains your own
 * time information to send to the server.  When requesting time from a client is, the message will
 * implement letting you set values.
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0090.html">JEP-0090 1.0</a></b></p>
 */
public class TimeIQMessage extends JabberIQMessage implements JabberCode {
    private String tz;
    private String disp;
    private String utc;
    private SimpleDateFormat format;

    /**
     * this constructor is for messages with type.
     */
    public TimeIQMessage(String type) {
        super(type);
        //add in the query element
        getDOM().addContent(new Element("query", XMLNS_IQ_TIME));
    }

    /**
     * defaults to iq type get
     */
    public TimeIQMessage() {
        this(TYPE_GET);
    }

    /**
     * a convenience method that gives you the client's current time
     * in the your own time zone.  Thus, it takes the UTC time and applies your time zone.
     * If you wish to get the client's local time, then either format
     * the date yourself OR you can also call getDisplay() to retrieve the client-provided
     * time string (which is in the client's time zone, not yours).
     */
    public Date getTimeInLocal() {
        String tutc = getUTC();
        if (tutc == null) return null;
        if (format == null) {
            format = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        Date date;
        date = format.parse(tutc, new ParsePosition(0));
        return date;
    }

    /**
     * return the UTC time string as returned by the client
     */
    public String getUTC() {
        if (utc != null) return utc;
        Element query = getDOM().getChild("query", XMLNS_IQ_TIME);
        if (query != null)
            utc = query.getChildText("utc", XMLNS_IQ_TIME);
        return utc;
    }

    /**
     * get the timezone that the client is in
     */
    public String getTimeZone() {
        if (tz != null) return tz;
        Element query = getDOM().getChild("query", XMLNS_IQ_TIME);
        if (query != null)
            tz = query.getChildText("tz", XMLNS_IQ_TIME);
        return tz;
    }

    /**
     * retrieve the time display in a preformatted string returned by the client.
     * The time is in local time of the client (ie. using client's time zone, not yours).
     */
    public String getDisplay() {
        if (disp != null) return disp;
        //retrieve the <display> tag
        Element query = getDOM().getChild("query", XMLNS_IQ_TIME);
        if (query != null)
            disp = query.getChildText("display", XMLNS_IQ_TIME);
        return disp;
    }

    public int getMessageType() {
        return MSG_IQ_TIME;
    }
}
