package com.echomine.xmpp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.net.SocketHandler;
import com.echomine.util.IOUtil;

/**
 * The handler for working with the xmpp client connection. The handler will
 * actually delegate the work to Streams that handle all the incoming and
 * outgoing parsing.
 */
public class XMPPConnectionHandler implements SocketHandler {
    private static final Log log = LogFactory.getLog(XMPPConnectionHandler.class);
    protected final static int SOCKETBUF = 8192;
    protected XMPPConnectionContext ctx;
    protected boolean shutdown;
    protected Socket socket;
    protected XMPPStreamWriter writer;
    protected UnmarshallingContext uctx;

    /**
     * The constructor for the handler. It accepts a connection context to use
     * the data stored or to store any connection-related data.
     * 
     * @param ctx the connection context
     */
    public XMPPConnectionHandler(XMPPConnectionContext ctx) {
        this.ctx = ctx;
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
        this.socket = socket;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            //set socket keepalive
            socket.setKeepAlive(true);
            //by setting output and document, the writer and unmarshalling
            // context
            //will get resetted
            writer.setOutput(socket.getOutputStream());
            uctx.setDocument(socket.getInputStream(), "UTF-8");
            //handshake processing
            XMPPClientHandshakeStream handshakeStream = new XMPPClientHandshakeStream();
            handshakeStream.process(ctx, uctx, writer);
            //Determine whether to do TLS processing
            if (ctx.getTLSFeature().tlsSupported) {
                TLSHandshakeStream tlsStream = new TLSHandshakeStream();
                tlsStream.setSocket(socket);
                tlsStream.process(ctx, uctx, writer);
                this.socket = socket = tlsStream.getTLSSocket();
                writer.flush();
                bis = new BufferedInputStream(socket.getInputStream(), SOCKETBUF);
                bos = new BufferedOutputStream(socket.getOutputStream(), SOCKETBUF);
                writer.setOutput(bos);
                uctx.setDocument(bis, "UTF-8");
                //redo the handshake process
                handshakeStream.process(ctx, uctx, writer);
            }
            //Determine whether to do SASL processing
            //Start normal session processing
        } catch (Exception ex) {
            if (log.isWarnEnabled())
                log.warn("Exception occurred during socket processing", ex);
        } finally {
            shutdown();
            //disconnected from server, close streams but not the socket
            if (writer != null)
                writer.close();
            IOUtil.closeStream(bis);
        }
    }

    /*
     * Resets the data before a connection begins for reusing the handler
     * 
     * @see com.echomine.net.SocketHandler#start()
     */
    public void start() {
        shutdown = false;
    }

    /*
     * shutdown the connection
     * 
     * @see com.echomine.net.SocketHandler#shutdown()
     */
    public void shutdown() {
        shutdown = true;
        IOUtil.closeSocket(socket);
    }

}
