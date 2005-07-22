package com.echomine.xmpp.stream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.IXMPPStream;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStreamContext;
import com.echomine.xmpp.packet.StreamFeatures;
import com.echomine.xmpp.stream.sasl.XMPPSaslClient;

/**
 * This is the main stream that process SASL authentication. This particular
 * stream implementation uses a custom implementation of the SASL, and thus will
 * only support PLAIN and DIGEST-MD5 mechanisms. JDK 1.5 actually has full SASL
 * support. When this API is upgraded to 1.5 minimum requirement, then SASL will
 * be used.
 */
public class SASLHandshakeStream implements IXMPPStream, XMPPConstants {
    private static Log log = LogFactory.getLog(SASLHandshakeStream.class);
    protected final static int SOCKETBUF = 8192;
    private static final String PLAIN = "PLAIN";
    private static final String DIGEST_MD5 = "DIGEST-MD5";
    private static final String CHALLENGE_ELEMENT_NAME = "challenge";
    private static final String RESPONSE_ELEMENT_NAME = "response";

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPStream#process(com.echomine.xmpp.XMPPSessionContext,
     *      com.echomine.xmpp.XMPPStreamContext)
     */
    public void process(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) throws XMPPException {
        if (!streamCtx.getFeatures().isSaslSupported())
            return;
        String mechanism = findPreferredMechanism(streamCtx.getFeatures());
        if (mechanism == null)
            throw new XMPPException("Unable to find a supported mechanism.  This stream only supports DIGEST-MD5 and PLAIN");
        XMPPStreamWriter writer = streamCtx.getWriter();
        UnmarshallingContext uctx = streamCtx.getUnmarshallingContext();
        String[] extns = new String[] { NS_STREAM_SASL };
        writer.pushExtensionNamespaces(extns);
        int idx = writer.getNamespaces().length;
        try {
            // send <auth>
            if (log.isInfoEnabled())
                log.info("Request authentication with " + mechanism);
            writer.startTagNamespaces(idx, "auth", new int[] { idx }, new String[] { "" });
            writer.addAttribute(0, "mechanism", mechanism);
            writer.closeEmptyTag();
            writer.flush();
            // server sends us challenge
            uctx.next();
            parseAndThrowFailure(uctx);
            if (!uctx.isAt(NS_STREAM_SASL, CHALLENGE_ELEMENT_NAME))
                throw new XMPPException("Expecting <challenge> tag, but found: " + uctx.getName());
            String challengeStr = uctx.parseElementText(NS_STREAM_SASL, CHALLENGE_ELEMENT_NAME);
            if (log.isInfoEnabled())
                log.info("Received challenge string: " + challengeStr);
            XMPPSaslClient client = new XMPPSaslClient();
            client.unwrapChallenge(challengeStr);
            String response = client.getAuthResponse(sessCtx, streamCtx);
            // send response
            if (log.isInfoEnabled())
                log.info("Sending response: " + response);
            writer.startTagNamespaces(idx, RESPONSE_ELEMENT_NAME, new int[] { idx }, new String[] { "" });
            writer.closeStartTag();
            writer.writeTextContent(response);
            writer.endTag(idx, RESPONSE_ELEMENT_NAME);
            writer.flush();
            // check remote response
            uctx.next();
            parseAndThrowFailure(uctx);
            if (!uctx.isAt(NS_STREAM_SASL, CHALLENGE_ELEMENT_NAME))
                throw new XMPPException("Expecting <challenge> tag, but found: " + uctx.getName());
            // the received string is rsauth, which can be ignored
            uctx.parseElementText(NS_STREAM_SASL, CHALLENGE_ELEMENT_NAME);
            // send final response
            if (log.isInfoEnabled())
                log.info("Authentication accepted, sending final acknowledgement");
            writer.startTagNamespaces(idx, RESPONSE_ELEMENT_NAME, new int[] { idx }, new String[] { "" });
            writer.closeEmptyTag();
            writer.flush();
            // receive final success or failure
            uctx.next();
            parseAndThrowFailure(uctx);
            if (!uctx.isAt(NS_STREAM_SASL, "success"))
                throw new XMPPException("Expecting <success> tag, but found: " + uctx.getName());
            uctx.parseElementText(NS_STREAM_SASL, "success");
            if (log.isInfoEnabled())
                log.info("SASL authentication complete, resetting input and output streams for new handshake");
            InputStreamReader bis = new InputStreamReader(streamCtx.getSocket().getInputStream(), "UTF-8");
            BufferedOutputStream bos = new BufferedOutputStream(streamCtx.getSocket().getOutputStream(), SOCKETBUF);
            // reset writer and unmarshalling context for handshake
            // renegotiation preparation
            writer = new XMPPStreamWriter();
            writer.setOutput(bos);
            uctx.setDocument(bis);
            streamCtx.setWriter(writer);
        } catch (IOException ex) {
            throw new XMPPException(ex);
        } catch (JiBXException ex) {
            throw new XMPPException(ex);
        }
    }

    /**
     * will parse and throw exception if failure is caught
     * 
     * @param uctx
     * @throws JiBXException
     * @throws XMPPException
     */
    private void parseAndThrowFailure(UnmarshallingContext uctx) throws JiBXException, XMPPException {
        if (uctx.isAt(NS_STREAM_SASL, "failure")) {
            uctx.parsePastStartTag(NS_STREAM_SASL, "failure");
            uctx.toTag();
            String errorType = uctx.getName();
            uctx.parsePastElement(NS_STREAM_SASL, errorType);
            uctx.toEnd();
            if (log.isWarnEnabled())
                log.warn("SASL auth negotiation failed with error " + errorType);
            throw new XMPPException("SASL Failed with error: " + errorType);
        }
    }

    /**
     * This will find and return the preferred mechanism to use. The preference
     * order is -- DIGEST-MD5, PLAIN.
     * 
     * @param features the features packet
     * @return the preferred mechanism, or null if one is not found.
     */
    protected String findPreferredMechanism(StreamFeatures features) {
        if (features.isSaslMechanismSupported(DIGEST_MD5))
            return DIGEST_MD5;
        if (features.isSaslMechanismSupported(PLAIN))
            return PLAIN;
        return null;
    }
}
