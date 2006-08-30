package com.echomine.xmpp.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

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

    protected enum RunningState {
        RUNNING, PAUSED, STOPPING, STOPPED
    }

    protected LinkedBlockingQueue<IStanzaPacket> queue;
    protected HashMap<String, IStanzaPacket> packetReplyTable;
    protected HashMap<String, IStanzaPacket> replyPackets;
    protected RunningState state = RunningState.STOPPED;
    private XMPPConnectionHandler handler;
    private ReentrantLock lock;
    private Semaphore pauseLock;
    private Thread queueThread;

    public PacketQueue(XMPPConnectionHandler handler) {
        this.handler = handler;
        queue = new LinkedBlockingQueue<IStanzaPacket>();
        lock = new ReentrantLock();
        pauseLock = new Semaphore(1);
        packetReplyTable = new HashMap<String, IStanzaPacket>(25);
        replyPackets = new HashMap<String, IStanzaPacket>(25);
    }

    /**
     * Clears the entire queue and any packets waiting for reply.
     */
    public void clear() {
        lock.lock();
        try {
            queue.clear();
            packetReplyTable.clear();
            replyPackets.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Starts up the queue and the queue thread in a running (unpaused) state,
     * ready to process any incoming/outgoing packets.
     */
    public void start() {
        start(false);
    }

    /**
     * Starts up the queue and the queue thread. This will also clear all
     * packets in the queue.
     * 
     * @param paused true to start the queue paused, false otherwise
     */
    public void start(boolean paused) {
        lock.lock();
        try {
            if (paused)
                state = RunningState.PAUSED;
            else
                state = RunningState.RUNNING;
            clear();
            queueThread = new Thread(this);
            queueThread.setName(QUEUE_RUNNING);
            queueThread.start();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Stops the queue. This method will actually send all currently queued
     * outgoing packets before shutting down and giving control back to the
     * caller.
     */
    public void stop() {
        lock.lock();
        try {
            if (state == RunningState.STOPPED || state == RunningState.STOPPING)
                return;
            state = RunningState.STOPPING;
            queueThread.interrupt();
            if (!queue.isEmpty()) {
                // finish sending off all the remaining packets
                Iterator<IStanzaPacket> iter = queue.iterator();
                IStanzaPacket packet;
                while (iter.hasNext()) {
                    packet = iter.next();
                    handler.sendPacket(packet);
                }
            }
            // iterate through all the msgs waiting for a reply and interrupt
            // them
            synchronized (packetReplyTable) {
                Iterator<IStanzaPacket> iter = packetReplyTable.values().iterator();
                IStanzaPacket packet;
                while (iter.hasNext()) {
                    packet = iter.next();
                    synchronized (packet) {
                        packet.notifyAll();
                    }
                }
            }
        } catch (SendPacketFailedException ex) {
            // intentionally left empty (connection likely closed)
        } finally {
            state = RunningState.STOPPED;
            lock.unlock();
        }
    }

    /**
     * Pauses current processing of sending packets. It will continue to accept
     * and queue packets, but will not send them out. This is normally used when
     * the entire xml processing is taken over by a stream processor. State must
     * be running in order to pause.
     */
    public void pause() {
        lock.lock();
        try {
            if (state != RunningState.RUNNING)
                return;
            state = RunningState.PAUSED;
            pauseLock.tryAcquire();
        } finally {
            lock.unlock();
        }
    }

    /**
     * resumes operation in sending out packets. State must be paused in order
     * to resume.
     */
    public void resume() {
        lock.lock();
        try {
            if (state != RunningState.PAUSED)
                return;
            state = RunningState.RUNNING;
            if (pauseLock.availablePermits() == 0)
                pauseLock.release();
        } finally {
            lock.unlock();
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
            oldPacket = packetReplyTable.remove(replyPkt.getId());
        }
        IStanzaPacket newPkt = replyPkt;
        if (oldPacket != null) {
            // if reply packet is IQPacket, then we need to recast
            if (IQPacket.class.getName().equals(replyPkt.getClass().getName())) {
                try {
                    newPkt = oldPacket.getClass().newInstance();
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
        if (state == RunningState.STOPPED)
            throw new SendPacketFailedException("The Queue is STOPPED, unable to queue packet for sending.");
        if (wait) {
            synchronized (packetReplyTable) {
                packetReplyTable.put(packet.getId(), packet);
            }
        }
        try {
            queue.put(packet);
            if (wait) {
                synchronized (packet) {
                    packet.wait(packet.getTimeout());
                    // retrieve reply packet
                    return replyPackets.remove(packet.getId());
                }
            }
        } catch (InterruptedException ex1) {
            throw new SendPacketFailedException("Wait interrupted");
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
            while (state != RunningState.STOPPED && state != RunningState.STOPPING) {
                while (state == RunningState.PAUSED) {
                    try {
                        Thread.currentThread().setName(QUEUE_PAUSED);
                        pauseLock.acquire();
                        Thread.currentThread().setName(QUEUE_RUNNING);
                    } catch (InterruptedException ex) {
                    } finally {
                        pauseLock.release();
                    }
                }
                if (state == RunningState.RUNNING) {
                    packet = queue.take();
                    handler.sendPacket(packet);
                }
            }
        } catch (InterruptedException ex) {
            if (log.isInfoEnabled())
                log.info("Thread is interrupted.  Likely a shutdown request was received.");
        } catch (SendPacketFailedException ex) {
            // either packet cannot be marshalled or IO exception occurred.
            if (log.isInfoEnabled())
                log.info("Packet cannot be sent.  Likely an IO Exception occurred.", ex);
        } finally {
            stop();
        }
    }
}
