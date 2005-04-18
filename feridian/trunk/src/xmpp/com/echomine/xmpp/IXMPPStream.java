package com.echomine.xmpp;

import org.jibx.runtime.impl.UnmarshallingContext;

import com.echomine.jibx.XMPPStreamWriter;

/**
 * The interface in which all Streams handlers must implement.
 */
public interface IXMPPStream {
    /**
     * Does the processing of the XMPP stream.  The unmarshalling context
     * should be positioned right at the start of the element that is required 
     * by the stream to process.
     * @param connCtx the connection context
     * @param uctx the unmarshalling context
     * @param writer the output stream writer
     * @throws XMPPException if any exceptions occur
     */
    public void process(XMPPConnectionContext connCtx, UnmarshallingContext uctx, XMPPStreamWriter writer) throws XMPPException;
}
