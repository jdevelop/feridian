package com.echomine.xmpp.stream;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.muse.MuseConfiguration;
import com.echomine.xmpp.IQPacket;
import com.echomine.xmpp.IQResourceBindPacket;
import com.echomine.xmpp.IXMPPStream;
import com.echomine.xmpp.JID;
import com.echomine.xmpp.ParseException;
import com.echomine.xmpp.StanzaErrorPacket;
import com.echomine.xmpp.XMPPClientContext;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;

/**
 * This stream checks to see if resource binding is required. Under XMPP specs,
 * resource binding occurs AFTER SASL authentication. If the stream feature
 * indicates that resource binding is required, then this stream will do it.
 * Normally, it is preferable that the client sets the resource and the server
 * accepts it, although the server can autogenerate a resource for the client.
 * If resource conflict occurs, either the server will reject the current
 * client's request with a forbidden error or the server will disconnect the
 * previous active resource and allow the current client to bind to the resource
 * (recommended for servers).
 */
public class XMPPResourceBindingStream implements IXMPPStream, XMPPConstants {
    private static final Log log = LogFactory.getLog(XMPPResourceBindingStream.class);
    protected static final String IQ_ELEMENT_NAME = "iq";
    protected static final String TYPE_ATTRIBUTE_NAME = "type";
    protected static final String ID_ATTRIBUTE_NAME = "id";
    protected static final String FROM_ATTRIBUTE_NAME = "from";
    protected static final String TO_ATTRIBUTE_NAME = "to";
    protected static final String ERROR_ELEMENT_NAME = "error";

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPStream#process(com.echomine.xmpp.XMPPClientContext,
     *      com.echomine.xmpp.stream.XMPPConnectionContext,
     *      org.jibx.runtime.impl.UnmarshallingContext,
     *      com.echomine.jibx.XMPPStreamWriter)
     */
    public void process(XMPPClientContext clientCtx, XMPPConnectionContext connCtx, UnmarshallingContext uctx, XMPPStreamWriter writer) throws XMPPException {
        if (!connCtx.isResourceBindingRequired())
            return;
        try {
            //send bind request
            IQResourceBindPacket request = new IQResourceBindPacket();
            request.setType(IQPacket.TYPE_SET);
            request.setResourceName(clientCtx.getResource());
            marshallIQPacket(request, writer);
            //process result
            IQResourceBindPacket result = (IQResourceBindPacket) unmarshallIQPacket(uctx);
            if (result.isError())
                throw new XMPPException(result.getError());
            if (result.getJid() == null) 
                throw new XMPPException("Resource Binding result does not include a JID.  Possibly bad server implementation");
            clientCtx.setResource(result.getJid().getResource());
        } catch (IOException ex) {
            throw new XMPPException(ex);
        } catch (JiBXException ex) {
            throw new XMPPException(ex);
        }
    }

    /**
     * Unmarshalls the IQ Packet. It will return a packet that is a subclass of
     * IQPacket.
     * 
     * @param ctx the unmarshalling context
     * @return the IQ packet
     * @throws JiBXException
     */
    private IQPacket unmarshallIQPacket(UnmarshallingContext ctx) throws JiBXException {
        if (!ctx.isAt(NS_XMPP_CLIENT, IQ_ELEMENT_NAME))
            ctx.throwStartTagNameError(NS_XMPP_CLIENT, IQ_ELEMENT_NAME);
        String type = null, stanzaId = null;
        JID to = null, from = null;
        StanzaErrorPacket error = null;
        IQPacket packet = null;
        try {
            //unmarshall attributes
            if (ctx.hasAttribute(null, TYPE_ATTRIBUTE_NAME))
                type = ctx.attributeText(null, TYPE_ATTRIBUTE_NAME);
            if (ctx.hasAttribute(null, TO_ATTRIBUTE_NAME))
                to = JID.parseJID(ctx.attributeText(null, TO_ATTRIBUTE_NAME));
            if (ctx.hasAttribute(null, FROM_ATTRIBUTE_NAME))
                from = JID.parseJID(ctx.attributeText(null, FROM_ATTRIBUTE_NAME));
            if (ctx.hasAttribute(null, ID_ATTRIBUTE_NAME))
                stanzaId = ctx.attributeText(null, ID_ATTRIBUTE_NAME);
            //unmarshall real packet's contents
            ctx.parsePastStartTag(NS_XMPP_CLIENT, IQ_ELEMENT_NAME);
            do {
                if (ctx.isAt(NS_XMPP_CLIENT, ERROR_ELEMENT_NAME)) {
                    error = (StanzaErrorPacket) JiBXUtil.unmarshallObject(ctx, StanzaErrorPacket.class);
                } else if (ctx.isEnd()) {
                    break;
                } else {
                    Class iqClass = MuseConfiguration.getConfig().getClassForURI(ctx.getNamespace());
                    //if no deserializer found, ignore the entire element
                    //as specified by XMPP specs
                    if (iqClass != null) {
                        packet = (IQPacket) JiBXUtil.unmarshallObject(ctx, iqClass);
                    } else {
                        String unknownText = ctx.parseElementText(ctx.getNamespace(), ctx.getElementName());
                        if (log.isWarnEnabled())
                            log.warn("Found and Ignoring Unknown Element Data: " + unknownText);
                    }
                }
            } while (true);
            ctx.toEnd();
            packet.setType(type);
            packet.setId(stanzaId);
            packet.setTo(to);
            packet.setFrom(from);
            if (error != null)
                packet.setError(error);
            return packet;
        } catch (ParseException ex) {
            throw new JiBXException("Error Parsing JID", ex);
        }
    }

    /**
     * Marshalls the IQ packet to the output stream.
     * 
     * @param packet the IQ Packet to marshall
     * @param writer the writer to output marshalled packet to
     * @throws JiBXException
     */
    private void marshallIQPacket(IQPacket packet, XMPPStreamWriter writer) throws IOException, JiBXException {
        writer.startTagOpen(IDX_XMPP_CLIENT, IQ_ELEMENT_NAME);
        //write out the IQ attributes
        if (packet.getTo() != null)
            writer.addAttribute(0, TO_ATTRIBUTE_NAME, packet.getTo().toString());
        if (packet.getFrom() != null)
            writer.addAttribute(0, FROM_ATTRIBUTE_NAME, packet.getFrom().toString());
        if (packet.getId() != null)
            writer.addAttribute(0, ID_ATTRIBUTE_NAME, packet.getId());
        if (packet.getType() != null)
            writer.addAttribute(0, TYPE_ATTRIBUTE_NAME, packet.getType());
        writer.closeStartTag();
        //marshall the packet's real contents
        StringWriter strWriter = new StringWriter(256);
        JiBXUtil.marshallObject(strWriter, packet);
        writer.writeMarkup(strWriter.toString());
        writer.endTag(IDX_XMPP_CLIENT, IQ_ELEMENT_NAME);
        writer.flush();
    }
}
