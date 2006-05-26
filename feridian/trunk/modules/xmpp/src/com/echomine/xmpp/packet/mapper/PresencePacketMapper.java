package com.echomine.xmpp.packet.mapper;

import java.io.IOException;
import java.util.Locale;

import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.IXMLReader;
import org.jibx.runtime.IXMLWriter;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.util.LocaleUtil;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.packet.PresencePacket;

/**
 * This is the mapper for the presence packet. This mapper support
 * (un)marshalling of extensions and xml:lang attributes. The only elements that
 * can contain xml:lang attributes is the status.
 */
public class PresencePacketMapper extends AbstractIMPacketMapper implements
        XMPPConstants {
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
        System.out.println("uri: " + uri + ", index=" + index + "name: " + name);
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
            // marshall attributes
            marshallStanzaAttributes(packet, ctx);
            // if packet has no show, status, etc, then close tag
            ctx.closeStartContent();
            if (packet.getShow() != null)
                ctx.element(index, SHOW_ELEMENT_NAME, packet.getShow());
            marshallMapWithLocale(index, STATUS_ELEMENT_NAME, packet.getStatuses(), ctx);
            if (packet.getPriority() != 0)
                ctx.element(index, PRIORITY_ELEMENT_NAME, packet.getPriority());
            if (packet.getError() != null)
                marshallStanzaError(packet.getError(), ctx);
            // marshall extensions
            marshallExtensions(ctx, packet);
            ctx.endTag(index, name);
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
        // make sure we're at the right start tag
        UnmarshallingContext ctx = (UnmarshallingContext) ictx;
        if (!ctx.isAt(uri, name)) {
            ctx.throwStartTagNameError(uri, name);
        }
        PresencePacket packet = (PresencePacket) obj;
        if (packet == null)
            packet = new PresencePacket();
        // unmarshall base packet attributes
        unmarshallStanzaAttributes(packet, ctx);
        do {
            ctx.next();
        } while (ctx.currentEvent() == IXMLReader.TEXT);
        String value;
        Locale locale;
        while (ctx.currentEvent() != IXMLReader.END_DOCUMENT
                && ctx.currentEvent() != IXMLReader.END_TAG
                && !name.equals(ctx.getName())) {
            if (ctx.isAt(uri, SHOW_ELEMENT_NAME)) {
                packet.setShow(ctx.parseElementText(uri, SHOW_ELEMENT_NAME));
            } else if (ctx.isAt(uri, STATUS_ELEMENT_NAME)) {
                locale = null;
                if (ctx.hasAttribute(NS_XML, LANG_ATTRIBUTE_NAME))
                    locale = LocaleUtil.parseLocale(ctx.attributeText(NS_XML, LANG_ATTRIBUTE_NAME));
                value = ctx.parseElementText(uri, STATUS_ELEMENT_NAME);
                packet.setStatus(value, locale);
            } else if (ctx.isAt(uri, PRIORITY_ELEMENT_NAME)) {
                packet.setPriority(ctx.parseElementInt(uri, PRIORITY_ELEMENT_NAME));
            } else if (ctx.isAt(uri, ERROR_ELEMENT_NAME)) {
                packet.setError(unmarshallStanzaError(ctx));
            } else {
                unmarshallExtension(ctx, packet);
            }
            while (ctx.currentEvent() == IXMLReader.TEXT)
                ctx.next();
        }
        return packet;
    }
}
