package com.echomine.xmlrpc;

import org.jdom.Element;
import org.jdom.Namespace;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * the date serializer will work with parsing and serializing the Date format.
 * Currently, the way the date time is parsed defaults to the time zone that
 * you are in.  XMLRPC specs indicate that the date timezone should be set
 * to the timezone of the server you are obtaining the response from.
 * For instance, if the remote time is sent as a UTC time (ie. 12pm) and you
 * are in PST time zone (GMT-800), then you should first set the timezone
 * of the DateSerializer to the remote server's timezone before any requests
 * are made.  Then you can make the request and retrieve the time.
 * The time will then be in that timezone (ie. 12pm GMT).  To convert to your
 * local timezone, you should set the timezone for the Date object you retrieved
 * from here and then work with it.  It's a little complicated, but when specs
 * are unclear, the developer will have to work a bit more.
 * If you want to have a different timezone, what you need to do is first set
 * the time zone for the SerializerFactory before using XMLRPC inside your code.
 * <br/>Example:
 * <pre>
 * &lt;dateTime.iso8601>19980717T14:08:55&lt;/dateTime.iso8601>
 * </pre>
 */
public class DateSerializer implements Serializer, Deserializer {
    public static final String NAME = "dateTime.iso8601";
    private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");

    /**
     * Serializes the Date object into an XML element.
     * The object must be of type Date or its derivative.
     * @param data the Date object to be serialized
     * @param ns optional namespace, null if none
     * @return the element data containing the date representation
     * @throws IllegalArgumentException if object is not of Date type
     */
    public Element serialize(Object data, Namespace ns) {
        if (!(data instanceof Date))
            throw new IllegalArgumentException("Object must be of type Date or its subclass");
        Element root = new Element(NAME, ns);
        synchronized (format) {
            root.setText(format.format((Date) data));
        }
        return root;
    }

    /**
     * deserializes the element data into a date object.
     * The method is synchronized to the internal date format so that
     * multithreading working through this method will not cause any problems.
     * @param elem the data for the date time
     * @return a Date object representing the datetime, or null if the date cannot be parsed
     */
    public Object deserialize(Element elem) {
        //it's the correct tag, get the data inside
        String text = elem.getText();
        if (text == null) return null;
        synchronized (format) {
            return format.parse(text, new ParsePosition(0));
        }
    }

    /** sets the time zone to be used. By default, the timezone is your default local time zone */
    public void setTimeZone(TimeZone tz) {
        if (tz == null) throw new IllegalArgumentException("Timezone variable cannot be null");
        format.setTimeZone(tz);
    }
}
