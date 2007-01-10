package com.echomine.xmpp.stream;

import org.jibx.runtime.IXMLReader;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.feridian.FeridianConfiguration;
import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.IDGenerator;
import com.echomine.xmpp.IXMPPStream;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStanzaErrorException;
import com.echomine.xmpp.XMPPStreamContext;
import com.echomine.xmpp.packet.IQPacket;
import com.echomine.xmpp.packet.SessionIQPacket;
import com.echomine.xmpp.packet.StanzaErrorPacket;

/**
 * This stream will issue a session start request. Under XMPP specs, client must
 * establish session after resource binding and SASL authentication. If the
 * stream feature indicates that session establishment is required, then this
 * stream will do it. If this stream process data before SASL OR resource
 * binding, nothing will happen. Server will not reply with any data and this
 * may cause the API to wait forever for incoming data.
 */
public class XMPPSessionStream implements IXMPPStream, XMPPConstants {
    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPStream#process(com.echomine.xmpp.XMPPSessionContext,
     *      com.echomine.xmpp.XMPPStreamContext)
     */
    public void process(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx)
            throws XMPPException {
        try {
            if (!streamCtx.getFeatures().isSessionSupported()) return;
            XMPPStreamWriter writer = streamCtx.getWriter();
            UnmarshallingContext uctx = streamCtx.getUnmarshallingContext();
            // start logging
            streamCtx.getReader().startLogging();
            // send request
            SessionIQPacket request = new SessionIQPacket();
            request.setId(IDGenerator.nextID());
            request.setType(IQPacket.TYPE_SET);
            JiBXUtil.marshallIQPacket(writer, request);
            // synchronized for first access is required to prevent thread
            // racing issue
            synchronized (uctx) {
                if (!uctx.isAt(XMPPConstants.NS_XMPP_CLIENT, "iq"))
                    uctx.next();
            }
            IQPacket result = (IQPacket) JiBXUtil.unmarshallObject(uctx, IQPacket.class);
            if (result == null)
                throw new XMPPException("No Valid Result Packet received");
            if (result.isError())
                throw new XMPPStanzaErrorException(result.getError());
        } catch (JiBXException ex) {
            throw new XMPPException(ex);
        } finally {
            streamCtx.getReader().stopLogging();
        }
    }
}
