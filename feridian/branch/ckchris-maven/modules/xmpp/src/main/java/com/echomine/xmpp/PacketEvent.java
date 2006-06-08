package com.echomine.xmpp;

import java.util.EventObject;

/**
 * The packet event that encapsulates the event data when firing off a incoming
 * packet event.
 */
public class PacketEvent extends EventObject {
    private static final long serialVersionUID = -4777187590657295054L;
    IStanzaPacket packet;

    /**
     * constructor to create a packet event
     * 
     * @param connection
     * @param packet
     */
    public PacketEvent(IXMPPConnection connection, IStanzaPacket packet) {
        super(connection);
        this.packet = packet;
    }

    /**
     * Retrieves the packet that was received.
     * 
     * @return the packet
     */
    public IStanzaPacket getPacket() {
        return packet;
    }

    /**
     * Retrieves the connection that obtained received this packet.
     * 
     * @return the connection
     */
    public IXMPPConnection getConnection() {
        return (IXMPPConnection) getSource();
    }
}
