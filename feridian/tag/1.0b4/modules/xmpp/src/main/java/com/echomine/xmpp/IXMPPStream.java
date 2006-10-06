package com.echomine.xmpp;

/**
 * The interface in which all Streams handlers must implement.
 */
public interface IXMPPStream {
    /**
     * Does the processing of the XMPP stream. The unmarshalling context should
     * be positioned right at the start of the element that is required by the
     * stream to process. Subclasses of XMPPException can be thrown. You may
     * check for those exceptions through the use of "instanceof".  
     * 
     * @param sessCtx the session context
     * @param streamCtx the stream context
     * @throws XMPPException if any exceptions occur
     */
    void process(XMPPSessionContext sessCtx, XMPPStreamContext streamCtx) throws XMPPException;
}
