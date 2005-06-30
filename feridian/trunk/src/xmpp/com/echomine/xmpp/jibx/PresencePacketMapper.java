package com.echomine.xmpp.jibx;

import java.io.IOException;

import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.IXMLWriter;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.xmpp.PresencePacket;

/**
 * This is the mapper for the presence packet.
 * <p>
 * FIXME: Support xml:lang and arbitrary extension children.
 * </p>
 */
public class PresencePacketMapper extends StanzaPacketMapper {
    protected static final String SHOW_ELEMENT_NAME = "show";
    protected static final String STATUS_ELEMENT_NAME = "status";
    protected static final String PRIORITY_ELEMENT_NAME = "priority";

    /**
     * @param uri the uri associated with this element
     * @param index the index of this uri
     * @param name the name of the element
     */
    public PresencePacketMapper(String uri, int index, String name) {
        super(uri, index, name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IMarshaller#marshal(java.lang.Object,
     *      org.jibx.runtime.IMarshallingContext)
     */
    public void marshal(Object obj, IMarshallingContext ictx) throws JiBXException {
        // make sure the parameters are as expected
        if (!(obj instanceof PresencePacket)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else if (!(ictx instanceof MarshallingContext)) {
            throw new JiBXException("Invalid object type for marshalling context");
        } else {
            // start by generating start tag for container
            MarshallingContext ctx = (MarshallingContext) ictx;
            PresencePacket packet = (PresencePacket) obj;
            IXMLWriter writer = ctx.getXmlWriter();
            ctx.startTagNamespaces(index, name, new int[] { index }, new String[] { "" });
            //marshall attributes
            marshallStanzaAttributes(packet, ctx);
            //if packet has no show, status, etc, then close tag
            if (packet.getStatus() == null && packet.getShow() == null && packet.getPriority() == 0 && packet.getError() == null) {
                ctx.closeStartEmpty();
            } else {
                ctx.closeStartContent();
                //marshall out the message
                if (packet.getShow() != null)
                    ctx.element(index, SHOW_ELEMENT_NAME, packet.getShow());
                if (packet.getStatus() != null)
                    ctx.element(index, STATUS_ELEMENT_NAME, packet.getStatus());
                if (packet.getPriority() != 0)
                    ctx.element(index, PRIORITY_ELEMENT_NAME, packet.getPriority());
                if (packet.getError() != null)
                    marshallStanzaError(packet.getError(), ctx);
                ctx.endTag(index, name);
            }
            try {
                writer.flush();
            } catch (IOException ex) {
                throw new JiBXException("Error flushing stream", ex);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jibx.runtime.IUnmarshaller#unmarshal(java.lang.Object,
     *      org.jibx.runtime.IUnmarshallingContext)
     */
    public Object unmarshal(Object obj, IUnmarshallingContext ictx) throws JiBXException {
        //make sure we're at the right start tag
        UnmarshallingContext ctx = (UnmarshallingContext) ictx;
        if (!ctx.isAt(uri, name)) {
            ctx.throwStartTagNameError(uri, name);
        }
        PresencePacket packet = (PresencePacket) obj;
        if (packet == null)
            packet = new PresencePacket();
        //unmarshall base packet attributes
        unmarshallStanzaAttributes(packet, ctx);
        int eventType = ctx.next();
        if (eventType == UnmarshallingContext.END_TAG)
            return packet;
        do {
            if (ctx.isAt(uri, SHOW_ELEMENT_NAME)) {
                packet.setShow(ctx.parseElementText(uri, SHOW_ELEMENT_NAME));
            } else if (ctx.isAt(uri, STATUS_ELEMENT_NAME)) {
                packet.setStatus(ctx.parseElementText(uri, STATUS_ELEMENT_NAME));
            } else if (ctx.isAt(uri, PRIORITY_ELEMENT_NAME)) {
                packet.setPriority(ctx.parseElementInt(uri, PRIORITY_ELEMENT_NAME));
            } else if (ctx.isAt(uri, ERROR_ELEMENT_NAME)) {
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
