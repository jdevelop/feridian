package com.echomine.xmpp.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

import com.echomine.net.ConnectionContext;
import com.echomine.net.HandshakeFailedException;
import com.echomine.net.MockConnectionContext;
import com.echomine.net.MockSocket;
import com.echomine.util.ClassUtil;
import com.echomine.xmpp.ErrorCode;
import com.echomine.xmpp.IPacketListener;
import com.echomine.xmpp.IStanzaPacket;
import com.echomine.xmpp.IXMPPConnection;
import com.echomine.xmpp.PacketEvent;
import com.echomine.xmpp.XMPPStanzaErrorException;
import com.echomine.xmpp.XMPPStreamContext;
import com.echomine.xmpp.XMPPTestCase;
import com.echomine.xmpp.packet.IQPacket;
import com.echomine.xmpp.packet.PresencePacket;
import com.echomine.xmpp.packet.RosterIQPacket;

/**
 * Tests the main connection handler class
 */
public class XMPPConnectionHandlerTest extends XMPPTestCase {
    XMPPConnectionHandler handler;

    XMPPStreamContext streamCtx;

    MockSocket socket;

    ConnectionContext connectionCtx;

    protected void setUp() throws Exception {
        super.setUp();
        handler = new XMPPConnectionHandler();
        handler.start();
        streamCtx = handler.getStreamContext();
        sessCtx = handler.getSessionContext();
        socket = new MockSocket(IXMPPConnection.DEFAULT_XMPP_PORT);
        streamCtx.setSocket(socket);
        streamCtx.setWriter(writer);
        streamCtx.setUnmarshallingContext(uctx);
        connectionCtx = new MockConnectionContext("example.com", "127.0.0.1",
                IXMPPConnection.DEFAULT_XMPP_PORT);
    }

    protected void tearDown() throws Exception {
        handler.shutdown();
    }

    public void testHandshakeSuccess() throws Exception {
        String reply = "<stream:stream id='c2s_123' from='example.com' version='1.0' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>"
                + "<stream:features><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/></stream:features>";
        socket.setInputStream(new ByteArrayInputStream(reply.getBytes()));
        handler.handshake(socket, connectionCtx);
        assertEquals("1.0", sessCtx.getVersion());
        assertEquals("example.com", sessCtx.getHostName());
        assertEquals("c2s_123", sessCtx.getStreamId());
        assertNotNull(streamCtx.getSocket());
        assertNotNull(streamCtx.getWriter());
        assertNotNull(streamCtx.getUnmarshallingContext());
        assertTrue(streamCtx.getFeatures().isBindingSupported());
    }

    /**
     * Tests the sending packet method is working properly
     */
    public void testSendIQPacket() throws Exception {
        String out = "<iq xmlns='jabber:client' "
                + "type='get' id='id_001'><query xmlns='jabber:iq:roster'/></iq>";
        RosterIQPacket packet = new RosterIQPacket();
        packet.setId("id_001");
        packet.setType(IQPacket.TYPE_GET);
        handler.sendPacket(packet);
        compare(new StringReader(out));
    }

    public void testSendNonIQPacket() throws Exception {
        String out = "<presence xmlns='jabber:client'/>";
        PresencePacket packet = new PresencePacket();
        handler.sendPacket(packet);
        compare(new StringReader(out));
    }

    public void testStreamErrorDuringHandling() throws Exception {
        String inRes = "com/echomine/xmpp/data/XMPPConnectionHandlerStreamError.xml";
        socket.setOutputStream(os);
        socket.setInputStream(ClassUtil.getResourceAsStream(inRes));
        try {
            handler.handshake(socket, connectionCtx);
            handler.handle(socket, connectionCtx);
            fail("This should fail by throwing an exception");
        } catch (IOException ex) {
            assertNotNull(ex.getCause());
            assertTrue(ex.getCause() instanceof XMPPStanzaErrorException);
            XMPPStanzaErrorException xex = (XMPPStanzaErrorException) ex
                    .getCause();
            assertEquals(ErrorCode.S_XML_NOT_WELL_FORMED, xex
                    .getErrorCondition());
        }
    }

    /**
     * Receiving of unknown IQ stanzas should reply with an error indicating
     * that the service is unavailable.
     */
    public void testReceiveUnknownIQStanza() throws Exception {
        String inRes = "com/echomine/xmpp/data/XMPPConnectionHandlerWithUnknownIQ.xml";
        String outRes = "com/echomine/xmpp/data/XMPPConnectionHandlerWithIQErrorReply.xml";
        socket.setOutputStream(os);
        socket.setInputStream(ClassUtil.getResourceAsStream(inRes));
        handler.handshake(socket, connectionCtx);
        handler.handle(socket, connectionCtx);
        compare(outRes);
    }

    /**
     * This tests that message packets with no children and no known extensions
     * are ignored (and consequently no packet receive event is fire)
     */
    public void testIgnoredMessagePacketNotFired() throws Exception {
        String inRes = "com/echomine/xmpp/data/XMPPConnectionHandlerIgnoredMessage.xml";
        socket.setOutputStream(os);
        socket.setInputStream(ClassUtil.getResourceAsStream(inRes));
        PacketListenerManager listenerManager = new PacketListenerManager(
                new XMPPConnectionImpl());
        handler.setPacketListenerManager(listenerManager);
        PacketReceiver rec = new PacketReceiver();
        listenerManager.addPacketListener(rec);
        handler.handshake(socket, connectionCtx);
        handler.handle(socket, connectionCtx);
        assertNull(rec.packet);
    }

    public void testListenForPacketReceived() throws Exception {
        String inRes = "com/echomine/xmpp/data/XMPPConnectionHandler_in1.xml";
        socket.setOutputStream(os);
        socket.setInputStream(ClassUtil.getResourceAsStream(inRes));
        PacketListenerManager listenerManager = new PacketListenerManager(
                new XMPPConnectionImpl());
        handler.setPacketListenerManager(listenerManager);
        PacketReceiver rec = new PacketReceiver();
        listenerManager.addPacketListener(rec);
        handler.handshake(socket, connectionCtx);
        handler.handle(socket, connectionCtx);
        assertNotNull(rec.packet);
        assertTrue(rec.packet instanceof RosterIQPacket);
    }

    public void testIncomingPacketProcessor() throws Exception {
        String inRes = "com/echomine/xmpp/data/XMPPConnectionHandler_in1.xml";
        socket.setOutputStream(os);
        socket.setInputStream(ClassUtil.getResourceAsStream(inRes));
        RosterIQPacket packet = new RosterIQPacket();
        packet.setType(IQPacket.TYPE_GET);
        packet.setId("iq_0001");
        Thread thread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                    handler.handshake(socket, connectionCtx);
                    handler.handle(socket, connectionCtx);
                } catch (HandshakeFailedException ex) {
                    fail("Handshaking should not fail");
                } catch (IOException ex) {
                    fail("IOException should not be thrown");
                } catch (InterruptedException ex) {
                    fail("Sleep should not be interrupted");
                }
            }
        };
        thread.start();
        // now let's send a packet and test for reply packet logic
        IStanzaPacket reply = handler.queuePacket(packet, true);
        // a reply should have broken us out of the wait
        assertNotNull(reply);
        assertTrue(reply instanceof RosterIQPacket);
    }

    class PacketReceiver implements IPacketListener {
        IStanzaPacket packet;

        public void packetReceived(PacketEvent event) {
            packet = event.getPacket();
        }
    }
}
