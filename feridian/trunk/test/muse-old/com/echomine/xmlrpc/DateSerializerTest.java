package com.echomine.xmlrpc;

import junit.framework.TestCase;
import org.jdom.Element;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * tests the date serializer
 */
public class DateSerializerTest extends TestCase {
    private DateSerializer serializer = new DateSerializer();

    /**
     * Tests the serialization of the boolean data
     */
    public void testDateSerialization() {
        Calendar cal = new GregorianCalendar(1998, 6, 17, 14, 8, 55);
        Element elem = serializer.serialize(cal.getTime(), null);
        assertEquals("dateTime.iso8601", elem.getName());
        assertEquals("19980717T14:08:55", elem.getText());
    }

    /** tests that setting the timezone will change the serialized date */
    public void testTimeZoneSerialization() {
        DateSerializer serializer = new DateSerializer();
        Calendar cal = new GregorianCalendar(1998, 6, 17, 14, 8, 55);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        serializer.setTimeZone(TimeZone.getTimeZone("PST"));
        Element elem = serializer.serialize(cal.getTime(), null);
        assertEquals("dateTime.iso8601", elem.getName());
        assertEquals("19980717T07:08:55", elem.getText());
    }

    /** tests the deserialization of the string data */
    public void testDateDerialization() {
        Element elem = new Element("dateTime.iso8601").setText("19980717T14:08:55");
        Date date = (Date) serializer.deserialize(elem);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(1998, cal.get(Calendar.YEAR));
        assertEquals(6, cal.get(Calendar.MONTH));
        assertEquals(17, cal.get(Calendar.DATE));
        assertEquals(14, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(8, cal.get(Calendar.MINUTE));
        assertEquals(55, cal.get(Calendar.SECOND));
    }
}
