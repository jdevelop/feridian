package com.echomine.xmpp.packet;

import java.io.StringReader;
import java.util.Locale;

import com.echomine.jibx.JiBXUtil;
import com.echomine.xmpp.ErrorCode;
import com.echomine.xmpp.NSI;
import com.echomine.xmpp.XMPPTestCase;

/**
 * This will test both stanza and stream error packets.
 */
public class ErrorPacketTest extends XMPPTestCase {
    /**
     * tests the stream containing all the packet data
     */
    public void testUnmarshallAllData() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>" + "\n\t<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
                + "\n\t<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>diagnostic</text>" + "\n\t<escape-your-data xmlns='application-ns'/></stream:error>";
        StringReader reader = new StringReader(xml);
        ErrorPacket msg = (ErrorPacket) JiBXUtil.unmarshallObject(reader, ErrorPacket.class);
        assertEquals(ErrorCode.S_XML_NOT_WELL_FORMED, msg.getCondition());
        assertEquals("diagnostic", msg.getText());
        assertEquals("escape-your-data", msg.getApplicationCondition().getName());
        assertEquals(Locale.ENGLISH, msg.getTextLocale());
    }

    public void testUnmarshallWithNoApplicationCondition() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>\n\t" + "<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
                + "<text xml:lang='en' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>diagnostic</text>" + "</stream:error>";
        StringReader reader = new StringReader(xml);
        ErrorPacket msg = (ErrorPacket) JiBXUtil.unmarshallObject(reader, ErrorPacket.class);
        assertEquals(ErrorCode.S_XML_NOT_WELL_FORMED, msg.getCondition());
        assertEquals("diagnostic", msg.getText());
        assertEquals(Locale.ENGLISH, msg.getTextLocale());
        assertNull(msg.getApplicationCondition());
    }

    public void testUnmarshallConditionOnly() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>\n\t" + "<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>" + "</stream:error>";
        StringReader reader = new StringReader(xml);
        ErrorPacket msg = (ErrorPacket) JiBXUtil.unmarshallObject(reader, ErrorPacket.class);
        assertEquals(ErrorCode.S_XML_NOT_WELL_FORMED, msg.getCondition());
        assertNull(msg.getText());
        assertNull(msg.getApplicationCondition());
        assertNull(msg.getTextLocale());
    }

    public void testUnmarshallNoText() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>\n\t" + "<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
                + "<escape-your-data xmlns='application-ns'/></stream:error>";
        StringReader reader = new StringReader(xml);
        ErrorPacket msg = (ErrorPacket) JiBXUtil.unmarshallObject(reader, ErrorPacket.class);
        assertEquals(ErrorCode.S_XML_NOT_WELL_FORMED, msg.getCondition());
        assertNull(msg.getText());
        assertNull(msg.getTextLocale());
        assertEquals("escape-your-data", msg.getApplicationCondition().getName());
    }

    public void testMarshallAllData() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>\n\t" + "<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
                + "<text xmlns='urn:ietf:params:xml:ns:xmpp-streams'>diagnostic</text>" + "<escape-your-data xmlns='application-ns'/></stream:error>";
        StringReader reader = new StringReader(xml);
        ErrorPacket packet = new ErrorPacket();
        packet.setCondition(ErrorCode.S_XML_NOT_WELL_FORMED);
        packet.setApplicationCondition(new NSI("escape-your-data", "application-ns"));
        packet.setText("diagnostic");
        JiBXUtil.marshallObject(writer, packet);
        compare(reader);
    }

    public void testMarshallTextWithLocale() throws Exception {
        String xml = "<stream:error xmlns:stream='http://etherx.jabber.org/streams'>\n\t" + "<xml-not-well-formed xmlns='urn:ietf:params:xml:ns:xmpp-streams'/>"
                + "<text xml:lang='en-us' xmlns='urn:ietf:params:xml:ns:xmpp-streams'>diagnostic</text>" + "<escape-your-data xmlns='application-ns'/></stream:error>";
        StringReader reader = new StringReader(xml);
        ErrorPacket packet = new ErrorPacket();
        packet.setCondition(ErrorCode.S_XML_NOT_WELL_FORMED);
        packet.setApplicationCondition(new NSI("escape-your-data", "application-ns"));
        packet.setText("diagnostic");
        packet.setTextLocale(Locale.US);
        JiBXUtil.marshallObject(writer, packet);
        compare(reader);
    }
}
