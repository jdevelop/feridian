package com.echomine.jabber.msg;

import com.echomine.jabber.DefaultMessageParser;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberIQMessage;
import com.echomine.jabber.JabberUtil;
import junit.framework.TestCase;
import org.jdom.Element;

import java.util.Calendar;

/**
 * Tests the vacation message
 */
public class VacationIQMessageTest extends TestCase {
    DefaultMessageParser parser = new DefaultMessageParser();

    /**
     * tests the parsing and retrieving of vacation settings returned from the server.
     */
    public void testGetVacationSettings() throws Exception {
        String streamXML = "<iq type='result' id='get1'><query xmlns='http://www.jabber.org/protocol/vacation'>" +
                "<start>2003-07-06T10:30:00+10:00</start><end>2003-07-13T08:00:00+10:00</end>" +
                "<message>vacation message</message></query></iq>";
        VacationIQMessage msg = new VacationIQMessage();
        Element elem = JabberUtil.parseXmlStringToDOM(streamXML);
        msg.parse(parser, elem);
        Calendar cal = msg.getStartDate();
        assertEquals(2003, cal.get(Calendar.YEAR));
        assertEquals(6, cal.get(Calendar.MONTH));
        assertEquals(6, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals("GMT+10:00", cal.getTimeZone().getID());
        cal = msg.getEndDate();
        assertEquals(2003, cal.get(Calendar.YEAR));
        assertEquals(6, cal.get(Calendar.MONTH));
        assertEquals(13, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(8, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, cal.get(Calendar.MINUTE));
        assertEquals(0, cal.get(Calendar.SECOND));
        assertEquals("GMT+10:00", cal.getTimeZone().getID());
        assertEquals("vacation message", msg.getVacationMessage());
    }

    /**
     * The request vacation message should simply be an empty get IQ query
     */
    public void testCreateRequestVacationMessage() {
        VacationIQMessage msg = VacationIQMessage.createRequestVacationMessage();
        assertEquals(JabberIQMessage.TYPE_GET, msg.getType());
    }

    /**
     * The remove vacation message should simply be an empty set IQ query
     */
    public void testCreateRemoveVacationMessage() {
        VacationIQMessage msg = VacationIQMessage.createRemoveVacationMessage();
        assertEquals(JabberIQMessage.TYPE_SET, msg.getType());
    }

    /**
     * The set vacation message should set the data
     */
    public void testCreateSetVacationMessage() throws Exception {
        Calendar startDate = JabberUtil.parseDateTime("2003-06-01T12:00:00Z");
        Calendar endDate = JabberUtil.parseDateTime("2003-06-05T08:00:00Z");
        String message = "vacation message";
        VacationIQMessage msg = VacationIQMessage.createSetVacationMessage(startDate, endDate, message);
        assertEquals(JabberIQMessage.TYPE_SET, msg.getType());
        assertEquals(startDate, msg.getStartDate());
        assertEquals(endDate, msg.getEndDate());
        assertEquals("vacation message", msg.getVacationMessage());
    }

    /**
     * this tests that the parser has the message registered to parse the namespace
     */
    public void testParserSupportsMessage() throws Exception {
        assertTrue(parser.supportsParsingFor("query", JabberCode.XMLNS_IQ_VACATION));
    }

    /** tests message type compliance to make sure it is returning the proper type */
    public void testMessageType() {
        VacationIQMessage msg = new VacationIQMessage();
        assertEquals(JabberCode.MSG_IQ_VACATION, msg.getMessageType());
    }
}
