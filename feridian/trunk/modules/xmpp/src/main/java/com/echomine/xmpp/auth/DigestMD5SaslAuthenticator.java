package com.echomine.xmpp.auth;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStreamContext;
import com.echomine.xmpp.auth.sasl.DigestMD5SaslClient;

/**
 * This is the main authenticator that performs SASL digest-md5 authentication.
 * This particular authenticator uses a custom implementation of the SASL.
 * 
 * @author ckchris
 * @since 1.0b5
 */
public class DigestMD5SaslAuthenticator extends BaseSaslAuthenticator {
    private static Log log = LogFactory.getLog(DigestMD5SaslAuthenticator.class);
    private static final String DIGEST_MD5 = "DIGEST-MD5";
    private static final String CHALLENGE_ELEMENT_NAME = "challenge";
    private static final String RESPONSE_ELEMENT_NAME = "response";

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPAuthenticator#canAuthenticate(com.echomine.xmpp.XMPPSessionContext,
     *      com.echomine.xmpp.XMPPStreamContext)
     */
    public boolean canAuthenticate(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) {
        if (!streamCtx.getFeatures().isSaslSupported()) return false;
        return streamCtx.getFeatures().isSaslMechanismSupported(DIGEST_MD5);
    }

    /*
     * Authenticates using the SASL digest-md5 mechanism
     * 
     * @see com.echomine.xmpp.auth.BaseSaslAuthenticator#processSasl(int,
     *      org.jibx.runtime.impl.UnmarshallingContext,
     *      com.echomine.jibx.XMPPStreamWriter,
     *      com.echomine.xmpp.XMPPSessionContext,
     *      com.echomine.xmpp.XMPPStreamContext)
     */
    @Override
    protected void processSasl(int idx, UnmarshallingContext uctx, XMPPStreamWriter writer, XMPPSessionContext sessCtx, XMPPStreamContext streamCtx)
            throws IOException, JiBXException, XMPPException {
        // send <stream>
        writer.startTagNamespaces(idx, "auth", new int[] { idx }, new String[] { "" });
        writer.addAttribute(0, "mechanism", DIGEST_MD5);
        writer.closeEmptyTag();
        writer.flush();
        // synchronize the first access in case connection handler read extra
        // data
        synchronized (uctx) {
            if (!uctx.isAt(NS_STREAM_SASL, CHALLENGE_ELEMENT_NAME)
                    && !uctx.isAt(NS_STREAM_SASL, FAILURE_ELEMENT_NAME))
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
        if (log.isInfoEnabled()) log.info("Sending response: " + response);
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
}
