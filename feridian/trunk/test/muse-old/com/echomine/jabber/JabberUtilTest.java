package com.echomine.jabber;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.TimeZone;

import com.echomine.common.ParseException;

/**
 * tests the jabber util methods
 */
public class JabberUtilTest extends TestCase {
    /**
     * checks that the date parsing is doing the right job
     */
    public void testParseDate() throws Exception {
        Calendar cal = JabberUtil.parseDate("2003-08-29");
        assertEquals(2003, cal.get(Calendar.YEAR));
        assertEquals(7, cal.get(Calendar.MONTH));
        assertEquals(29, cal.get(Calendar.DAY_OF_MONTH));
        try {
            JabberUtil.parseDate("2003-8-29");
            fail("Parsing of date should have failed");
        } catch (ParseException ex) {
        }
    }

    /**
     * checks that the time parsing is doing the right job
     */
    public void testParseTime() throws Exception {
        Calendar cal = JabberUtil.parseTime("12:20:33Z");
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, cal.get(Calendar.MINUTE));
        assertEquals(33, cal.get(Calendar.SECOND));
        assertEquals("GMT", cal.getTimeZone().getID());
        cal = JabberUtil.parseTime("12:20:33");
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, cal.get(Calendar.MINUTE));
        assertEquals(33, cal.get(Calendar.SECOND));
        cal = JabberUtil.parseTime("12:20:33.333");
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, cal.get(Calendar.MINUTE));
        assertEquals(33, cal.get(Calendar.SECOND));
        cal = JabberUtil.parseTime("12:20:33.333Z");
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, cal.get(Calendar.MINUTE));
        assertEquals(33, cal.get(Calendar.SECOND));
        cal = JabberUtil.parseTime("12:20:33-08:00");
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, cal.get(Calendar.MINUTE));
        assertEquals(33, cal.get(Calendar.SECOND));
        assertEquals("GMT-08:00", cal.getTimeZone().getID());
        cal = JabberUtil.parseTime("12:20:33+08:00");
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, cal.get(Calendar.MINUTE));
        assertEquals(33, cal.get(Calendar.SECOND));
        assertEquals("GMT+08:00", cal.getTimeZone().getID());
        cal = JabberUtil.parseTime("12:20:33.333+08:00");
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, cal.get(Calendar.MINUTE));
        assertEquals(33, cal.get(Calendar.SECOND));
        assertEquals("GMT+08:00", cal.getTimeZone().getID());
        try {
            cal = JabberUtil.parseTime("12:20:33+0800");
            fail("Parsing should have failed");
        } catch (ParseException ex) {
        }
    }

    /**
     * checks that the datetime parsing is doing the right job
     */
    public void testParseDateTime() throws Exception {
        Calendar cal = JabberUtil.parseDateTime("2003-12-21T12:20:33Z");
        assertEquals(2003, cal.get(Calendar.YEAR));
        assertEquals(11, cal.get(Calendar.MONTH));
        assertEquals(21, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, cal.get(Calendar.MINUTE));
        assertEquals(33, cal.get(Calendar.SECOND));
        assertEquals("GMT", cal.getTimeZone().getID());
        cal = JabberUtil.parseDateTime("2003-12-21T12:20:33-08:00");
        assertEquals(2003, cal.get(Calendar.YEAR));
        assertEquals(11, cal.get(Calendar.MONTH));
        assertEquals(21, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, cal.get(Calendar.MINUTE));
        assertEquals(33, cal.get(Calendar.SECOND));
        assertEquals("GMT-08:00", cal.getTimeZone().getID());
        cal = JabberUtil.parseDateTime("2003-12-21T12:20:33+08:00");
        assertEquals(2003, cal.get(Calendar.YEAR));
        assertEquals(11, cal.get(Calendar.MONTH));
        assertEquals(21, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, cal.get(Calendar.MINUTE));
        assertEquals(33, cal.get(Calendar.SECOND));
        assertEquals("GMT+08:00", cal.getTimeZone().getID());
        cal = JabberUtil.parseDateTime("2003-12-21T12:20:33.333+08:00");
        assertEquals(2003, cal.get(Calendar.YEAR));
        assertEquals(11, cal.get(Calendar.MONTH));
        assertEquals(21, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(12, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(20, cal.get(Calendar.MINUTE));
        assertEquals(33, cal.get(Calendar.SECOND));
        assertEquals("GMT+08:00", cal.getTimeZone().getID());
        try {
            JabberUtil.parseDateTime("2003-12-2112:20:33+08:00");
            fail("Parsing of datetime should have failed");
        } catch (ParseException ex) {
        }
        try {
            JabberUtil.parseDateTime("2003-12-21T12:20:33");
            fail("Parsing of datetime should have failed");
        } catch (ParseException ex) {
        }
    }

    /**
     * tests the parsing of timezones
     */
    public void testParseTimeZone() {
        TimeZone tz = JabberUtil.parseTimeZone(null);
        assertEquals("GMT", tz.getID());
        tz = JabberUtil.parseTimeZone("Z");
        assertEquals("GMT", tz.getID());
        tz = JabberUtil.parseTimeZone("+08:00");
        assertEquals("GMT+08:00", tz.getID());
    }

    /**
     * tests formatting of datetime into a string
     */
    public void testFormatDateTime() throws Exception {
        Calendar cal = JabberUtil.parseDateTime("2003-12-21T12:20:33Z");
        assertEquals("2003-12-21T12:20:33Z", JabberUtil.formatDateTime(cal));
        cal = JabberUtil.parseDateTime("2003-12-21T12:20:33+07:00");
        assertEquals("2003-12-21T12:20:33+07:00", JabberUtil.formatDateTime(cal));
    }
}
