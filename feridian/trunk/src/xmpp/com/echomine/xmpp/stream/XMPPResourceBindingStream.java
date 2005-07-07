package com.echomine.xmpp.stream;

import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.IQPacket;
import com.echomine.xmpp.IQResourceBindPacket;
import com.echomine.xmpp.IXMPPStream;
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
            // send bind request
            IQResourceBindPacket request = new IQResourceBindPacket();
            request.setType(IQPacket.TYPE_SET);
            request.setResourceName(clientCtx.getResource());
            JiBXUtil.marshallIQPacket(writer, request);
            // process result
            IQResourceBindPacket result = (IQResourceBindPacket) JiBXUtil.unmarshallObject(uctx, IQPacket.class);
            if (result == null)
                throw new XMPPException("No Valid Result Packet received");
            if (result.isError())
                throw new XMPPException(result.getError());
            if (result.getJid() == null)
                throw new XMPPException("Resource Binding result does not include a JID.  Possibly bad server implementation");
            clientCtx.setResource(result.getJid().getResource());
        } catch (JiBXException ex) {
            throw new XMPPException(ex);
        }
    }
}
