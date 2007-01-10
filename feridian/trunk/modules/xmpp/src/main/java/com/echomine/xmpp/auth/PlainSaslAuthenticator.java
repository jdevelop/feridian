package com.echomine.xmpp.auth;

import java.io.IOException;

import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.util.Base64;
import com.echomine.xmpp.JID;
import com.echomine.xmpp.XMPPAuthCallback;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStreamContext;

/**
 * This is the main authenticator that performs SASL Plain authentication. This
 * particular authenticator uses a custom implementation of the SASL.
 * 
 * @author ckchris
 * @since 1.0b5
 */
public class PlainSaslAuthenticator extends BaseSaslAuthenticator {
    private static final String PLAIN = "PLAIN";

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPAuthenticator#canAuthenticate(com.echomine.xmpp.XMPPSessionContext,
     *      com.echomine.xmpp.XMPPStreamContext)
     */
    public boolean canAuthenticate(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) {
        if (!streamCtx.getFeatures().isSaslSupported()) return false;
        return streamCtx.getFeatures().isSaslMechanismSupported(PLAIN);
    }

    /*
     * Authenticates using the SASL PLAIN mechanism
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
        writer.addAttribute(0, "mechanism", PLAIN);
        writer.closeStartTag();
        XMPPAuthCallback callback = streamCtx.getAuthCallback();
        JID authid = new JID(callback.getUsername(), sessCtx.getHostName(), callback.getResource());
        StringBuffer buf = new StringBuffer(128);
        buf.append(authid.getJIDWithoutResource()).append('\0').append(callback.getUsername()).append('\0').append(callback.getPassword());
        writer.writeTextContent(Base64.encodeString(buf.toString()));
        writer.endTag(idx, "auth");
        // send response immediately
        writer.flush();
        synchronized (uctx) {
            if (!uctx.isAt(NS_STREAM_SASL, "success")
                    && !uctx.isAt(NS_STREAM_SASL, FAILURE_ELEMENT_NAME))
                uctx.next();
        }
        // receive final success or failure
        parseAndThrowFailure(uctx, streamCtx);
        if (!uctx.isAt(NS_STREAM_SASL, "success"))
            throw new XMPPException("Expecting <success> tag, but found: "
                    + uctx.getName());
        parseElementText(uctx, "success", streamCtx);
        streamCtx.getReader().flushLog();
    }
}
