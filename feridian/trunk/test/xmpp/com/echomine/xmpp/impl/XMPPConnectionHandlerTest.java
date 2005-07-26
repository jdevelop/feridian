package com.echomine.xmpp.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

import com.echomine.net.ConnectionContext;
import com.echomine.net.HandshakeFailedException;
import com.echomine.net.MockSocket;
import com.echomine.util.ClassUtil;
import com.echomine.xmpp.IPacketListener;
import com.echomine.xmpp.IStanzaPacket;
import com.echomine.xmpp.IXMPPConnection;
import com.echomine.xmpp.PacketEvent;
import com.echomine.xmpp.XMPPStreamContext;
import com.echomine.xmpp.XMPPTestCase;
import com.echomine.xmpp.packet.IQPacket;
import com.echomine.xmpp.packet.IQRosterPacket;
import com.echomine.xmpp.packet.PresencePacket;

/**
 * Tests the main connection handler class
 */
public class XMPPConnectionHandlerTest extends XMPPTestCase {
    XMPPConnectionHandler handler;
    XMPPStreamContext streamCtx;
    MockSocket socket;
    ConnectionContext connectionCtx;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        streamCtx = new XMPPStreamContext();
        socket = new MockSocket("example.com", IXMPPConnection.DEFAULT_XMPP_PORT);
        streamCtx.setSocket(socket);
        streamCtx.setWriter(writer);
        streamCtx.setUnmarshallingContext(uctx);
        connectionCtx = new ConnectionContext("example.com", IXMPPConnection.DEFAULT_XMPP_PORT);
        handler = new XMPPConnectionHandler(sessCtx, streamCtx);
    }

    public void testHandshakeSuccess() throws Exception {
        String reply = "<stream:stream id='c2s_123' from='example.com' version='1.0' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>"
                + "<stream:features><bind xmlns='urn:ietf:params:xml:ns:xmpp-bind'/></stream:features>";
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

    /**
     * Tests the sending packet method is working properly
     */
    public void testSendIQPacket() throws Exception {
        String out = "<iq xmlns='jabber:client' " + "type='get' id='id_001'><query xmlns='jabber:iq:roster'/></iq>";
        IQRosterPacket packet = new IQRosterPacket();
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

    public void testIncomingPacketProcessor() throws Exception {
        String inRes = "com/echomine/xmpp/data/XMPPConnectionHandler_in1.xml";
        socket.setOutputStream(os);
        socket.setInputStream(ClassUtil.getResourceAsStream(inRes));
        IQRosterPacket packet = new IQRosterPacket();
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
        assertTrue(reply instanceof IQRosterPacket);
    }

    public void testListenForPacketReceived() throws Exception {
        String inRes = "com/echomine/xmpp/data/XMPPConnectionHandler_in1.xml";
        socket.setOutputStream(os);
        socket.setInputStream(ClassUtil.getResourceAsStream(inRes));
        PacketListenerManager listenerManager = new PacketListenerManager(new XMPPConnectionImpl());
        handler.setPacketListenerManager(listenerManager);
        PacketReceiver rec = new PacketReceiver();
        listenerManager.addPacketListener(rec);
        handler.handshake(socket, connectionCtx);
        handler.handle(socket, connectionCtx);
        assertNotNull(rec.packet);
        assertTrue(rec.packet instanceof IQRosterPacket);
    }

    class PacketReceiver implements IPacketListener {
        IStanzaPacket packet;

        public void packetReceived(PacketEvent event) {
            packet = event.getPacket();
        }
    }
}
