package com.echomine.xmpp.packet.mapper;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.IXMLReader;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.feridian.FeridianConfiguration;
import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.packet.IQPacket;
import com.echomine.xmpp.packet.StanzaErrorPacket;

/**
 * Mapper for all IQ packets. This mapper will (un)marshall the common IQ packet
 * attributes as well as error stanzas attached to the packet. For the real IQ
 * packet data, it will utilize a secondary (un)marshaller to work with the
 * inner data. As with all IQ packets, the main IQ stanza itself is really a
 * wrapper for the inner real packet. All IQ packets must extend from IQPacket.
 */
public class IQPacketMapper extends AbstractStanzaPacketMapper {
    private final static Log log = LogFactory.getLog(IQPacketMapper.class);

    /**
     * @param uri the uri of the element working with
     * @param index the index for the namespace
     * @param name the element name
     */
    public IQPacketMapper(String uri, int index, String name) {
        super(uri, index, name);
    }

    /**
     * marshalls the data into an xml string
     */
    public void marshal(Object obj, IMarshallingContext ictx) throws JiBXException {
        // make sure the parameters are as expected
        if (!(obj instanceof IQPacket)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else if (!(ictx instanceof MarshallingContext)) {
            throw new JiBXException("Invalid object type for marshalling context");
        } else {
            // start by generating start tag for container
            MarshallingContext ctx = (MarshallingContext) ictx;
            IQPacket packet = (IQPacket) obj;
            try {
                XMPPStreamWriter writer = (XMPPStreamWriter) ctx.getXmlWriter();
                writer.startStanzaTagOpen(name);
                // marshall attributes
                marshallStanzaAttributes(packet, ctx);
                ctx.closeStartContent();
                // if obj is more than a simple IQPacket, then marshall real
                // data
                if (packet.getClass() != IQPacket.class) {
                    // marshall the packet's real contents
                    StringWriter strWriter = new StringWriter(256);
                    JiBXUtil.marshallObject(strWriter, packet);
                    //write out empty text to work around jibx issue
                    ctx.writeContent("");
                    writer.writeMarkup(strWriter.toString());
                }
                if (packet.getError() != null)
                    marshallStanzaError(packet.getError(), ctx);
                writer.endStanzaTag(name);
                writer.flush();
            } catch (IOException ex) {
                throw new JiBXException("Error writing to stream", ex);
            }
        }
    }

    /**
     * Unmarshalls the iq packet. It will unmarshall the iq header attributes,
     * and then subsequently call the binding directory to separately unmarshall
     * the inner stanza.
     */
    public Object unmarshal(Object obj, IUnmarshallingContext ictx) throws JiBXException {
        // make sure we're at the right start tag
        UnmarshallingContext ctx = (UnmarshallingContext) ictx;
        if (!ctx.isAt(uri, name))
            ctx.throwStartTagNameError(uri, name);
        if (obj == null)
            obj = new IQPacket();
        IQPacket tpkt = (IQPacket) obj;
        IQPacket packet = null;
        unmarshallStanzaAttributes(tpkt, ctx);
        do {
            ctx.next();
        } while (ctx.currentEvent() == IXMLReader.TEXT);
        while (ctx.currentEvent() != IXMLReader.END_DOCUMENT
                && ctx.currentEvent() != IXMLReader.END_TAG
                && !name.equals(ctx.getName())) {
            if (ctx.isAt(uri, "error")) {
                tpkt.setError((StanzaErrorPacket) JiBXUtil.unmarshallObject(ctx, StanzaErrorPacket.class));
            } else {
                Class iqClass = FeridianConfiguration.getConfig().getClassForUri(ctx.getNamespace());
                if (packet != null) {
                    if (log.isWarnEnabled())
                        log.warn("Invalid IQ Packet.  Already unmarshalled one child element, but found more than one.  This does not conform to XMPP specs.  Ignoring this child element");
                    ctx.skipElement();
                } else if (iqClass != null) {
                    packet = (IQPacket) JiBXUtil.unmarshallObject(ctx, iqClass);
                } else {
                    // ignore unknown stanza
                    ctx.skipElement();
                }
                while (ctx.currentEvent() == IXMLReader.TEXT)
                    ctx.next();
            }
        }
        if (packet != null) {
            packet.copyFrom(tpkt);
            return packet;
        }
        return tpkt;
    }
}
