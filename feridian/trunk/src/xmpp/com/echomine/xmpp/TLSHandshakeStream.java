package com.echomine.xmpp;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import java.net.Socket;

import com.echomine.jibx.XMPPStreamWriter;

/**
 * This stream will handle all the TLS handshaking. It will first determine
 * if the remote entity supports TLS.  If not, it does nothing.  The
 * stream will follow the XMPP specification procedure:
 * <br/>
 * <ol>
 * <li>Send starttls command</li>
 * <li>Remote replies with either proceed or failure</li>
 * <li>If failure during negotiation, then close stream/connection</li>
 * </ol>
 * <br/>
 * The stream handler will process up to the point of successful TLS
 * handshake.  If handshaking fails, then an exception will be thrown,
 * in effect closing our side of the stream and connection.
 */
public class TLSHandshakeStream implements IXMPPStream, XMPPConstants {
    private static Log log = LogFactory.getLog(TLSHandshakeStream.class);
    private static final String STARTTLS_ELEMENT_NAME = "starttls";
    
    private Socket clearSocket;
    private SSLSocket tlsSocket;
    
    public TLSHandshakeStream() {
        super();
    }

    /**
     * set the currently open socket
     * @param socket
     */
    public void setSocket(Socket socket) {
        this.clearSocket = socket;
    }
    
    /**
     * obtains the new secure socket.  Or null if TLS was not successful
     * @return the new secure socket
     */
    public SSLSocket getTLSSocket() {
        return tlsSocket;
    }
    
    /*
     * @see com.echomine.xmpp.IXMPPStream#process(com.echomine.xmpp.XMPPConnectionContext, org.jibx.runtime.impl.UnmarshallingContext, com.echomine.jibx.XMPPStreamWriter)
     */
    public void process(XMPPConnectionContext connCtx, UnmarshallingContext uctx, XMPPStreamWriter writer) throws XMPPException {
        if (!connCtx.getTLSFeature().tlsSupported) return;
        String[] extns = new String[] { NS_TLS };
        writer.pushExtensionNamespaces(extns);
        int idx = writer.getNamespaces().length;
        try {
            //send starttls
	        writer.startTagNamespaces(idx, STARTTLS_ELEMENT_NAME, new int[] { idx }, new String[] { "" });
	        writer.closeEmptyTag();
	        writer.flush();
	        //check for error or proceed
	        if (uctx.isAt(NS_TLS, "failure"))
	            throw new XMPPException("TLS Failure");
	        if (uctx.isAt(NS_TLS, "proceed"))
	            uctx.parsePastEndTag(NS_TLS, "proceed");
	        tlsSocket = startTLSHandshake();
        } catch (IOException ex) {
            throw new XMPPException(ex);
        } catch (JiBXException ex) {
            throw new XMPPException(ex);
        }
    }

    /**
     * Start the phyical TLS handshake
     * @throws IOException
     */
    protected SSLSocket startTLSHandshake() throws IOException {
        // Create an SSL context
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
            SSLSocketFactory factory = context.getSocketFactory();
            //test codes will not go through SSL
            SSLSocket sslsocket = (SSLSocket) factory.createSocket(clearSocket, clearSocket.getInetAddress().getHostAddress(), clearSocket.getPort(), true);
            sslsocket.startHandshake();
            return sslsocket;
        } catch (NoSuchAlgorithmException ex) {
            if (log.isErrorEnabled())
                log.error("Unable to obtain the TLS algorithm", ex);
            throw new IOException("Unable to obtain the TLS algorithm from SSL Context");
        }
    }
}
