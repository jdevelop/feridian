package com.echomine.xmpp.jibx;

import java.io.IOException;

import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.IXMLWriter;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.JiBXUtil;
import com.echomine.xmpp.IQPacket;
import com.echomine.xmpp.MessagePacket;

/**
 * Mapper for all IQ packets. This mapper will (un)marshall the common IQ packet
 * attributes as well as error stanzas attached to the packet. For the real IQ
 * packet data, it will utilize a secondary (un)marshaller to work with the
 * inner data. As with all IQ packets, the main IQ stanza itself is really a
 * wrapper for the inner real packet. All IQ packets must extend from IQPacket.
 */
public class IQPacketMapper extends StanzaPacketMapper {
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
            MessagePacket packet = (MessagePacket) obj;
            IXMLWriter writer = ctx.getXmlWriter();
            ctx.startTagNamespaces(index, name, new int[] { index }, new String[] { "" });
            //marshall attributes
            marshallStanzaAttributes(packet, ctx);
            ctx.closeStartContent();
            //marshall the real message encapsulated in the message
            JiBXUtil.marshallObject(ctx, obj, 1);
            if (packet.getError() != null)
                marshallStanzaError(packet.getError(), ctx);
            ctx.endTag(index, name);
            try {
                writer.flush();
            } catch (IOException ex) {
                throw new JiBXException("Error flushing stream", ex);
            }
        }
    }

    /**
     * Unmarshalls the error packet. The reason for this is that the error
     * packet uses different condition elements and may also condition
     * application-specific conditions. Due to the highly non-conforming nature
     * of the error message, a custom mapper is required.
     */
    public Object unmarshal(Object obj, IUnmarshallingContext ictx) throws JiBXException {
        //make sure we're at the right start tag
        UnmarshallingContext ctx = (UnmarshallingContext) ictx;
        if (!ctx.isAt(uri, name)) {
            ctx.throwStartTagNameError(uri, name);
        }
        MessagePacket packet = (MessagePacket) obj;
        if (packet == null)
            packet = new MessagePacket();
        //unmarshall base packet attributes
        unmarshallStanzaAttributes(packet, ctx);
        ctx.parsePastStartTag(uri, name);
        do {
            if (ctx.isAt(uri, ERROR_ELEMENT_NAME)) {
                packet.setError(unmarshallStanzaError(ctx));
            } else {
                break;
            }
        } while (true);
        //parse to end
        ctx.toEnd();
        return packet;
    }
}
