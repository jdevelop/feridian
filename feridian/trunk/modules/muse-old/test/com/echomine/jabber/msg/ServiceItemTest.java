package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JID;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberUtil;
import junit.framework.TestCase;
import org.jdom.Element;

/**
 * test the service item class
 */
public class ServiceItemTest extends TestCase {
    /**
     * tests that the JID shall never be null under any circumstances.
     * And test that the class does proper checking for such things
     */
    public void testJIDNeverNull() throws Exception {
        try {
            new ServiceItem((JID) null);
            fail("JID cannot be null.  Must check and throw exception");
        } catch (Throwable thr) {
        }
        try {
            new ServiceItem(null, null);
            fail("JID cannot be null.  Must check and throw exception");
        } catch (Throwable thr) {
        }
        try {
            new ServiceItem(null, null, null);
            fail("JID cannot be null.  Must check and throw exception");
        } catch (Throwable thr) {
        }
        Element elem = JabberUtil.parseXmlStringToDOM("<item xmlns='" + JabberCode.XMLNS_IQ_DISCO_ITEMS.getURI() + "'/>");
        try {
            new ServiceItem(elem);
            fail("JID cannot be null.  Must check and throw exception");
        } catch (ParseException ex) {
        }
    }

    /**
     * JEP specifies that when setting the node, if the node is empty string, it should be treated as if the
     * attribute does not exist.
     */
    public void testSetNode() throws Exception {
        ServiceItem item = new ServiceItem(new JID("abc@def.com"));
        item.setNode("");
        assertNull(item.getNode());
        item.setNode(null);
        assertNull(item.getNode());
        item.setNode(" ");
        assertEquals(" ", item.getNode());
    }

    /**
     * tests parsing and encoding of the data
     */
    public void testParsingAndEncoding() throws Exception {
        ServiceItem item = new ServiceItem(new JID("abc@def.com"), "name", "node");
        Element elem = item.encode();
        item = new ServiceItem(elem);
        assertEquals(new JID("abc@def.com"), item.getJID());
        assertEquals("name", item.getName());
        assertEquals("node", item.getNode());
    }
}
