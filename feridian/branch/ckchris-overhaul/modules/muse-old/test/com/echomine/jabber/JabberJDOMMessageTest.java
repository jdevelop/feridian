package com.echomine.jabber;

import junit.framework.TestCase;
import org.jdom.Element;

import java.util.HashMap;
import java.util.NoSuchElementException;

public class JabberJDOMMessageTest extends TestCase {
    private final String CONTENT = "Let's pretend I'm a JDOM Message!";

    /**
     * This tests a bug that occurred when JabberJDOMMessage.encode() was
     * called on messages which contained textual data and no child elements.
     * PGPEncryptedXMessage is an example of this type of message.
     */
    public void testJDOMMessageWithTextOnly() throws Exception {
        String xml = "<x xmlns=\"jabber:x:encrypted\">" + CONTENT + "</x>";
        Element elem = JabberUtil.parseXmlStringToDOM(xml);
        JabberJDOMMessage msg = new JabberJDOMMessage(elem);
        Element root = msg.getDOM();
        assertEquals("x", root.getName());
        assertEquals(CONTENT, root.getText());
    }

    public void testMessageIDSerialization() throws Exception {
        Element elem = new Element("message");
        JabberJDOMMessage msg = new JabberJDOMMessage(elem);
        String xml = msg.toString();
        assertTrue(xml.indexOf("id=") > 0);
    }

    /**
     * This test case illustrates a bug where when a child element of the root
     * contains an empty namespace, the empty namespace will not be outputted.
     * This causes the outputted data to think that the should-be-empty-namespace
     * is actually the same namespace as the root. For instance, if xml data is:
     * <p/>
     * &lt;Root xmlns="ns">
     * &lt;Child xmlns=""/ >
     * &lt;/Root>
     * <p/>
     * The output data will become:
     * <p/>
     * &lt;Root xmlns="ns">
     * &lt;Child/ >
     * &lt;/Root>
     * <p/>
     * The output text is obvious wrong.
     */
    public void testEncodeWithEmptyNamespace() throws Exception {
        String xml = "<Root xmlns='myroot'><Child xmlns=''/></Root>";
        Element root = JabberUtil.parseXmlStringToDOM(xml);
        JabberJDOMMessage msg = new JabberJDOMMessage(root);
        String newxml = msg.toString();
        root = JabberUtil.parseXmlStringToDOM(newxml);
        assertEquals("myroot", root.getNamespaceURI());
        assertNotNull(root.getChild("Child"));
    }

    public void testEncodeWithEmptyXMessagesHashMap() throws Exception {
        String xml = "<Root xmlns='myroot'><Child xmlns=''/></Root>";
        Element root = JabberUtil.parseXmlStringToDOM(xml);
        JabberJDOMMessage msg = new JabberJDOMMessage(root);
        msg.setXMessages(new HashMap());
        try {
            assertTrue(msg.getXMessages().isEmpty());
            msg.encode();
        } catch (NoSuchElementException ex) {
            fail("XMessages is empty should not throw an exception");
        }
    }
}
