package com.echomine.jabber.msg;

import com.echomine.jabber.DefaultMessageParser;
import com.echomine.jabber.JID;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberUtil;
import junit.framework.TestCase;
import org.jdom.Element;

import java.util.List;

/**
 * This class tests both Service Discovery (Info and Item) classes
 */
public class ServiceDiscoveryIQMessageTest extends TestCase {
    DefaultMessageParser parser = new DefaultMessageParser();

    /**
     * Tests parsing and encoding of the Service Info message
     */
    public void testServiceInfoParsingAndEncoding() throws Exception {
        ServiceInfoIQMessage msg = new ServiceInfoIQMessage();
        msg.setType(ServiceItemsIQMessage.TYPE_RESULT);
        msg.addIdentity(new ServiceIdentity("conference", "Conference", "text"));
        msg.addIdentity(new ServiceIdentity("directory", "Directory", "room"));
        msg.addFeature("gc-1.0");
        msg.addFeature("http://jabber.org/protocol/muc");
        msg.addFeature("jabber:iq:register");
        String xmlStr = msg.encode();
        Element elem = JabberUtil.parseXmlStringToDOM(xmlStr);
        msg = new ServiceInfoIQMessage();
        msg.parse(parser, elem);
        List list = msg.getFeatures();
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("gc-1.0", (String) list.get(0));
        assertEquals("http://jabber.org/protocol/muc", (String) list.get(1));
        assertEquals("jabber:iq:register", (String) list.get(2));
        list = msg.getIdentities();
        assertNotNull(list);
        assertEquals(2, list.size());
        ServiceIdentity ident = (ServiceIdentity) list.get(0);
        assertEquals("conference", ident.getCategory());
        assertEquals("Conference", ident.getName());
        assertEquals("text", ident.getType());
        ident = (ServiceIdentity) list.get(1);
        assertEquals("directory", ident.getCategory());
        assertEquals("Directory", ident.getName());
        assertEquals("room", ident.getType());
    }

    /**
     * tests the result message returned from the server
     * @throws Exception
     */
    public void testServiceInfoResult() throws Exception {
        ServiceInfoIQMessage msg = new ServiceInfoIQMessage();
        msg.setType(ServiceItemsIQMessage.TYPE_RESULT);
        msg.addIdentity(new ServiceIdentity("conference", "Conference", "text"));
        msg.addIdentity(new ServiceIdentity("directory", "Directory", "room"));
        msg.addFeature("gc-1.0");
        msg.addFeature("http://jabber.org/protocol/muc");
        msg.addFeature("jabber:iq:register");
        List list = msg.getFeatures();
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("gc-1.0", (String) list.get(0));
        assertEquals("http://jabber.org/protocol/muc", (String) list.get(1));
        assertEquals("jabber:iq:register", (String) list.get(2));
        list = msg.getIdentities();
        assertNotNull(list);
        assertEquals(2, list.size());
        ServiceIdentity ident = (ServiceIdentity) list.get(0);
        assertEquals("conference", ident.getCategory());
        assertEquals("Conference", ident.getName());
        assertEquals("text", ident.getType());
        ident = (ServiceIdentity) list.get(1);
        assertEquals("directory", ident.getCategory());
        assertEquals("Directory", ident.getName());
        assertEquals("room", ident.getType());
    }

    /**
     * Tests that the default service info type
     */
    public void testDefaultServiceInfoType() throws Exception {
        ServiceInfoIQMessage msg = new ServiceInfoIQMessage();
        assertEquals(ServiceInfoIQMessage.TYPE_GET, msg.getType());
        Element dom = msg.getDOM();
        Element query = dom.getChild("query", JabberCode.XMLNS_IQ_DISCO_INFO);
        assertNotNull(query);
        assertTrue(query.getChildren().size() == 0);
    }

    /** tests the parsing and encoding of the Disco Items message */
    public void testServiceItemsParsingAndEncoding() throws Exception {
        ServiceItemsIQMessage msg = new ServiceItemsIQMessage();
        msg.setType(ServiceItemsIQMessage.TYPE_RESULT);
        msg.setNode("music");
        msg.addItem(new ServiceItem(new JID("people.shakespeare.lit"), "Directory of Characters"));
        msg.addItem(new ServiceItem(new JID("catalog.shakespeare.lit"), "Buy Shakespeare Stuff!"));
        String xmlStr = msg.encode();
        Element elem = JabberUtil.parseXmlStringToDOM(xmlStr);
        msg = new ServiceItemsIQMessage();
        msg.parse(parser, elem);
        List list = msg.getItems();
        assertNotNull(list);
        assertEquals(ServiceItemsIQMessage.TYPE_RESULT, msg.getType());
        assertEquals("music", msg.getNode());
        assertEquals(2, list.size());
        assertEquals("people.shakespeare.lit", ((ServiceItem) list.get(0)).getJID().toString());
        assertEquals("Directory of Characters", ((ServiceItem) list.get(0)).getName());
        assertEquals("catalog.shakespeare.lit", ((ServiceItem) list.get(1)).getJID().toString());
        assertEquals("Buy Shakespeare Stuff!", ((ServiceItem) list.get(1)).getName());
    }

    /**
     * Tests that the service items message query can have nodes
     */
    public void testServiceItemsQueryWithNode() throws Exception {
        ServiceItemsIQMessage msg = new ServiceItemsIQMessage();
        msg.setNode("music");
        assertEquals("music", msg.getNode());
    }

    /**
     * tests the result message returned from the server
     * @throws Exception
     */
    public void testServiceItemsResult() throws Exception {
        ServiceItemsIQMessage msg = new ServiceItemsIQMessage();
        msg.setType(ServiceItemsIQMessage.TYPE_RESULT);
        msg.addItem(new ServiceItem(new JID("people.shakespeare.lit"), "Directory of Characters"));
        msg.addItem(new ServiceItem(new JID("catalog.shakespeare.lit"), "Buy Shakespeare Stuff!"));
        List list = msg.getItems();
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("people.shakespeare.lit", ((ServiceItem) list.get(0)).getJID().toString());
        assertEquals("Directory of Characters", ((ServiceItem) list.get(0)).getName());
        assertEquals("catalog.shakespeare.lit", ((ServiceItem) list.get(1)).getJID().toString());
        assertEquals("Buy Shakespeare Stuff!", ((ServiceItem) list.get(1)).getName());
    }

    /**
     * Tests that the default service items type
     */
    public void testDefaultServiceItemsType() throws Exception {
        ServiceItemsIQMessage msg = new ServiceItemsIQMessage();
        assertEquals(ServiceItemsIQMessage.TYPE_GET, msg.getType());
        Element dom = msg.getDOM();
        Element query = dom.getChild("query", JabberCode.XMLNS_IQ_DISCO_ITEMS);
        assertNotNull(query);
        assertTrue(query.getChildren().size() == 0);
    }

    /**
     * this tests that the parser has the message registered to parse the namespace
     */
    public void testParserSupportsMessage() throws Exception {
        assertTrue(parser.supportsParsingFor("query", JabberCode.XMLNS_IQ_DISCO_INFO));
        assertTrue(parser.supportsParsingFor("query", JabberCode.XMLNS_IQ_DISCO_ITEMS));
    }

    /**
     * Test to make sure that the message type is set properly
     * @throws java.lang.Exception
     */
    public void testMessageType() throws Exception {
        ServiceInfoIQMessage msg = new ServiceInfoIQMessage();
        assertEquals(JabberCode.MSG_IQ_DISCO_INFO, msg.getMessageType());
        ServiceItemsIQMessage imsg = new ServiceItemsIQMessage();
        assertEquals(JabberCode.MSG_IQ_DISCO_ITEMS, imsg.getMessageType());
    }
}
