package com.echomine.xmpp.stream;

import java.io.IOException;

import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.ErrorPacket;
import com.echomine.xmpp.IXMPPStream;
import com.echomine.xmpp.StreamFeaturesPacket;
import com.echomine.xmpp.XMPPClientContext;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;

/**
 * This is the initial handshaking stream to work with XMPP. Its job is to send
 * outgoing handshake and parse incoming handshake header only. Once the
 * handshake is finished, this stream handler will relinquish control to another
 * stream handler.
 */
public class XMPPClientHandshakeStream implements IXMPPStream, XMPPConstants {
    private static final String STREAM_ELEMENT_NAME = "stream";

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPStream#process(com.echomine.xmpp.XMPPClientContext,
     *      com.echomine.xmpp.stream.XMPPConnectionContext,
     *      org.jibx.runtime.impl.UnmarshallingContext,
     *      com.echomine.jibx.XMPPStreamWriter)
     */
    public void process(XMPPClientContext clientCtx, XMPPConnectionContext connCtx, UnmarshallingContext uctx, XMPPStreamWriter writer) throws XMPPException {
        try {
            writer.startTagNamespaces(IDX_JABBER_STREAM, STREAM_ELEMENT_NAME, new int[] { 2, 3 }, new String[] { "stream", "" });
            writer.addAttribute(IDX_XMPP_CLIENT, "version", "1.0");
            writer.addAttribute(IDX_XMPP_CLIENT, "to", clientCtx.getHost());
            writer.closeStartTag();
            writer.flush();
            //now read in the xml stream
            if (!uctx.isAt(NS_JABBER_STREAM, STREAM_ELEMENT_NAME))
                uctx.throwStartTagNameError(NS_JABBER_STREAM, STREAM_ELEMENT_NAME);
            //parse out the incoming info
            connCtx.setHost(uctx.attributeText(null, "from"));
            connCtx.setSessionId(uctx.attributeText(null, "id"));
            //parse past start tag
            uctx.parsePastStartTag(NS_JABBER_STREAM, STREAM_ELEMENT_NAME);
            int eventType;
            while (true) {
                eventType = uctx.next();
                if (eventType == UnmarshallingContext.END_DOCUMENT) {
                    //stream finished
                    endStream(writer);
                    break;
                }
                if (eventType == UnmarshallingContext.END_TAG && "stream".equals(uctx.getName()) && NS_JABBER_STREAM.equals(uctx.getNamespace())) {
                    //check if end tag is for stream
                    endStream(writer);
                    break;
                }
                //read in any possible error element
                if (uctx.isAt(NS_JABBER_STREAM, "error")) {
                    ErrorPacket packet = (ErrorPacket) JiBXUtil.unmarshallObject(uctx, ErrorPacket.class);
                    endStream(writer);
                    throw new XMPPException("Error message received for handshake", packet);
                }
                if (uctx.isAt(NS_JABBER_STREAM, "features")) {
                    StreamFeaturesPacket packet = (StreamFeaturesPacket) JiBXUtil.unmarshallObject(uctx, StreamFeaturesPacket.class);
                    connCtx.setTLSFeature(packet.getTLSFeature());
                }
            }
        } catch (IOException ex) {
            throw new XMPPException(ex);
        } catch (JiBXException ex) {
            throw new XMPPException(ex);
        }
    }

    /**
     * Ends the stream due to either receiving an error from remote entity or
     * any error encountered here. The method will not flush or close the
     * underlying stream.
     * 
     * @param writer the stream writer to write the end stream to
     */
    private void endStream(XMPPStreamWriter writer) throws IOException {
        writer.endTag(IDX_JABBER_STREAM, "stream");
    }
}
