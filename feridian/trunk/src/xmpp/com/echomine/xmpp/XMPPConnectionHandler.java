package com.echomine.xmpp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.net.SocketHandler;
import com.echomine.util.IOUtil;
import com.echomine.xmpp.stream.TLSHandshakeStream;
import com.echomine.xmpp.stream.XMPPClientHandshakeStream;
import com.echomine.xmpp.stream.XMPPConnectionContext;

/**
 * The handler for working with the xmpp client connection. The handler will
 * actually delegate the work to Streams that handle all the incoming and
 * outgoing parsing.
 */
public class XMPPConnectionHandler implements SocketHandler {
    private static final Log log = LogFactory.getLog(XMPPConnectionHandler.class);
    protected XMPPClientContext clientCtx;
    protected XMPPConnectionContext connCtx;
    protected boolean shutdown;
    protected XMPPStreamWriter writer;
    protected UnmarshallingContext uctx;

    /**
     * The constructor for the handler. It accepts a connection context to use
     * the data stored or to store any connection-related data.
     * 
     * @param connCtx the connection context
     */
    public XMPPConnectionHandler(XMPPClientContext clientCtx) {
        if (clientCtx == null)
            throw new IllegalArgumentException("Client Context cannot be null");
        this.clientCtx = clientCtx;
        connCtx = new XMPPConnectionContext();
        shutdown = false;
        writer = new XMPPStreamWriter(XMPPConstants.STREAM_URIS);
        uctx = new UnmarshallingContext();
    }

    /*
     * The main ahndler method.
     * 
     * @see com.echomine.net.SocketHandler#handle(alt.java.net.Socket)
     */
    public void handle(Socket socket) throws IOException {
        connCtx.setSocket(socket);
        try {
            //set socket keepalive
            socket.setKeepAlive(true);
            //by setting output and document, the writer and unmarshalling
            // context
            //will get resetted
            writer.setOutput(socket.getOutputStream());
            uctx.setDocument(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            //handshake processing
            XMPPClientHandshakeStream handshakeStream = new XMPPClientHandshakeStream();
            handshakeStream.process(clientCtx, connCtx, uctx, writer);
            //Determine whether to do TLS processing
            if (connCtx.isTLSSupported()) {
                TLSHandshakeStream tlsStream = new TLSHandshakeStream();
                tlsStream.process(clientCtx, connCtx, uctx, writer);
                socket = connCtx.getSocket();
                //redo the handshake process
                handshakeStream.process(clientCtx, connCtx, uctx, writer);
            }
            //Determine whether to do SASL processing
            //Start normal session processing
        } catch (Exception ex) {
            if (log.isWarnEnabled())
                log.warn("Exception occurred during socket processing", ex);
        } finally {
            //disconnected from server, close streams but not the socket
            if (writer != null)
                writer.close();
            shutdown();
        }
    }

    /*
     * Resets the data before a connection begins for reusing the handler
     * 
     * @see com.echomine.net.SocketHandler#start()
     */
    public void start() {
        shutdown = false;
        connCtx.reset();
    }

    /*
     * shutdown the connection
     * 
     * @see com.echomine.net.SocketHandler#shutdown()
     */
    public void shutdown() {
        shutdown = true;
        IOUtil.closeSocket(connCtx.getSocket());
    }

}
