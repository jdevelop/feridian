package com.echomine.xmpp.auth.sasl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.echomine.util.Base64;
import com.echomine.util.HexDec;
import com.echomine.xmpp.XMPPAuthCallback;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStreamContext;

/**
 * <p>
 * Allows the user of this class to work with SASL in a more simplied manner. It
 * will unwrap, wrap, and work with the different aspects of SASL authentication
 * that is pertinent to XMPP.
 * </p>
 * <p>
 * NOTE: authzid is currently disabled due to an incompatibility issue with some
 * servers. However, almost all servers work fine without the authzid. Until
 * further notice, the authzid is not used.
 * </p>
 */
public class DigestMD5SaslClient {
    private static final Log log = LogFactory.getLog(DigestMD5SaslClient.class);
    private DigestMD5SaslContext challengeCtx = new DigestMD5SaslContext();
    private String cnonce;
    // private JID authzid;
    private String digesturi;

    /**
     * Unwraps the base64-encoded challenge phrase and store the challenge
     * context.
     * 
     * @param challenge the base64-encoded challenge phrase
     */
    public void unwrapChallenge(String challenge) {
        challengeCtx.unwrap(challenge);
    }

    /**
     * Based on the authentication information and the challenge, create a
     * response.
     * 
     * @param streamCtx the stream context
     * @return the auth string
     */
    public String getAuthResponse(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) {
        XMPPAuthCallback auth = streamCtx.getAuthCallback();
        if (auth == null)
            throw new IllegalStateException("Authentication callback must be set");
        StringBuffer buf = new StringBuffer();
        cnonce = generateCNonce();
        digesturi = "xmpp/" + sessCtx.getHostName();
        // authzid = new JID(auth.getUsername(), sessCtx.getHostName(), null);
        buf.append("username=\"").append(auth.getUsername()).append("\"");
        if (challengeCtx.getRealm() != null)
            buf.append(",realm=\"").append(challengeCtx.getRealm()).append("\"");
        buf.append(",nonce=\"").append(challengeCtx.getNonce()).append("\"");
        buf.append(",cnonce=\"").append(cnonce).append("\"");
        buf.append(",nc=00000001");
        buf.append(",qop=auth");
        buf.append(",digest-uri=\"").append(digesturi).append("\"");
        buf.append(",response=\"").append(generatePasswordDigest(sessCtx, streamCtx.getAuthCallback())).append("\"");
        buf.append(",charset=utf-8");
        // buf.append(",authzid=\"").append(authzid.toString()).append("\"");
        if (log.isDebugEnabled())
            log.debug("Response String: " + buf.toString());
        return Base64.encodeString(buf.toString());
    }

    /**
     * Generates the digest string of the password
     * 
     * @return the password digest string
     */
    private String generatePasswordDigest(XMPPSessionContext sessCtx, XMPPAuthCallback callback) {
        MessageDigest md;
        byte[] temp;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            md = MessageDigest.getInstance("MD5");
            String realm = challengeCtx.getRealm() != null ? challengeCtx.getRealm()
                    : "";
            String x = callback.getUsername() + ":" + realm + ":"
                    + new String(callback.getPassword());
            temp = md.digest(x.getBytes());
            bos.write(temp);
            String b = ":" + challengeCtx.getNonce() + ":" + cnonce;  // + ":" + authzid.toString();
            bos.write(b.getBytes());
            byte[] a1 = bos.toByteArray();
            String a2 = "AUTHENTICATE:" + digesturi;
            md.reset();
            String ha1 = HexDec.convertBytesToHexString(md.digest(a1));
            md.reset();
            String ha2 = HexDec.convertBytesToHexString(md.digest(a2.getBytes()));
            md.reset();
            StringBuffer buf = new StringBuffer();
            buf.append(ha1).append(":").append(challengeCtx.getNonce());
            buf.append(":00000001:").append(cnonce).append(":auth:");
            buf.append(ha2);
            String resp = HexDec.convertBytesToHexString(md.digest(buf.toString().getBytes()));
            return resp;
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Specified message digest algorithm not supported");
        } catch (IOException ex) {
            throw new IllegalArgumentException("IOException during hashing");
        }
    }

    /**
     * Generate our own nonce to return. Used to prevent replay attacks. Our
     * generation is rather simple. Current implementation simply uses random
     * bytes
     * 
     * @return
     */
    private String generateCNonce() {
        SecureRandom rand = new SecureRandom();
        byte[] randBytes = new byte[1024];
        rand.nextBytes(randBytes);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return Base64.encodeBytes(md.digest(randBytes));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Specified message digest algorithm not supported");
        }
    }
}
