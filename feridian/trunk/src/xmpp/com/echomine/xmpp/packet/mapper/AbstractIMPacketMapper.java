package com.echomine.xmpp.packet.mapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.feridian.FeridianConfiguration;
import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.util.LocaleUtil;
import com.echomine.xmpp.IPacket;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.packet.IMPacket;
import com.echomine.xmpp.packet.StanzaPacketBase;

/**
 * The IM packet mapper provides methods to work with the functionality added by
 * the IMPacket. It provides convenience methods to marshall/unmarshall the
 * locale and extension packets.
 */
public abstract class AbstractIMPacketMapper extends AbstractStanzaPacketMapper {
    protected static final String LANG_ATTRIBUTE_NAME = "lang";

    /**
     * @param uri the namespace
     * @param index the index of the current element's namespace
     * @param name the name of the element
     */
    public AbstractIMPacketMapper(String uri, int index, String name) {
        super(uri, index, name);

    }

    /*
     * The method overrides its parent method to work with the stanza-level
     * xml:lang attribute.
     * 
     * @see com.echomine.xmpp.packet.mapper.AbstractStanzaPacketMapper#marshallStanzaAttributes(com.echomine.xmpp.packet.StanzaPacketBase,
     *      org.jibx.runtime.impl.MarshallingContext)
     */
    public void marshallStanzaAttributes(StanzaPacketBase packet, MarshallingContext ctx) throws JiBXException {
        super.marshallStanzaAttributes(packet, ctx);
        if (!(packet instanceof IMPacket))
            return;
        IMPacket impkt = (IMPacket) packet;
        if (impkt.getLocale() != null)
            ctx.attribute(XMPPConstants.IDX_XML, LANG_ATTRIBUTE_NAME, LocaleUtil.format(impkt.getLocale()));
    }

    /*
     * The method overrides its parent method to work with the stanza-level
     * xml:lang attribute.
     * 
     * @see com.echomine.xmpp.packet.mapper.AbstractStanzaPacketMapper#unmarshallStanzaAttributes(com.echomine.xmpp.packet.StanzaPacketBase,
     *      org.jibx.runtime.impl.UnmarshallingContext)
     */
    public void unmarshallStanzaAttributes(StanzaPacketBase packet, UnmarshallingContext ctx) throws JiBXException {
        super.unmarshallStanzaAttributes(packet, ctx);
        if (!(packet instanceof IMPacket))
            return;
        IMPacket impkt = (IMPacket) packet;
        if (ctx.hasAttribute(XMPPConstants.NS_XML, LANG_ATTRIBUTE_NAME))
            impkt.setLocale(LocaleUtil.parseLocale(ctx.attributeText(XMPPConstants.NS_XML, LANG_ATTRIBUTE_NAME)));
    }

    /**
     * marshalls hash map values (locale/string key/value pairs).
     * 
     * @param index the index of the namespace
     * @param elementName the elementName
     * @param map the map of the key/value pairs
     * @param ctx the context
     * @throws JiBXException
     */
    protected void marshallMapWithLocale(int index, String elementName, Map map, MarshallingContext ctx) throws JiBXException {
        if (map.isEmpty())
            return;
        Iterator iter = map.keySet().iterator();
        Locale locale;
        while (iter.hasNext()) {
            locale = (Locale) iter.next();
            marshallElementWithLocale(index, elementName, (String) map.get(locale), ctx, locale);
        }
    }

    /**
     * marshalls out an element that may contain an xml:lang locale attribute.
     * 
     * @param index the index of the namespace
     * @param elementName the element name
     * @param value the value of the element
     * @param ctx the context
     * @param locale optional locale, null if none
     * @throws JiBXException
     */
    protected void marshallElementWithLocale(int index, String elementName, String value, MarshallingContext ctx, Locale locale) throws JiBXException {
        ctx.startTagAttributes(index, elementName);
        if (locale != null)
            ctx.attribute(XMPPConstants.IDX_XML, "lang", LocaleUtil.format(locale));
        ctx.closeStartContent().content(value);
        ctx.endTag(index, elementName);
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
    protected void marshallExtensions(MarshallingContext ctx, IMPacket packet) throws JiBXException {
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
    protected void unmarshallExtension(UnmarshallingContext ctx, IMPacket packet) throws JiBXException {
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
