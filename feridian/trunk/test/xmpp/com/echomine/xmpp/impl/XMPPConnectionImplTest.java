package com.echomine.xmpp.impl;

import com.echomine.net.ConnectionEvent;
import com.echomine.net.ConnectionException;
import com.echomine.net.HandshakeFailedException;
import com.echomine.net.MockConnectionListener;
import com.echomine.net.MockSocketConnector;
import com.echomine.xmpp.IXMPPConnection;
import com.echomine.xmpp.MockXMPPConnectionHandler;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPTestCase;

/**
 * Connection test
 */
public class XMPPConnectionImplTest extends XMPPTestCase {
    XMPPConnectionImpl conn;
    MockXMPPConnectionHandler handler;
    MockSocketConnector connector;
    MockConnectionListener l;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        handler = new MockXMPPConnectionHandler();
        handler.getStreamContext().setWriter(writer);
        handler.getStreamContext().setUnmarshallingContext(uctx);
        connector = new MockSocketConnector(handler);
        conn = new XMPPConnectionImpl(connector, handler);
        l = new MockConnectionListener();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        conn.disconnect();
    }

    /**
     * Tests successful connection. The connection object should simply be
     * connected
     */
    public void testSuccessfulConnect() throws Exception {
        conn.connect("example.com", IXMPPConnection.DEFAULT_XMPP_PORT, true);
        assertTrue(conn.isConnected());
    }

    public void testSuccessfulDisconnect() throws Exception {
        conn.connect("example.com", IXMPPConnection.DEFAULT_XMPP_PORT, true);
        assertTrue(conn.isConnected());
        conn.disconnect();
        assertFalse(conn.isConnected());
    }

    public void testConnectionFailure() throws Exception {
        connector.setConnectionFailure(true);
        try {
            conn.connect("example.com", IXMPPConnection.DEFAULT_XMPP_PORT, true);
            fail("Connection should fail but did not");
        } catch (ConnectionException ex) {
        }
    }

    public void testHandshakeFailure() throws Exception {
        connector.setConnectionFailure(false);
        handler.setFailHandshake(true);
        try {
            conn.connect("example.com", IXMPPConnection.DEFAULT_XMPP_PORT, true);
            fail("Handshake should have failed but did not");
        } catch (ConnectionException ex) {
            if (!(ex instanceof HandshakeFailedException))
                fail("Exception thrown should be HandshakeFailedException");
        }
        assertFalse(conn.isConnected());
    }

    public void testSuccessfulConnectionUsingListener() throws Exception {
        conn.addConnectionListener(l);
        conn.connect("example.com", IXMPPConnection.DEFAULT_XMPP_PORT, false);
        Thread thread = new Thread() {
            public void run() {
                conn.disconnect();
            }
        };
        thread.start();
        l.waitForConnectionClose();
        assertTrue(l.isStartingCalled());
        assertTrue(l.isEstablishedCalled());
        assertTrue(l.isClosedCalled());
        assertEquals(ConnectionEvent.CONNECTION_CLOSED, l.getCloseEvent().getStatus());
    }

    public void testHandshakeFailureUsingListener() throws Exception {
        handler.setFailHandshake(true);
        conn.addConnectionListener(l);
        try {
            conn.connect("example.com", IXMPPConnection.DEFAULT_XMPP_PORT, true);
            fail("Handshake should have failed but did not");
        } catch (ConnectionException ex) {
        }
        assertTrue(l.isStartingCalled());
        assertFalse(l.isEstablishedCalled());
        assertTrue(l.isClosedCalled());
        assertEquals(ConnectionEvent.CONNECTION_ERRORED, l.getCloseEvent().getStatus());
    }

    public void testSuccessfulLogin() throws Exception {
        
        conn.login("romeo", "password".toCharArray(), "Home");
        assertEquals("romeo", handler.getSessionContext().getUsername());
        assertEquals("Home", handler.getSessionContext().getResource());
    }

    public void testFailedLogin() throws Exception {
        try {
            conn.login("romeo", "password".toCharArray(), "Home");
            fail("Login should fail");
        } catch (XMPPException ex) {
        }
    }
}
