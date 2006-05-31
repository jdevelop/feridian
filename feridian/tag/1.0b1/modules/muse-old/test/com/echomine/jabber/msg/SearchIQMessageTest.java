package com.echomine.jabber.msg;

import com.echomine.jabber.DefaultMessageParser;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberUtil;
import junit.framework.TestCase;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.HashMap;
import java.util.List;

/**
 * Tests the jabber:iq:search message object
 */
public class SearchIQMessageTest extends TestCase {
    DefaultMessageParser parser = new DefaultMessageParser();

    /**
     * Tests to make sure that the get search fields works properly
     */
    public void testGetSearchFields() throws Exception {
        String streamXML = "<iq type='result' xmlns='jabber:client' from='users.jabber.org' to='abc@abc.com' id='1001'>" +
                "<query xmlns='jabber:iq:search'><first/><last/><nick/><email/><instructions>These are instructions</instructions>" +
                "</query></iq>";
        Element elem = JabberUtil.parseXmlStringToDOM(streamXML);
        SearchIQMessage msg = new SearchIQMessage();
        msg.parse(parser, elem);
        HashMap map = msg.getFields();
        assertTrue(map.containsKey("first"));
        assertTrue(map.containsKey("last"));
        assertTrue(map.containsKey("nick"));
        assertTrue(map.containsKey("email"));
        assertTrue(map.containsKey("instructions"));
        assertEquals("These are instructions", map.get("instructions"));
    }

    /**
     * tests that the addFields method is working properly
     */
    public void testAddFields() throws Exception {
        SearchIQMessage msg = new SearchIQMessage();
        HashMap map = new HashMap();
        map.put("first", "joan");
        map.put("last", "arc");
        map.put("nick", null);
        msg.addFields(map);
        Namespace ns = JabberCode.XMLNS_IQ_SEARCH;
        Element query = msg.getDOM().getChild("query", ns);
        assertNotNull(query.getChild("first", ns));
        assertNotNull(query.getChild("last", ns));
        assertNotNull(query.getChild("nick", ns));
        assertEquals("joan", query.getChildText("first", ns));
        assertEquals("arc", query.getChildText("last", ns));
        assertEquals("", query.getChildText("nick", ns));
    }

    /** tests the addField() method */
    public void testAddField() throws Exception {
        SearchIQMessage msg = new SearchIQMessage();
        msg.addField("first", "joan");
        msg.addField("last", "arc");
        msg.addField("nick", null);
        Namespace ns = JabberCode.XMLNS_IQ_SEARCH;
        Element query = msg.getDOM().getChild("query", ns);
        assertNotNull(query.getChild("first", ns));
        assertNotNull(query.getChild("last", ns));
        assertNotNull(query.getChild("nick", ns));
        assertEquals("joan", query.getChildText("first", ns));
        assertEquals("arc", query.getChildText("last", ns));
        assertEquals("", query.getChildText("nick", ns));
    }

    /** test the getResultItems() method */
    public void testGetResultItems() throws Exception {
        String streamXML = "<iq type='result' from='users.jabber.org' to='abc@abc.com' id='search2'>" +
                "<query xmlns='jabber:iq:search'><item jid='peterpan@jabber.org'><first>Peter</first>" +
                "<last>Pan</last><nick>peterpan</nick><email>peter.pan@fairytales.lit</email></item>" +
                "<item jid='stpeter@jabber.org'><first>Peter</first><last>Saint-Andre</last><nick>stpeter</nick>" +
                "<email>stpeter@jabber.org</email></item></query></iq>";
        Element elem = JabberUtil.parseXmlStringToDOM(streamXML);
        SearchIQMessage msg = new SearchIQMessage();
        msg.parse(parser, elem);
        List list = msg.getResultItems();
        assertNotNull(list);
        assertEquals(2, list.size());
        SearchItem item = (SearchItem) list.get(0);
        assertEquals("peterpan@jabber.org", item.getJID().toString());
        assertEquals("Peter", item.getFirst());
        assertEquals("Pan", item.getLast());
        assertEquals("peterpan", item.getNick());
        assertEquals("peter.pan@fairytales.lit", item.getEmail());
        item = (SearchItem) list.get(1);
        assertEquals("stpeter@jabber.org", item.getJID().toString());
        assertEquals("Peter", item.getFirst());
        assertEquals("Saint-Andre", item.getLast());
        assertEquals("stpeter", item.getNick());
        assertEquals("stpeter@jabber.org", item.getEmail());
    }

    /**
     * this tests that the parser has the message registered to parse the namespace
     */
    public void testParserSupportsMessage() throws Exception {
        assertTrue(parser.supportsParsingFor("query", JabberCode.XMLNS_IQ_SEARCH));
    }

    /** Tests the message type compliance */
    public void testMessageType() {
        SearchIQMessage msg = new SearchIQMessage();
        assertEquals(JabberCode.MSG_IQ_SEARCH, msg.getMessageType());
    }
}
