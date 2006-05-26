package com.echomine.xmpp.stream;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPLoggableReader;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.util.SimpleTrustManager;
import com.echomine.xmpp.IXMPPStream;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;
import com.echomine.xmpp.XMPPSessionContext;
import com.echomine.xmpp.XMPPStreamContext;

/**
 * This stream will handle all the TLS handshaking. It will first determine if
 * the remote entity supports TLS. If not, it does nothing. The stream will
 * follow the XMPP specification procedure: <br/>
 * <ol>
 * <li>Send starttls command</li>
 * <li>Remote replies with either proceed or failure</li>
 * <li>If failure during negotiation, then close stream/connection</li>
 * </ol>
 * <br/>The stream handler will process up to the point of successful TLS
 * handshake. If handshaking fails, then an exception will be thrown, in effect
 * closing our side of the stream and connection. <br/>Once TLS negotiation
 * succeeds, the entire writer and unmarshalling context will be redone to use
 * the new input/output streams from the SSL socket. The original socket will
 * also be replaced with the new SSL socket. This stream will NOT redo the
 * handshake. Thus, the caller must subsequently redo the handshake after TLS
 * negotiation succeeds.
 */
public class TLSHandshakeStream implements IXMPPStream, XMPPConstants {
    private static final String STARTTLS_ELEMENT_NAME = "starttls";
    protected final static int SOCKETBUF = 8192;

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPStream#process(com.echomine.xmpp.XMPPSessionContext,
     *      com.echomine.xmpp.XMPPStreamContext)
     */
    public void process(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) throws XMPPException {
        if (!streamCtx.getFeatures().isTLSSupported())
            return;
        XMPPStreamWriter writer = streamCtx.getWriter();
        UnmarshallingContext uctx = streamCtx.getUnmarshallingContext();
        String[] extns = new String[] { NS_STREAM_TLS };
        writer.pushExtensionNamespaces(extns);
        int idx = writer.getNamespaces().length;
        try {
            // send starttls
            writer.startTagNamespaces(idx, STARTTLS_ELEMENT_NAME, new int[] { idx }, new String[] { "" });
            writer.closeEmptyTag();
            writer.flush();
            // start logging
            streamCtx.getReader().startLogging();
            // no need to sync the first unmarshalling cuz TLS handshake
            // occurs before async packet processing begins.
            // check for error or proceed
            uctx.next();
            if (uctx.isAt(NS_STREAM_TLS, "failure")) {
                uctx.toEnd();
                throw new XMPPException("TLS Failure");
            }
            if (!uctx.isAt(NS_STREAM_TLS, "proceed"))
                throw new XMPPException("Expecting <proceed> tag, but found: " + uctx.getName());
            uctx.toEnd();
            streamCtx.getReader().stopLogging();
            SSLSocket tlsSocket = startTLSHandshake(streamCtx.getSocket());
            streamCtx.setSocket(tlsSocket);
            // Workaround for JiBX's reset() not resetting prefix
            // Thus, a new stream writer must be created
            XMPPLoggableReader bis = new XMPPLoggableReader(tlsSocket.getInputStream(), "UTF-8");
            BufferedOutputStream bos = new BufferedOutputStream(tlsSocket.getOutputStream(), SOCKETBUF);
            writer.flush();
            writer = new XMPPStreamWriter();
            writer.setOutput(bos);
            uctx.setDocument(bis);
            streamCtx.setSocket(tlsSocket);
            streamCtx.setWriter(writer);
            streamCtx.setUnmarshallingContext(uctx);
            streamCtx.setReader(bis);
            // as per XMPP specs, must reset all previous session data
            String hostname = sessCtx.getHostName();
            sessCtx.reset();
            sessCtx.setHostName(hostname);
            streamCtx.clearFeatures();
        } catch (Exception ex) {
            if (ex instanceof XMPPException)
                throw (XMPPException) ex;
            throw new XMPPException(ex);
        }
    }

    /**
     * Start the phyical TLS handshake
     * 
     * @param socket the socket to do TLS over
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    protected SSLSocket startTLSHandshake(Socket socket) throws KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, IOException {
        SSLSocket sslsocket = setupSSLSocket(socket);
        sslsocket.startHandshake();
        return sslsocket;
    }

    /**
     * sets up the SSL socket for use and does any key management, trust
     * manager, etc initialization.
     * 
     * @param socket the socket to do TLS over
     * @throws IOException
     */
    protected SSLSocket setupSSLSocket(Socket socket) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, IOException {
        // Create an SSL context
        SSLContext context = null;
        context = SSLContext.getInstance("TLS");
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        TrustManagerFactory tfactory = TrustManagerFactory.getInstance("SunPKIX");
        tfactory.init(keyStore);
        SimpleTrustManager tmanager = new SimpleTrustManager(keyStore, System.getProperty("user.home") + System.getProperty("file.separator") + ".keystore", null);
        context.init(null, new TrustManager[] { tmanager }, null);
        SSLSocketFactory factory = context.getSocketFactory();
        SSLSocket sslsocket = (SSLSocket) factory.createSocket(socket, socket.getInetAddress().getHostAddress(), socket.getPort(), true);
        sslsocket.setUseClientMode(true);
        return sslsocket;
    }
}
