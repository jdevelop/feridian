package com.echomine.xmpp;

/**
 * This is the base class in which all IQ packets should extend from. IQ packets
 * are special cases in the parsing scheme. The IQ stanza itself acts more like
 * a wrapper. The child with custom namespace is the main element that contains
 * the real data. In order to work with this, the packet processing stream will
 * parse the iq packet and also parse the real packet contained within the IQ
 * element. The mapper will search the jibx binding system to obtain a
 * marshaller/unmarshaller for the inner packets.
 */
public abstract class IQPacket extends StanzaPacketBase {
    public static final String TYPE_GET = "get";
    public static final String TYPE_SET = "set";
    public static final String TYPE_RESULT = "result";
    public static final String TYPE_ERROR = "error";

    public IQPacket() {
        super();
    }
}
