package com.echomine.xmpp.packet;


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

    /**
     * Constructs a default iq packet with a default type of "get".
     */
    public IQPacket() {
        super(TYPE_GET);
    }
    
    /**
     * Constructs an iq packet with the specified type.
     * 
     * @param type the type as indicated by the static data
     */
    public IQPacket(String type) {
        super(type);
    }
}
