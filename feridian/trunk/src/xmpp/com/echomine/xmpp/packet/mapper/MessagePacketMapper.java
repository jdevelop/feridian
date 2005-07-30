package com.echomine.xmpp.packet.mapper;

import java.io.IOException;
import java.util.Locale;

import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.IXMLWriter;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.util.LocaleUtil;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.packet.MessagePacket;

/**
 * Mapper for the message stanza. This mapper supports xml:lang and arbitrary
 * extension children.
 */
public class MessagePacketMapper extends AbstractIMPacketMapper implements XMPPConstants {
    protected static final String TYPE_ATTRIBUTE_NAME = "type";
    protected static final String SUBJECT_ELEMENT_NAME = "subject";
    protected static final String BODY_ELEMENT_NAME = "body";
    protected static final String THREAD_ELEMENT_NAME = "thread";

    /**
     * @param uri the uri of the element working with
     * @param index the index for the namespace
     * @param name the element name
     */
    public MessagePacketMapper(String uri, int index, String name) {
        super(uri, index, name);
    }

    /**
     * marshalls the data into an xml string
     */
    public void marshal(Object obj, IMarshallingContext ictx) throws JiBXException {
        // make sure the parameters are as expected
        if (!(obj instanceof MessagePacket)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else if (!(ictx instanceof MarshallingContext)) {
            throw new JiBXException("Invalid object type for marshalling context");
        } else {
            // start by generating start tag for container
            MarshallingContext ctx = (MarshallingContext) ictx;
            MessagePacket packet = (MessagePacket) obj;
            IXMLWriter writer = ctx.getXmlWriter();
            ctx.startTagNamespaces(index, name, new int[] { index }, new String[] { "" });
            // marshall attributes
            marshallStanzaAttributes(packet, ctx);
            ctx.closeStartContent();
            // marshall out the message
            marshallMapWithLocale(index, SUBJECT_ELEMENT_NAME, packet.getSubjects(), ctx);
            marshallMapWithLocale(index, BODY_ELEMENT_NAME, packet.getBodies(), ctx);
            if (packet.getThreadID() != null)
                ctx.element(index, THREAD_ELEMENT_NAME, packet.getThreadID());
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

    /**
     * Unmarshalls the error packet. The reason for this is that the error
     * packet uses different condition elements and may also condition
     * application-specific conditions. Due to the highly non-conforming nature
     * of the error message, a custom mapper is required.
     */
    public Object unmarshal(Object obj, IUnmarshallingContext ictx) throws JiBXException {
        // make sure we're at the right start tag
        UnmarshallingContext ctx = (UnmarshallingContext) ictx;
        if (!ctx.isAt(uri, name)) {
            ctx.throwStartTagNameError(uri, name);
        }
        MessagePacket packet = (MessagePacket) obj;
        if (packet == null)
            packet = new MessagePacket();
        // unmarshall base packet attributes
        unmarshallStanzaAttributes(packet, ctx);
        ctx.next();
        String value;
        Locale locale;
        while (ctx.currentEvent() != UnmarshallingContext.END_DOCUMENT && ctx.currentEvent() != UnmarshallingContext.END_TAG && !name.equals(ctx.getName())) {
            if (ctx.isAt(uri, SUBJECT_ELEMENT_NAME)) {
                locale = null;
                if (ctx.hasAttribute(NS_XML, LANG_ATTRIBUTE_NAME))
                    locale = LocaleUtil.parseLocale(ctx.attributeText(NS_XML, LANG_ATTRIBUTE_NAME));
                value = ctx.parseElementText(uri, SUBJECT_ELEMENT_NAME);
                packet.setSubject(value, locale);
            } else if (ctx.isAt(uri, BODY_ELEMENT_NAME)) {
                locale = null;
                if (ctx.hasAttribute(NS_XML, LANG_ATTRIBUTE_NAME))
                    locale = LocaleUtil.parseLocale(ctx.attributeText(NS_XML, LANG_ATTRIBUTE_NAME));
                value = ctx.parseElementText(uri, BODY_ELEMENT_NAME);
                packet.setBody(value, locale);
            } else if (ctx.isAt(uri, THREAD_ELEMENT_NAME)) {
                packet.setThreadID(ctx.parseElementText(uri, THREAD_ELEMENT_NAME));
            } else if (ctx.isAt(uri, ERROR_ELEMENT_NAME)) {
                packet.setError(unmarshallStanzaError(ctx));
            } else {
                unmarshallExtension(ctx, packet);
            }
        }
        return packet;
    }
}
