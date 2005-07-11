package com.echomine.xmpp.jibx;

import java.io.IOException;

import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.IXMLWriter;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.packet.StanzaErrorPacket;

/**
 * Mapper for the stanza error packet.
 */
public class StanzaErrorPacketMapper extends StreamErrorPacketMapper implements XMPPConstants {
    protected static final String TYPE_ATTRIBUTE_NAME = "type";

    /**
     * @param uri the uri of the element working with
     * @param index the index for the namespace
     * @param name the element name
     */
    public StanzaErrorPacketMapper(String uri, int index, String name) {
        super(uri, index, name);
        if (index == 0)
            this.index = IDX_XMPP_CLIENT;
    }

    /**
     * marshalls the data into an xml string
     */
    public void marshal(Object obj, IMarshallingContext ictx) throws JiBXException {
        // make sure the parameters are as expected
        if (!(obj instanceof StanzaErrorPacket)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else if (!(ictx instanceof MarshallingContext)) {
            throw new JiBXException("Invalid object type for marshaller");
        } else {
            // start by generating start tag for container
            MarshallingContext ctx = (MarshallingContext) ictx;
            StanzaErrorPacket packet = (StanzaErrorPacket) obj;
            IXMLWriter writer = ctx.getXmlWriter();
            // validate
            if (packet.getErrorType() == null)
                throw new JiBXException("XMPP requires the error type to be set for stanza errors");
            // add extension namespaces
            int stanzasIdx = writer.getNamespaces().length;
            String[] extns;
            if (packet.getApplicationCondition() == null)
                extns = new String[] { NS_STANZA_ERROR };
            else
                extns = new String[] { NS_STANZA_ERROR, packet.getApplicationCondition().getNamespaceURI() };
            writer.pushExtensionNamespaces(extns);
            ctx.startTagNamespaces(index, name, new int[] { index }, new String[] { "" });
            ctx.attribute(index, "type", packet.getErrorType());
            ctx.closeStartContent();
            marshallErrorCondition(ctx, stanzasIdx, stanzasIdx + 1, packet);
            // close error tag
            ctx.endTag(index, name);
            writer.popExtensionNamespaces();
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
        StanzaErrorPacket packet = (StanzaErrorPacket) obj;
        if (packet == null)
            packet = new StanzaErrorPacket();
        // parse required type argument
        packet.setErrorType(ctx.attributeText(null, TYPE_ATTRIBUTE_NAME));
        // parse past the error element
        ctx.parsePastStartTag(uri, name);
        unmarshallErrorCondition(ctx, NS_STANZA_ERROR, packet);
        ctx.toEnd();
        return packet;
    }
}
