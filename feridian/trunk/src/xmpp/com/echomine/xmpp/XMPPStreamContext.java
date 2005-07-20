package com.echomine.xmpp;

import java.net.Socket;

import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPStreamWriter;
import com.echomine.xmpp.packet.StreamFeatures;

/**
 * This is the context in which stream share information with other streams or
 * objects.
 */
public class XMPPStreamContext {
    private UnmarshallingContext uctx;
    private XMPPStreamWriter writer;
    private Socket socket;
    private StreamFeatures features;

    public XMPPStreamContext() {
        reset();
    }

    /**
     * Retrieves the unmarshalling context
     * 
     * @return the context
     */
    public UnmarshallingContext getUnmarshallingContext() {
        return uctx;
    }

    /**
     * Sets the unmarshalling context
     * 
     * @param uctx the context
     */
    public void setUnmarshallingContext(UnmarshallingContext uctx) {
        this.uctx = uctx;
    }

    /**
     * @return the output xmpp writer
     */
    public XMPPStreamWriter getWriter() {
        return writer;
    }

    /**
     * sets the output xmpp writer
     * 
     * @param writer the writer
     */
    public void setWriter(XMPPStreamWriter writer) {
        this.writer = writer;
    }

    /**
     * @return Returns the socket.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * @param socket The socket to set.
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * resets the data in this class so that the class can be reused.
     */
    public void reset() {
        uctx = new UnmarshallingContext();
        writer = new XMPPStreamWriter();
        socket = null;
    }

    /**
     * retrieves the features supported by the remote entity. This is
     * essentially a feature packet.
     * 
     * @return Returns the features.
     */
    public StreamFeatures getFeatures() {
        if (features == null)
            features = new StreamFeatures();
        return features;
    }

    /**
     * sets the features supported by the remote entity.
     * 
     * @param features The features to set.
     */
    public void setFeatures(StreamFeatures features) {
        this.features = features;
    }
}
