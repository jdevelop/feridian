package com.echomine.xmpp.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.JiBXOutputStreamWrapper;
import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.net.SocketHandler;
import com.echomine.util.IOUtil;
import com.echomine.xmpp.XMPPClientContext;
import com.echomine.xmpp.XMPPConstants;
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

    protected final static int SOCKETBUF = 8192;
    protected XMPPClientContext clientCtx;
    protected XMPPConnectionContext connCtx;
    protected boolean shutdown;
    protected XMPPStreamWriter writer;
    protected UnmarshallingContext uctx;
    protected TLSHandshakeStream tlsStream;

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
        uctx = new UnmarshallingContext();
        tlsStream = new TLSHandshakeStream();
    }

    /*
     * The main ahndler method.
     * 
     * @see com.echomine.net.SocketHandler#handle(alt.java.net.Socket)
     */
    public void handle(Socket socket) throws IOException {
        start();
        connCtx.setHost(clientCtx.getHost());
        connCtx.setSocket(socket);
        try {
            // set socket keepalive
            socket.setKeepAlive(true);
            // by setting output and document, the writer and unmarshalling
            // context will get resetted
            writer.setOutput(new JiBXOutputStreamWrapper(socket.getOutputStream()));
            uctx.setDocument(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            // handshake processing
            XMPPClientHandshakeStream handshakeStream = new XMPPClientHandshakeStream();
            handshakeStream.process(clientCtx, connCtx, uctx, writer);
            // Determine whether to do TLS processing
            if (connCtx.isTLSSupported()) {
                tlsStream.process(clientCtx, connCtx, uctx, writer);
                socket = connCtx.getSocket();
                InputStreamReader bis = new InputStreamReader(socket.getInputStream(), "UTF-8");
                BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream(), SOCKETBUF);
                // Workaround for JiBX's reset() not resetting prefix
                // Thus, a new stream writer must be created
                writer.close();
                writer = new XMPPStreamWriter();
                writer.setOutput(bos);
                uctx.setDocument(bis);
                connCtx.reset();
                // redo the handshake process
                handshakeStream.process(clientCtx, connCtx, uctx, writer);
            }
            // TODO: SASL processing
            // Resource binding MUST be done if set
            if (connCtx.isResourceBindingRequired()) {

            }
            if (connCtx.isSessionRequired()) {

            }
            // Start normal session processing
        } catch (Exception ex) {
            if (log.isWarnEnabled())
                log.warn("Exception occurred during socket processing", ex);
        } finally {
            try {
                endStream();
                // disconnected from server, close streams but not the socket
                if (writer != null)
                    writer.close();
            } catch (IOException ex) {
                // intentionally left blank
            }
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
        writer = new XMPPStreamWriter();
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

    /**
     * Ends the stream due to either receiving an error from remote entity or
     * any error encountered here. The method will not flush or close the
     * underlying stream.
     */
    protected void endStream() throws IOException {
        writer.endTag(XMPPConstants.IDX_JABBER_STREAM, "stream");
    }
}
