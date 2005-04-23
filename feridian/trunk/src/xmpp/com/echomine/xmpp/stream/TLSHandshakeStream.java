package com.echomine.xmpp.stream;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.util.SimpleTrustManager;
import com.echomine.xmpp.IXMPPStream;
import com.echomine.xmpp.XMPPClientContext;
import com.echomine.xmpp.XMPPConstants;
import com.echomine.xmpp.XMPPException;

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
 * closing our side of the stream and connection.
 */
public class TLSHandshakeStream implements IXMPPStream, XMPPConstants {
    private static Log log = LogFactory.getLog(TLSHandshakeStream.class);
    private static final String STARTTLS_ELEMENT_NAME = "starttls";

    public TLSHandshakeStream() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.IXMPPStream#process(com.echomine.xmpp.XMPPClientContext,
     *      com.echomine.xmpp.stream.XMPPConnectionContext,
     *      org.jibx.runtime.impl.UnmarshallingContext,
     *      com.echomine.jibx.XMPPStreamWriter)
     */
    public void process(XMPPClientContext clientCtx, XMPPConnectionContext connCtx, UnmarshallingContext uctx, XMPPStreamWriter writer) throws XMPPException {
        if (!connCtx.isTLSSupported())
            return;
        String[] extns = new String[] { NS_TLS };
        writer.pushExtensionNamespaces(extns);
        int idx = writer.getNamespaces().length;
        try {
            //send starttls
            writer.startTagNamespaces(idx, STARTTLS_ELEMENT_NAME, new int[] { idx }, new String[] { "" });
            writer.closeEmptyTag();
            writer.flush();
            //check for error or proceed
            int eventType = uctx.next();
            if (uctx.isAt(NS_TLS, "failure"))
                throw new XMPPException("TLS Failure");
            if (!uctx.isAt(NS_TLS, "proceed"))
                throw new XMPPException("Expecting <proceed> tag, but found: " + uctx.getName());
            uctx.toEnd();
            SSLSocket tlsSocket = startTLSHandshake(connCtx.getSocket());
            connCtx.setSocket(tlsSocket);
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
        //test codes will not go through SSL
        SSLSocket sslsocket = (SSLSocket) factory.createSocket(socket, socket.getInetAddress().getHostAddress(), socket.getPort(), true);
        sslsocket.setUseClientMode(true);
        return sslsocket;
    }
}
