package com.echomine.xmpp.auth;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPLoggableReader;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.util.Base64;
import com.echomine.xmpp.IXMPPStream;
import com.echomine.xmpp.JID;
import com.echomine.xmpp.XMPPAuthCallback;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStreamContext;
import com.echomine.xmpp.packet.StreamFeatures;
import com.echomine.xmpp.auth.sasl.DigestMD5SaslClient;

/**
 * This is the main stream that process SASL authentication. This particular
 * stream implementation uses a custom implementation of the SASL, and thus will
 * only support PLAIN and DIGEST-MD5 mechanisms. JDK 1.5 actually has full SASL
 * support. When this API is upgraded to 1.5 minimum requirement, then SASL will
 * be used.
 */
public class SASLAuthenticator implements IXMPPStream, XMPPConstants {
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
            throw new XMPPException("SASL is not supported and this stream should not have been called");
        String mechanism = findPreferredMechanism(streamCtx.getFeatures());
        if (mechanism == null)
            throw new XMPPException("Unable to find a supported mechanism.  This stream only supports DIGEST-MD5 and PLAIN");
        if (log.isInfoEnabled())
            log.info("Request authentication with " + mechanism);
        XMPPStreamWriter writer = streamCtx.getWriter();
        UnmarshallingContext uctx = streamCtx.getUnmarshallingContext();
        String[] extns = new String[] { NS_STREAM_SASL };
        writer.pushExtensionNamespaces(extns);
        try {
            streamCtx.getReader().startLogging();
            if (DIGEST_MD5.equals(mechanism))
                authDigestMD5(uctx, writer, sessCtx, streamCtx);
            else if (PLAIN.equals(mechanism))
                authPlain(uctx, writer, sessCtx, streamCtx);
            // save username and resource and reset auth callback
            String hostname = sessCtx.getHostName();
            sessCtx.reset();
            sessCtx.setHostName(hostname);
            sessCtx.setUsername(streamCtx.getAuthCallback().getUsername());
            sessCtx.setResource(streamCtx.getAuthCallback().getResource());
            streamCtx.getAuthCallback().clear();
            streamCtx.clearFeatures();
            if (log.isInfoEnabled())
                log.info("SASL authentication complete, resetting input and output streams for new handshake");
            // reset writer and unmarshalling context for handshake
            // renegotiation preparation
            streamCtx.getReader().stopLogging();
            XMPPLoggableReader bis = new XMPPLoggableReader(streamCtx.getSocket().getInputStream(), "UTF-8");
            BufferedOutputStream bos = new BufferedOutputStream(streamCtx.getSocket().getOutputStream(), SOCKETBUF);
            writer.flush();
            writer = new XMPPStreamWriter();
            writer.setOutput(bos);
            uctx.setDocument(bis);
            streamCtx.setWriter(writer);
            streamCtx.setReader(bis);
        } catch (IOException ex) {
            throw new XMPPException(ex);
        } catch (JiBXException ex) {
            throw new XMPPException(ex);
        }
    }

    /**
     * Authenticates using the SASL PLAIN mechanism
     * 
     * @throws IOException
     * @throws JiBXException
     * @throws XMPPException
     */
    private void authPlain(UnmarshallingContext uctx, XMPPStreamWriter writer, XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) throws IOException, JiBXException, XMPPException {
        int idx = writer.getNamespaces().length;
        // send <auth>
        writer.startTagNamespaces(idx, "auth", new int[] { idx }, new String[] { "" });
        writer.addAttribute(0, "mechanism", PLAIN);
        writer.closeStartTag();
        XMPPAuthCallback callback = streamCtx.getAuthCallback();
        JID authid = new JID(callback.getUsername(), sessCtx.getHostName(), callback.getResource());
        StringBuffer buf = new StringBuffer(128);
        buf.append(authid.toString()).append('\0').append(callback.getUsername()).append('\0').append(callback.getPassword());
        writer.writeTextContent(Base64.encodeString(buf.toString()));
        writer.endTag(idx, "auth");
        // send response immediately
        writer.flush();
        // receive final success or failure
        parseAndThrowFailure(uctx, streamCtx);
        if (!uctx.isAt(NS_STREAM_SASL, "success"))
            throw new XMPPException("Expecting <success> tag, but found: " + uctx.getName());
        parseElementText(uctx, "success", streamCtx);
        streamCtx.getReader().flushLog();
    }

    /**
     * authenticates the user using SASL Digest-MD5 mechanism
     * 
     * @throws XMPPException
     * @throws IOException
     * @throws JiBXException
     */
    private void authDigestMD5(UnmarshallingContext uctx, XMPPStreamWriter writer, XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) throws XMPPException, IOException, JiBXException {
        int idx = writer.getNamespaces().length;
        // send <auth>
        writer.startTagNamespaces(idx, "auth", new int[] { idx }, new String[] { "" });
        writer.addAttribute(0, "mechanism", DIGEST_MD5);
        writer.closeEmptyTag();
        writer.flush();
        // synchronize the first access in case connection handler read extra data
        synchronized (uctx) {
            if (!uctx.isAt(NS_STREAM_SASL, CHALLENGE_ELEMENT_NAME))
                uctx.next();
        }
        parseAndThrowFailure(uctx, streamCtx);
        String challengeStr = parseElementText(uctx, CHALLENGE_ELEMENT_NAME, streamCtx);
        if (log.isInfoEnabled())
            log.info("Received challenge string: " + challengeStr);
        DigestMD5SaslClient client = new DigestMD5SaslClient();
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
        uctx.next();
        parseAndThrowFailure(uctx, streamCtx);
        // the received string is rspauth, which can be ignored
        parseElementText(uctx, CHALLENGE_ELEMENT_NAME, streamCtx);
        // send final response
        if (log.isInfoEnabled())
            log.info("Authentication accepted, sending final acknowledgement");
        writer.startTagNamespaces(idx, RESPONSE_ELEMENT_NAME, new int[] { idx }, new String[] { "" });
        writer.closeEmptyTag();
        writer.flush();
        // receive final success or failure
        uctx.next();
        parseAndThrowFailure(uctx, streamCtx);
        parseElementText(uctx, "success", streamCtx);
    }

    /**
     * Convenience method to parse past an element and obtaining the text
     * contained within. It expects to be at the start tag, and will parse the
     * context, then end tag. This method is here to bypass a bug in jibx where
     * jibx's parseElementText() will hang while reading an xml stream. This is
     * because the jibx method attempts to read past the end tag, but in a
     * streaming xml, there is no more data, and thus the parser will hang,
     * waiting for more incoming data.
     * 
     * @param uctx the unmarshalling context
     * @param elementName the element name to parse text
     * @return the text, or null if none cannot be found.
     * @throws JiBXException if any exception occurs
     */
    private String parseElementText(UnmarshallingContext uctx, String elementName, XMPPStreamContext streamCtx) throws JiBXException {
        uctx.parsePastStartTag(NS_STREAM_SASL, elementName);
        String text = uctx.parseContentText();
        uctx.toEnd();
        streamCtx.getReader().flushLog();
        return text;
    }

    /**
     * will parse and throw exception if failure during negotiation is caught
     * 
     * @param uctx
     * @throws JiBXException
     * @throws XMPPException
     */
    protected void parseAndThrowFailure(UnmarshallingContext uctx, XMPPStreamContext streamCtx) throws JiBXException, XMPPException {
        if (uctx.isAt(NS_STREAM_SASL, "failure")) {
            uctx.parsePastStartTag(NS_STREAM_SASL, "failure");
            uctx.toTag();
            String errorType = uctx.getName();
            uctx.parsePastElement(NS_STREAM_SASL, errorType);
            uctx.toEnd();
            streamCtx.getReader().stopLogging();
            if (log.isInfoEnabled())
                log.info("SASL auth negotiation failed with error " + errorType);
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
        if (log.isDebugEnabled()) {
            Iterator iter = features.getSaslMechanisms().iterator();
            StringBuffer buf = new StringBuffer("Available Mechanism: ");
            while (iter.hasNext())
                buf.append(iter.next() + ",");
            log.debug(buf.toString());
        }
        if (features.isSaslMechanismSupported(DIGEST_MD5))
            return DIGEST_MD5;
        if (features.isSaslMechanismSupported(PLAIN))
            return PLAIN;
        return null;
    }
}
