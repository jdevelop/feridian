package com.echomine.xmpp.impl;

import java.util.HashMap;
import java.util.LinkedList;

import junit.framework.TestCase;

import com.echomine.xmpp.IStanzaPacket;
import com.echomine.xmpp.MockXMPPConnectionHandler;
import com.echomine.xmpp.SendPacketFailedException;
import com.echomine.xmpp.packet.PresencePacket;

/**
 * Tests the packet queue and make sure it works properly
 */
public class PacketQueueTest extends TestCase {
    MockXMPPConnectionHandler handler;
    TestablePacketQueue queue;

    protected void setUp() throws Exception {
        handler = new MockXMPPConnectionHandler();
        queue = new TestablePacketQueue(handler);
    }

    protected void tearDown() throws Exception {
        queue.stop();
    }

    public void testQueueShutdownClearData() throws Exception {
        queue.queuePacket(new PresencePacket(), false);
        assertEquals(1, queue.getQueue().size());
        assertEquals(0, queue.getReplyTable().size());
    }

    public void testQueuePacketWithWait() throws Exception {
        queue.start();
        QueuePacketRunnable runner = new QueuePacketRunnable();
        Thread thread = new Thread(runner);
        thread.start();
        // call reply received
        PresencePacket packet = new PresencePacket();
        packet.setId("id_001");
        packet.setType(PresencePacket.TYPE_SUBSCRIBED);
        queue.packetReceived(packet);
        assertNotNull(runner.replyPacket);
        assertEquals(PresencePacket.TYPE_SUBSCRIBED, runner.replyPacket.getType());
        assertEquals(0, queue.getQueue().size());
        assertEquals(0, queue.getReplyTable().size());
    }

    /**
     * Checks that the start state has all variables set properly
     * 
     * @throws Exception
     */
    public void testStartStopState() throws Exception {
        queue.start();
        assertFalse(queue.isShutdown());
        queue.stop();
        assertTrue(queue.isShutdown());
    }

    class QueuePacketRunnable implements Runnable {
        IStanzaPacket replyPacket;

        public void run() {
            PresencePacket packet = new PresencePacket();
            packet.setId("id_001");
            packet.setType(PresencePacket.TYPE_SUBSCRIBE);

            // queue the packet, and wait for reply
            try {
                replyPacket = queue.queuePacket(packet, true);
            } catch (SendPacketFailedException ex) {
            }
        }
    }

    class TestablePacketQueue extends PacketQueue {
        public TestablePacketQueue(XMPPConnectionHandler handler) {
            super(handler);
        }

        public boolean isShutdown() {
            return shutdown;
        }

        public LinkedList getQueue() {
            return queue;
        }

        public HashMap getReplyTable() {
            return packetReplyTable;
        }
    }
}
