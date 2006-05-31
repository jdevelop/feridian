package com.echomine.xmpp.packet.mapper;

import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.xmpp.JID;
import com.echomine.xmpp.JIDFormatException;
import com.echomine.xmpp.packet.StanzaErrorPacket;
import com.echomine.xmpp.packet.StanzaPacketBase;

/**
 * Mapper for the basic stanza packet. This mapper is the base class for all the
 * stanza packet mappers. Specifically, those are the message, presence, and iq
 * packets top level packets.
 */
public abstract class AbstractStanzaPacketMapper extends AbstractPacketMapper {
    protected static final String TYPE_ATTRIBUTE_NAME = "type";
    protected static final String ID_ATTRIBUTE_NAME = "id";
    protected static final String FROM_ATTRIBUTE_NAME = "from";
    protected static final String TO_ATTRIBUTE_NAME = "to";
    protected static final String ERROR_ELEMENT_NAME = "error";

    private StanzaErrorPacketMapper errorMapper;

    /**
     * @see com.echomine.xmpp.jibx.XMPPStreamWriter
     */
    public AbstractStanzaPacketMapper(String uri, int index, String name) {
        super(uri, index, name);
        errorMapper = new StanzaErrorPacketMapper(uri, index, "error");
    }

    /**
     * This will marshall the stanza attributes to the current element
     * positioned in the marshalling context. The attributes are the to, from,
     * id, type. This method does NOT close the start tag.
     * 
     * @param ctx the marshalling context
     * @throws JiBXException
     */
    public void marshallStanzaAttributes(StanzaPacketBase packet, MarshallingContext ctx) throws JiBXException {
        // write out the attributes
        if (packet.getTo() != null)
            ctx.attribute(0, TO_ATTRIBUTE_NAME, packet.getTo().toString());
        if (packet.getFrom() != null)
            ctx.attribute(0, FROM_ATTRIBUTE_NAME, packet.getFrom().toString());
        if (packet.getId() != null)
            ctx.attribute(0, ID_ATTRIBUTE_NAME, packet.getId());
        if (packet.getType() != null)
            ctx.attribute(0, TYPE_ATTRIBUTE_NAME, packet.getType());
    }

    /**
     * unmarshalls the attributes from the context. the context must be
     * positioned at the element name that contains the attributes.
     * 
     * @param ctx the unmarshalling context
     * @throws JiBXException
     */
    public void unmarshallStanzaAttributes(StanzaPacketBase packet, UnmarshallingContext ctx) throws JiBXException {
        try {
            if (ctx.hasAttribute(null, TYPE_ATTRIBUTE_NAME))
                packet.setType(ctx.attributeText(null, TYPE_ATTRIBUTE_NAME));
            if (ctx.hasAttribute(null, TO_ATTRIBUTE_NAME))
                packet.setTo(JID.parseJID(ctx.attributeText(null, TO_ATTRIBUTE_NAME)));
            if (ctx.hasAttribute(null, FROM_ATTRIBUTE_NAME))
                packet.setFrom(JID.parseJID(ctx.attributeText(null, FROM_ATTRIBUTE_NAME)));
            if (ctx.hasAttribute(null, ID_ATTRIBUTE_NAME))
                packet.setId(ctx.attributeText(null, ID_ATTRIBUTE_NAME));
        } catch (JIDFormatException ex) {
            throw new JiBXException("Error Parsing JID", ex);
        }
    }

    /**
     * Marshalls the stanza error at the current context position. It will
     * immediately start writing out the error element. Thus, before calling
     * this method, the context should already close (not end) a start tag and
     * be ready to write. When this method returns, the context will be positioned
     * at the end of the ending error element.
     * 
     * @param packet the error packet to marshall
     * @param ctx the context to use
     * @throws JiBXException
     */
    protected void marshallStanzaError(StanzaErrorPacket packet, MarshallingContext ctx) throws JiBXException {
        errorMapper.marshal(packet, ctx);
    }

    /**
     * Unmarshalls the stanza error packet. The context should be positioned
     * directly at the error element.
     * 
     * @param ctx the context
     * @return the stanza error packet
     * @throws JiBXException
     */
    protected StanzaErrorPacket unmarshallStanzaError(UnmarshallingContext ctx) throws JiBXException {
        return (StanzaErrorPacket) errorMapper.unmarshal(null, ctx);
    }
}
