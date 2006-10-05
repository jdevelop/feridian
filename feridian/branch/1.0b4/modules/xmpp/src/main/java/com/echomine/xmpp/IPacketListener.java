package com.echomine.xmpp;

import java.util.EventListener;

/**
 * This is the main interface that all classes who are interested in receiving
 * incoming packets must implement.
 */
public interface IPacketListener extends EventListener {
    void packetReceived(PacketEvent event);
}
