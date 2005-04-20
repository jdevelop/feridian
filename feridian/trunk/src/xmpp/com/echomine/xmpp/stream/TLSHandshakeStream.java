package com.echomine.xmpp.stream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPStreamWriter;
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
    protected final static int SOCKETBUF = 8192;

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
        if (!connCtx.getTLSFeature().tlsSupported)
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
            if (uctx.isAt(NS_TLS, "failure"))
                throw new XMPPException("TLS Failure");
            if (!uctx.isAt(NS_TLS, "proceed"))
                throw new XMPPException("Expecting <proceed> tag, but found: " + uctx.getName());
            uctx.parsePastEndTag(NS_TLS, "proceed");
            SSLSocket tlsSocket = startTLSHandshake(connCtx.getSocket());
            connCtx.setSocket(tlsSocket);
            writer.flush();
            BufferedInputStream bis = new BufferedInputStream(tlsSocket.getInputStream(), SOCKETBUF);
            BufferedOutputStream bos = new BufferedOutputStream(tlsSocket.getOutputStream(), SOCKETBUF);
            //setting output and document automatically reset the writer and
            // context
            writer.setOutput(bos);
            uctx.setDocument(bis, "UTF-8");
            connCtx.reset();
        } catch (IOException ex) {
            throw new XMPPException(ex);
        } catch (JiBXException ex) {
            throw new XMPPException(ex);
        } catch (KeyManagementException ex) {
            throw new XMPPException(ex);
        } catch (NoSuchAlgorithmException ex) {
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
    protected SSLSocket startTLSHandshake(Socket socket) throws KeyManagementException, NoSuchAlgorithmException, IOException {
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
    protected SSLSocket setupSSLSocket(Socket socket) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        // Create an SSL context
        SSLContext context = null;
        context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        SSLSocketFactory factory = context.getSocketFactory();
        //test codes will not go through SSL
        SSLSocket sslsocket = (SSLSocket) factory.createSocket(socket, socket.getInetAddress().getHostAddress(), socket.getPort(), true);
        sslsocket.setUseClientMode(true);
        return sslsocket;
    }
}
