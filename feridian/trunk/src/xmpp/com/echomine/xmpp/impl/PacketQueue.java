package com.echomine.xmpp.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.echomine.xmpp.IStanzaPacket;
import com.echomine.xmpp.SendPacketFailedException;
import com.echomine.xmpp.packet.IQPacket;

/**
 * An internally used queue that will perform multiple functions. First, it runs
 * in its own thread and will allow outgoing messages to be queued and sent to
 * remote connection. This allows asynchronicity in the API. Second, incoming
 * packets will be checked against any outgoing packet to see if any outgoing
 * packets match with any incoming packets. This second function allows
 * synchronicity in the API.
 */
public class PacketQueue implements Runnable {
    private static Log log = LogFactory.getLog(PacketQueue.class);
    private static final String QUEUE_RUNNING = "Feridian Packet Queue";
    private static final String QUEUE_PAUSED = "Feridian Packet Queue -- PAUSED";
    protected LinkedList queue;
    protected HashMap packetReplyTable;
    protected HashMap replyPackets;
    protected boolean shutdown = true;
    private XMPPConnectionHandler handler;
    private boolean paused;

    public PacketQueue(XMPPConnectionHandler handler) {
        this.handler = handler;
        queue = new LinkedList();
        packetReplyTable = new HashMap(25);
        replyPackets = new HashMap(25);
    }

    /**
     * Clears the entire queue and any packets waiting for reply.
     */
    public synchronized void clear() {
        queue.clear();
        packetReplyTable.clear();
        replyPackets.clear();
    }

    /**
     * Starts up the queue and the queue thread. This will also clear all
     * packets in the queue.
     */
    public synchronized void start() {
        shutdown = false;
        paused = false;
        clear();
        Thread thread = new Thread(this);
        thread.setName(QUEUE_RUNNING);
        thread.start();
    }

    /**
     * Stops the queue. This method will actually wait for all packets to be
     * sent before shutting down and giving control back to the caller.
     */
    public synchronized void stop() {
        if (shutdown)
            return;
        shutdown = true;
        synchronized (queue) {
            queue.notifyAll();
            if (!queue.isEmpty())
                try {
                    wait();
                } catch (InterruptedException ex) {
                    // intentionally left empty
                }
        }
        // iterate through all the msgs waiting for a reply and interrupt them
        synchronized (packetReplyTable) {
            Iterator iter = packetReplyTable.values().iterator();
            IStanzaPacket packet;
            while (iter.hasNext()) {
                packet = (IStanzaPacket) iter.next();
                synchronized (packet) {
                    packet.notifyAll();
                }
            }
        }
    }

    /**
     * Pauses current processing of sending packets. It will continue to accept
     * and queue packets, but will not send them out. This is normally used when
     * the entire xml processing is taken over by a stream processor.
     */
    public synchronized void pause() {
        paused = true;
    }

    /**
     * resumes operation in sending out packets.
     * 
     */
    public synchronized void resume() {
        if (!paused)
            return;
        paused = false;
        synchronized (queue) {
            queue.notify();
        }
    }

    /**
     * a method that gets called to indicate that an incoming packet was
     * received. This will check if this packet is a reply packet for a previous
     * request packet. If so, it will notify and release the wait on that
     * packet. This method will also check if the reply packet is a IQPacket
     * while the request packet is some other packet. If so, it will try to
     * match the reply IQPacket with the request packet by instantiation and
     * copying all data from the current reply packet. This will offer better
     * conformance in case the reply packet received is an empty IQ packet, in
     * which case the API will create an IQPacket. If caller is expecting a
     * reply packet of the same type as the request packet, then class cast
     * exception will occur. Anytime an error occurs during instantiation or
     * copying, the original reply packet will be returned instead as a
     * fail-safe mechanism.
     * 
     * @param replyPkt the packet received for matching with original packet
     * @return the received reply packet (new instantiated version or the
     *         original)
     */
    public IStanzaPacket packetReceived(IStanzaPacket replyPkt) {
        if (replyPkt == null)
            return null;
        if (replyPkt.getId() == null)
            return replyPkt;
        IStanzaPacket oldPacket = null;
        synchronized (packetReplyTable) {
            oldPacket = (IStanzaPacket) packetReplyTable.remove(replyPkt.getId());
        }
        IStanzaPacket newPkt = replyPkt;
        if (oldPacket != null) {
            // if reply packet is IQPacket, then we need to recast
            if (IQPacket.class.getName().equals(replyPkt.getClass().getName())) {
                try {
                    newPkt = (IStanzaPacket) oldPacket.getClass().newInstance();
                    newPkt.copyFrom(replyPkt);
                } catch (Exception ex) {
                    if (log.isWarnEnabled())
                        log.warn("Unable to instantiate new packet for casting.. returning current reply packet instead...", ex);
                }
            }
            replyPackets.put(newPkt.getId(), newPkt);
            synchronized (oldPacket) {
                oldPacket.notifyAll();
            }
        }
        return newPkt;
    }

    /**
     * Queues the packet for delivery
     * 
     * @param packet the packet to send
     * @param wait whether to wait for a reply
     * @return the reply packet if wait is true, or null if wait is false
     * @throws SendPacketFailedException when packet cannot be sent
     *             (IOException) or when waiting timed out before receiving
     *             reply
     */
    public IStanzaPacket queuePacket(IStanzaPacket packet, boolean wait) throws SendPacketFailedException {
        if (wait) {
            synchronized (packetReplyTable) {
                packetReplyTable.put(packet.getId(), packet);
            }
        }
        synchronized (queue) {
            queue.addLast(packet);
            // notify threads that's waiting for messages
            queue.notify();
        }
        if (wait) {
            synchronized (packet) {
                try {
                    packet.wait(packet.getTimeout());
                    // retrieve reply packet
                    return (IStanzaPacket) replyPackets.remove(packet.getId());
                } catch (InterruptedException ex) {
                    throw new SendPacketFailedException("Wait interrupted");
                }
            }
        }
        return null;
    }

    /*
     * This will process the data packets inside the queue. If any exist, it
     * will send them out immediately.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        IStanzaPacket packet;
        try {
            while (!shutdown || !queue.isEmpty()) {
                synchronized (queue) {
                    if (paused && !shutdown) {
                        Thread.currentThread().setName(QUEUE_PAUSED);
                        queue.wait();
                        Thread.currentThread().setName(QUEUE_RUNNING);
                    }
                    // wait until there is a new request
                    // or until we get interrupted
                    if (queue.isEmpty() && !shutdown)
                        queue.wait();
                    if (!queue.isEmpty() && !paused) {
                        packet = (IStanzaPacket) queue.removeFirst();
                        handler.sendPacket(packet);
                    }
                }
            }
        } catch (InterruptedException ex) {
            // intentionally left empty (likely a shutdown request)
        } catch (SendPacketFailedException ex) {
            // either packet cannot be marshalled or IO exception occurred.
        } finally {
            synchronized (this) {
                notifyAll();
            }
            stop();
        }
    }
}
