package com.echomine.jabber.compat.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jabber.compat.packet.AuthIQPacket;
import com.echomine.jibx.JiBXUtil;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.util.HexDec;
import com.echomine.xmpp.IDGenerator;
import com.echomine.xmpp.IXMPPAuthenticator;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStanzaErrorException;
import com.echomine.xmpp.XMPPStreamContext;
import com.echomine.xmpp.packet.IQPacket;
import com.echomine.xmpp.packet.StreamFeatures;

/**
 * <p>
 * This authentication stream will perform non-sasl authentication, essentially
 * following JEP-0078. This stream will only work with Plain and Digest
 * password. Note that zero-knowledge authentication is deprecated. This stream
 * will also prefer Digest over Plain if available. In addition, this stream
 * will double check first to see that SASL is not supported. If SASL is
 * supported by the remote entity, then this stream will not authenticate as
 * stated by the JEP. Only if SASL is not supported will this stream continue.
 * Lastly, if no features exist whatsoever, then we assume the server is an old
 * server that only supports Non-SASL authentication, in which case, this
 * authenticator will perform its duty.
 * </p>
 * <p>Implementation: <a href="http://www.jabber.org/jeps/jep-0078.html">JEP-0078</a> version 2.2</p>
 */
public class NonSASLAuthenticator implements IXMPPAuthenticator {
    private static final Log log = LogFactory.getLog(NonSASLAuthenticator.class);
    private static final String NS_IQ_AUTH = "http://jabber.org/features/iq-auth";

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPAuthenticator#canAuthenticate(com.echomine.xmpp.XMPPSessionContext,
     *      com.echomine.xmpp.XMPPStreamContext)
     */
    public boolean canAuthenticate(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) {
        StreamFeatures features = streamCtx.getFeatures();
        if (features.isSaslSupported())
            return false;
        if (features.isFeatureSupported(NS_IQ_AUTH))
            return true;
        if (features.getFeatures().isEmpty())
            return true;
        return false;
    }

    /**
     * Non-SASL authentication does not require stream renegotiation.
     * 
     * @return false
     */
    public boolean redoHandshake() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPStream#process(com.echomine.xmpp.XMPPSessionContext,
     *      com.echomine.xmpp.XMPPStreamContext)
     */
    public void process(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) throws XMPPException {
        if (!canAuthenticate(sessCtx, streamCtx))
            throw new XMPPException("Either SASL authentication is supported or iq-auth not supported");
        // start logging
        streamCtx.getReader().startLogging();
        try {
            // first obtain what remote server supports
            AuthIQPacket packet = getInitialAuthResponse(sessCtx, streamCtx);
            // prefer digest over plain
            if (packet.isDigestSupported())
                doDigestAuthentication(sessCtx, streamCtx);
            else if (packet.isPlainSupported())
                doPlainAuthentication(sessCtx, streamCtx);
            else
                throw new XMPPException("Unable to find a supported non-SASL authentication method");
        } catch (JiBXException ex) {
            throw new XMPPException("Error while authenticating", ex);
        } finally {
            streamCtx.getReader().stopLogging();
        }
    }

    /**
     * This will perform plain authentication.
     * 
     * @param sessCtx the session context
     * @param streamCtx the stream context
     * @throws XMPPException if general error occurs or an error reply is
     *             received
     * @throws JiBXException if errors occur during writing or reading from
     *             streams
     */
    private void doPlainAuthentication(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) throws XMPPException, JiBXException {
        if (log.isInfoEnabled())
            log.info("Authenticating using Plain method");
        XMPPStreamWriter writer = streamCtx.getWriter();
        UnmarshallingContext uctx = streamCtx.getUnmarshallingContext();
        AuthIQPacket req = new AuthIQPacket();
        req.setId(IDGenerator.nextID());
        req.setType(IQPacket.TYPE_SET);
        req.setUsername(streamCtx.getAuthCallback().getUsername());
        req.setPassword(String.valueOf(streamCtx.getAuthCallback().getPassword()));
        req.setResource(streamCtx.getAuthCallback().getResource());
        JiBXUtil.marshallIQPacket(writer, req);
        // read the response
        IQPacket result = (IQPacket) JiBXUtil.unmarshallObject(uctx, IQPacket.class);
        streamCtx.getReader().flushLog();
        if (result == null)
            throw new XMPPException("No Valid Result Packet received");
        if (result.isError())
            throw new XMPPStanzaErrorException(result.getError());
    }

    /**
     * This will perform digest authentication.
     * 
     * @param sessCtx the session context
     * @param streamCtx the stream context
     * @throws XMPPException if general error occurs or an error reply is
     *             received
     * @throws JiBXException if errors occur during writing or reading from
     *             streams
     */
    private void doDigestAuthentication(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) throws XMPPException, JiBXException {
        if (log.isInfoEnabled())
            log.info("Authenticating using Digest method");
        XMPPStreamWriter writer = streamCtx.getWriter();
        UnmarshallingContext uctx = streamCtx.getUnmarshallingContext();
        AuthIQPacket req = new AuthIQPacket();
        req.setId(IDGenerator.nextID());
        req.setType(IQPacket.TYPE_SET);
        req.setUsername(streamCtx.getAuthCallback().getUsername());
        req.setDigest(getDigestPassword(sessCtx.getStreamId(), streamCtx.getAuthCallback().getPassword()));
        req.setResource(streamCtx.getAuthCallback().getResource());
        JiBXUtil.marshallIQPacket(writer, req);
        // read the response
        IQPacket result = (IQPacket) JiBXUtil.unmarshallObject(uctx, IQPacket.class);
        streamCtx.getReader().flushLog();
        if (result == null)
            throw new XMPPException("No Valid Result Packet received");
        if (result.isError())
            throw new XMPPStanzaErrorException(result.getError());
    }

    /**
     * Calculates the digest password used for authentication. The digest is
     * calculated as follows, according JEP-0078:
     * <ol>
     * <li>Concatenate the Stream ID received from the server with the
     * password.</li>
     * <li>Hash the concatenated string according to the SHA1 algorithm, i.e.,
     * SHA1(concat(sid, password)).</li>
     * <li>Ensure that the hash output is in hexidecimal format, not binary or
     * base64.</li>
     * <li>Convert the hash output to all lowercase characters.</li>
     * </ol>
     * 
     * @param password the plaintext password
     * @return the digest password
     * @throws XMPPException when error occurs during digesting of data
     */
    private String getDigestPassword(String streamId, char[] password) throws XMPPException {
        // instantiate a SHA1 hash
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(streamId.getBytes());
            md.update(String.valueOf(password).getBytes());
            byte[] hash = md.digest();
            // convert to hex representation
            return HexDec.convertBytesToHexString(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new XMPPException(ex);
        }
    }

    /**
     * Sends the initial auth request and retrieve the response. This first
     * response should indicate to us what authentication methods are supported
     * by the remote entity.
     * 
     * @param sessCtx the session context
     * @param streamCtx the stream context
     * @return the response packet
     * @throws JiBXException when parsing or writing occurs
     * @throws XMPPException if packet recieve is an error response or no result
     *             was received
     */
    private AuthIQPacket getInitialAuthResponse(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) throws JiBXException, XMPPException {
        XMPPStreamWriter writer = streamCtx.getWriter();
        UnmarshallingContext uctx = streamCtx.getUnmarshallingContext();
        AuthIQPacket req = new AuthIQPacket();
        req.setId(IDGenerator.nextID());
        req.setType(IQPacket.TYPE_GET);
        req.setUsername(streamCtx.getAuthCallback().getUsername());
        JiBXUtil.marshallIQPacket(writer, req);
        // synchronized for first access is required to prevent thread racing
        // issue
        synchronized (uctx) {
            if (!uctx.isAt(XMPPConstants.NS_XMPP_CLIENT, "iq"))
                uctx.next();
        }
        AuthIQPacket result = (AuthIQPacket) JiBXUtil.unmarshallObject(uctx, IQPacket.class);
        streamCtx.getReader().flushLog();
        if (result == null)
            throw new XMPPException("No Valid Result Packet received");
        if (result.isError())
            throw new XMPPStanzaErrorException(result.getError());
        return result;
    }
}
