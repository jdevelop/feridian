package com.echomine.xmpp.stream;

import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.IDGenerator;
import com.echomine.xmpp.IXMPPStream;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStanzaErrorException;
import com.echomine.xmpp.XMPPStreamContext;
import com.echomine.xmpp.packet.IQPacket;
import com.echomine.xmpp.packet.IQResourceBindPacket;

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
public class XMPPResourceBindingStream implements IXMPPStream {
    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPStream#process(com.echomine.xmpp.XMPPSessionContext,
     *      com.echomine.xmpp.XMPPStreamContext)
     */
    public void process(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) throws XMPPException {
        try {
            if (!streamCtx.getFeatures().isBindingSupported())
                return;
            XMPPStreamWriter writer = streamCtx.getWriter();
            UnmarshallingContext uctx = streamCtx.getUnmarshallingContext();
            // send bind request
            IQResourceBindPacket request = new IQResourceBindPacket();
            request.setId(IDGenerator.nextID());
            request.setType(IQPacket.TYPE_SET);
            request.setResourceName(sessCtx.getResource());
            JiBXUtil.marshallIQPacket(writer, request);
            // start logging
            streamCtx.getReader().startLogging();
            // process result
            IQResourceBindPacket result = (IQResourceBindPacket) JiBXUtil.unmarshallObject(uctx, IQPacket.class);
            streamCtx.getReader().stopLogging();
            if (result == null)
                throw new XMPPException("No Valid Result Packet received");
            if (result.isError())
                throw new XMPPStanzaErrorException(result.getError());
            if (result.getJid() == null)
                throw new XMPPException("Resource Binding result does not include a JID.  Possibly bad server implementation");
            sessCtx.setResource(result.getJid().getResource());
        } catch (JiBXException ex) {
            throw new XMPPException(ex);
        }
    }
}
