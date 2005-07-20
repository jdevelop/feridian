package com.echomine.xmpp.impl;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import com.echomine.net.ConnectionContext;
import com.echomine.net.MockSocket;
import com.echomine.net.MockSocketConnector;
import com.echomine.xmpp.IXMPPConnection;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStreamContext;

/**
 * Tests the main connection handler class
 */
public class XMPPConnectionHandlerTest extends TestCase {
    XMPPConnectionImpl conn;
    MockSocketConnector connector;
    XMPPConnectionHandler handler;
    XMPPSessionContext sessCtx;
    XMPPStreamContext streamCtx;
    MockSocket socket;
    ConnectionContext connectionCtx;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        sessCtx = new XMPPSessionContext();
        streamCtx = new XMPPStreamContext();
        socket = new MockSocket("example.com", IXMPPConnection.DEFAULT_XMPP_PORT);
        connectionCtx = new ConnectionContext("example.com", IXMPPConnection.DEFAULT_XMPP_PORT);
        connector = new MockSocketConnector();
        handler = new XMPPConnectionHandler(sessCtx, streamCtx);
        conn = new XMPPConnectionImpl(connector, handler);
    }

    public void testHandshakeSuccess() throws Exception {
        String reply = "<stream:stream id='c2s_123' from='example.com' version='1.0' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>"
                + "<stream:features><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/></stream:features>";
        MockSocket socket = connector.getSocket("example.com", IXMPPConnection.DEFAULT_XMPP_PORT);
        socket.setInputStream(new ByteArrayInputStream(reply.getBytes()));
        handler.handshake(socket, connectionCtx);
        assertEquals("1.0", sessCtx.getVersion());
        assertEquals("example.com", sessCtx.getHostName());
        assertEquals("c2s_123", sessCtx.getSessionId());
        assertNotNull(streamCtx.getSocket());
        assertNotNull(streamCtx.getWriter());
        assertNotNull(streamCtx.getUnmarshallingContext());
        assertTrue(streamCtx.getFeatures().isBindingSupported());
    }
}
