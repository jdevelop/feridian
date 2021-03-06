package com.echomine.xmpp.stream;

import java.io.IOException;

import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.util.LocaleUtil;
import com.echomine.xmpp.IXMPPStream;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStanzaErrorException;
import com.echomine.xmpp.XMPPStreamContext;
import com.echomine.xmpp.packet.ErrorPacket;
import com.echomine.xmpp.packet.StreamFeatures;

/**
 * This is the initial handshaking stream to work with XMPP. Its job is to send
 * outgoing handshake and parse incoming handshake header only. Once the
 * handshake is finished, this stream handler will relinquish control to another
 * stream handler. This handler does NOT close the stream in any way. It only
 * works with sending the initiating stream tag as well as receiving up to the
 * stream features.
 */
public class XMPPClientHandshakeStream implements IXMPPStream {
    private static final String STREAM_ELEMENT_NAME = "stream";

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPStream#process(com.echomine.xmpp.XMPPSessionContext,
     *      com.echomine.xmpp.XMPPStreamContext)
     */
    public void process(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) throws XMPPException {
        try {
            XMPPStreamWriter writer = streamCtx.getWriter();
            UnmarshallingContext uctx = streamCtx.getUnmarshallingContext();
            writer.startHandshakeStream(XMPPConstants.NS_XMPP_CLIENT, sessCtx.getHostName(), sessCtx.getLocale());
            // start logging
            streamCtx.getReader().startLogging();
            // no need to sync on first read from unmarshalling context
            // because client handshake is either called before
            // async packet processing starts OR it is called right
            // after a separate stream processor, in which case the
            // async packet processing activity is already paused.
            // now read in the xml stream
            if (!uctx.isAt(XMPPConstants.NS_JABBER_STREAM, STREAM_ELEMENT_NAME))
                uctx.throwStartTagNameError(XMPPConstants.NS_JABBER_STREAM, STREAM_ELEMENT_NAME);
            // parse out the incoming info
            if (uctx.hasAttribute(null, "from"))
                sessCtx.setHostName(uctx.attributeText(null, "from"));
            if (uctx.hasAttribute(null, "id"))
                sessCtx.setStreamId(uctx.attributeText(null, "id"));
            if (uctx.hasAttribute(null, "version"))
                sessCtx.setVersion(uctx.attributeText(null, "version"));
            if (uctx.hasAttribute(XMPPConstants.NS_XML, "lang"))
                sessCtx.setLocale(LocaleUtil.parseLocale(uctx.attributeText(XMPPConstants.NS_XML, "lang")));
            // parse past start tag
            uctx.next();
            // read in any possible error element
            if (uctx.isAt(XMPPConstants.NS_JABBER_STREAM, "error")) {
                ErrorPacket packet = (ErrorPacket) JiBXUtil.unmarshallObject(uctx, ErrorPacket.class);
                throw new XMPPStanzaErrorException("Handshake Error", packet);
            }
            if (uctx.isAt(XMPPConstants.NS_JABBER_STREAM, "features")) {
                StreamFeatures features = (StreamFeatures) JiBXUtil.unmarshallObject(uctx, StreamFeatures.class);
                streamCtx.setFeatures(features);
            }
            streamCtx.getReader().stopLogging();
        } catch (IOException ex) {
            throw new XMPPException(ex);
        } catch (JiBXException ex) {
            throw new XMPPException(ex);
        }
    }
}
