package com.echomine.xmpp.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.echomine.xmpp.IStanzaPacket;
import com.echomine.xmpp.SendPacketFailedException;

/**
 * An internally used queue that will perform multiple functions. First, it runs
 * in its own thread and will allow outgoing messages to be queued and sent to
 * remote connection. This allows asynchronicity in the API. Second, incoming
 * packets will be checked against any outgoing packet to see if any outgoing
 * packets match with any incoming packets. This second function allows
 * synchronicity in the API.
 */
public class PacketQueue implements Runnable {
    protected LinkedList queue;
    protected HashMap packetReplyTable;
    protected HashMap replyPackets;
    protected boolean shutdown;
    private XMPPConnectionHandler handler;

    public PacketQueue(XMPPConnectionHandler handler) {
        this.handler = handler;
        queue = new LinkedList();
        packetReplyTable = new HashMap(25);
        replyPackets = new HashMap(25);
    }

    /**
     * Clears the entire queue and any packets waiting for reply.
     */
    public void clear() {
        queue.clear();
        packetReplyTable.clear();
        replyPackets.clear();
    }

    /**
     * Starts up the queue and the queue thread. This will also clear all
     * packets in the queue.
     */
    public void start() {
        shutdown = false;
        clear();
        Thread thread = new Thread(this);
        thread.setName("Feridian Packet Queue");
        thread.start();
    }

    public void stop() {
        shutdown = true;
        synchronized (queue) {
            queue.notifyAll();
        }
        // iterate through all the msgs waiting for a reply and interrupt them
        synchronized (packetReplyTable) {
            Iterator iter = packetReplyTable.values().iterator();
            IStanzaPacket packet;
            while (iter.hasNext()) {
                packet = (IStanzaPacket) iter.next();
                synchronized(packet) {
                    packet.notifyAll();
                }
            }
        }
    }

    /**
     * a method that gets called to indicate that an incoming packet was
     * received. This will check if this packet is a reply packet for a previous
     * request packet. If so, it will notify and release the wait on that
     * packet.
     * 
     */
    public void packetReceived(IStanzaPacket packet) {
        if (packet == null || packet.getId() == null)
            return;
        IStanzaPacket oldPacket = (IStanzaPacket) packetReplyTable.remove(packet.getId());
        if (oldPacket != null) {
            replyPackets.put(packet.getId(), packet);
            synchronized (oldPacket) {
                oldPacket.notifyAll();
            }
        }
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
            queue.notifyAll();
        }
        if (wait) {
            synchronized (packet) {
                try {
                    packet.wait(packet.getTimeout());
                    //retrieve reply packet
                    return (IStanzaPacket) replyPackets.remove(packet.getId());
                } catch (InterruptedException ex) {
                    throw new SendPacketFailedException("Wait timed out or interrupted");
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
            while (!shutdown) {
                synchronized (queue) {
                    // wait until there is a new request
                    // or until we get interrupted
                    if (queue.isEmpty() && !shutdown)
                        queue.wait();
                    if (!queue.isEmpty() && !shutdown) {
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
            stop();
        }
    }
}
