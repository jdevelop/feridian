package com.echomine.xmpp.impl;

import com.echomine.xmpp.IPacketRouter;

/**
 * <p>
 * This is the main default implementation for routing all packets within the
 * API. This router supports multiple features.
 * </p>
 * <ul>
 * <li>Runs the queue in a seprate thread for utmost packet processing
 * efficiency. It will free up the read and write connection threads as well</li>
 * <li>Support routing for multiple connections. Only one router is needed for
 * all connection packet routing. Reduces memory overhead.</li>
 * </ul>
 */
public class PacketRouterImpl implements IPacketRouter {

    public PacketRouterImpl() {
        super();
    }
}
