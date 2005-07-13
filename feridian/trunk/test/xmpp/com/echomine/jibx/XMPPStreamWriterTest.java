package com.echomine.jibx;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

/**
 * Tests the xmpp stream writer to see that it does handles XMPP special cases
 * properly
 */
public class XMPPStreamWriterTest extends TestCase {
    private XMPPStreamWriter writer;

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        writer = new XMPPStreamWriter();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * After reset, check to see whether the main namespaces are still
     * registered and stored.
     */
    public void testNamespacesValidAfterReset() throws Exception {
        writer.setOutput(new ByteArrayOutputStream());
        writer.startTagNamespaces(2, "stream", new int[] { 2, 3 }, new String[] { "stream", "" });
        assertEquals("stream", writer.getNamespacePrefix(2));
        assertEquals("", writer.getNamespacePrefix(3));
    }
}
