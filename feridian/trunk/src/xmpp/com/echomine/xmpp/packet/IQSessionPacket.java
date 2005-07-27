package com.echomine.xmpp.packet;

/**
 * Represents the IQ session packet that establishes session. Due to the jibx binding
 * mechanism, this class is created purely for binding purposes.  It contain no need
 * data as the session element is an empty element.
 */
public class IQSessionPacket extends IQPacket {
    public IQSessionPacket() {
        super();
    }
}
