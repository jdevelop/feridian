package com.echomine.xmpp.impl;

import javax.swing.event.EventListenerList;

import com.echomine.xmpp.IPacketListener;
import com.echomine.xmpp.IStanzaPacket;
import com.echomine.xmpp.IXMPPConnection;
import com.echomine.xmpp.PacketEvent;

/**
 * A supporting class to manage packet listeners, and firing events to those
 * listeners.
 */
public class PacketListenerManager {
    private IXMPPConnection connection;
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Instantiate with the source connection. This manager will subsequently
     * create all packet events using this connection as the source.
     */
    public PacketListenerManager(IXMPPConnection source) {
        this.connection = source;
    }

    /** adds a subscriber to listen for connection events */
    public void addPacketListener(IPacketListener l) {
        listenerList.add(IPacketListener.class, l);
    }

    /** remove from listening to connection events */
    public void removePacketListener(IPacketListener l) {
        listenerList.remove(IPacketListener.class, l);
    }

    /**
     * This will fire off a packet event that contains the incoming packet
     * 
     * @param packet
     */
    protected void firePacketReceived(IStanzaPacket packet) {        
        PacketEvent event = new PacketEvent(connection, packet);
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            ((IPacketListener) listeners[i + 1]).packetReceived(event);
        }
    }
}
