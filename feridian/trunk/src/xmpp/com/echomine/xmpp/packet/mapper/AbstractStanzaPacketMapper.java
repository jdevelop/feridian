package com.echomine.xmpp.packet.mapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.feridian.FeridianConfiguration;
import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.IPacket;
import com.echomine.xmpp.JID;
import com.echomine.xmpp.ParseException;
import com.echomine.xmpp.StanzaPacketBase;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.packet.StanzaErrorPacket;

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

    private StanzaErrorPacketMapper errorMapper = new StanzaErrorPacketMapper(XMPPConstants.NS_XMPP_CLIENT, XMPPStreamWriter.IDX_XMPP_CLIENT, "error");

    /**
     * Allows super constructor to set values and then checks if index is 0. If
     * index is 0, it will set it to the default xmpp jabber:client namespace
     * index.
     * 
     * @see com.echomine.xmpp.jibx.XMPPStreamWriter
     */
    public AbstractStanzaPacketMapper(String uri, int index, String name) {
        super(uri, index, name);
        if (this.index == 0)
            this.index = XMPPStreamWriter.IDX_XMPP_CLIENT;
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
        } catch (ParseException ex) {
            throw new JiBXException("Error Parsing JID", ex);
        }
    }

    /**
     * Marshalls the stanza error at the current context position. It will
     * immediately start writing out the error element. Thus, before calling
     * this method, the context should already close (not end) a start tag and
     * be ready to write. When this method returns, the context will be position
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

    /**
     * This will marshall all extensions contained within the packet. It will
     * try to find the marshaller for each packet and marshall the extension.
     * Failing that, it will simply skip the extension. The extensions are
     * marshalled in no particular order.
     * 
     * @param ctx the marshalling context
     * @param packet packet containing the extensions
     * @throws JiBXException
     */
    protected void marshallExtensions(MarshallingContext ctx, StanzaPacketBase packet) throws JiBXException {
        Iterator iter = packet.getExtensions().iterator();
        IPacket ext;
        StringWriter strWriter = new StringWriter(256);
        XMPPStreamWriter writer = (XMPPStreamWriter) ctx.getXmlWriter();
        try {
            while (iter.hasNext()) {
                ext = (IPacket) iter.next();
                JiBXUtil.marshallObject(strWriter, ext);
                writer.writeMarkup(strWriter.toString());
            }
        } catch (IOException ex) {
            throw new JiBXException("Error Occurred while writing markup to writer", ex);
        }
    }

    /**
     * This will unmarshall unknown extension data. It will try to find an
     * unmarshaller that recognizes the extension data. Failing that, it will
     * simply skip the extension data and move on to the next extension segment.
     * 
     * @param ctx the unmarshalling context
     * @param packet the stanza packet to add the extension to
     * @throws JiBXException
     */
    protected void unmarshallExtension(UnmarshallingContext ctx, StanzaPacketBase packet) throws JiBXException {
        // extension/unknown stanzas
        String ns = ctx.getNamespace();
        Class extClass = FeridianConfiguration.getConfig().getClassForIQUri(ns);
        IPacket extPacket;
        if (extClass != null) {
            extPacket = (IPacket) JiBXUtil.unmarshallObject(ctx, extClass);
            // add packet to main packet
            packet.addExtension(ns, extPacket);
        } else {
            // ignore unknown stanza
            ctx.skipElement();
        }
    }
}
