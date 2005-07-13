package com.echomine.xmpp.packet;

import com.echomine.xmpp.StanzaPacketBase;

/**
 * <p>
 * This is the base class in which all IQ packets should extend from. IQ packets
 * are special cases in the parsing scheme. The IQ stanza itself acts more like
 * a wrapper. The child with custom namespace is the main element that contains
 * the real data. In order to work with this, the packet processing stream will
 * parse the iq packet and also parse the real packet contained within the IQ
 * element. The mapper will search the jibx binding system to obtain a
 * marshaller/unmarshaller for the inner packets.
 * </p>
 * <p>
 * According to XMPP specifications, IQ packets MUST contain the ID and type
 * attributes. IQ packets are considered invalid without those attributes. IQ
 * packets MUST also contain one and only one child element that specifies the
 * semantics of the particular request or response. IQ packets of type "result"
 * can contain zero or one child element.
 * </p>
 */
public class IQPacket extends StanzaPacketBase {
    public static final String TYPE_GET = "get";
    public static final String TYPE_SET = "set";
    public static final String TYPE_RESULT = "result";
    public static final String TYPE_ERROR = "error";

    public IQPacket() {
        super();
    }

    /**
     * copies the values in this object to the object specified.
     * 
     * @param packet the object that will have data be copied to
     */
    public void copyTo(IQPacket packet) {
        packet.setType(getType());
        packet.setId(getId());
        packet.setTo(getTo());
        packet.setFrom(getFrom());
        if (getError() != null)
            packet.setError(getError());
    }
}
