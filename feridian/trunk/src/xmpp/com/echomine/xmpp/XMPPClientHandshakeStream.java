package com.echomine.xmpp;

import java.io.IOException;

import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.MuseException;
import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;

/**
 * This is the initial handshaking stream to work with XMPP. Its job is to send
 * outgoing handshake and parse incoming handshake header only. Once the
 * handshake is finished, this stream handler will relinquish control to another
 * stream handler.
 */
public class XMPPClientHandshakeStream implements IXMPPStream, XMPPConstants {
    private static final String STREAM_ELEMENT_NAME = "stream";

    /**
     * Process the current stream. The parser contains the incoming stream
     * placed at the position of the next starting element. The output stream
     * allows the stream to send outgoing data.
     * 
     * @param connCtx XMPP Context
     * @param uctx the incoming data to unmarshall
     * @param writer the outgoing stream
     * @throws MuseException if there is an unexpected error during parsing of
     *             incoming stream or writing to output stream
     */
    public void process(XMPPConnectionContext connCtx, UnmarshallingContext uctx, XMPPStreamWriter writer) throws XMPPException {
        try {
            writer.startTagNamespaces(2, STREAM_ELEMENT_NAME, new int[] { 2, 3 }, new String[] { "stream", "" });
            writer.addAttribute(3, "version", "1.0");
            writer.addAttribute(3, "to", connCtx.getHost());
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
     * Ends the stream due to either receiving an error from remote entity
     * or any error encountered here. The method will not flush or
     * close the underlying stream.
     * @param writer the stream writer to write the end stream to
     */
    private void endStream(XMPPStreamWriter writer) throws IOException {
        writer.endTag(IDX_JABBER_STREAM, "stream");
    }

}
