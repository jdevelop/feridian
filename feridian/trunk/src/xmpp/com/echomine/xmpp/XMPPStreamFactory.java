package com.echomine.xmpp;

import com.echomine.feridian.FeridianConfiguration;
import com.echomine.util.ClassUtil;

/**
 * This is an abstract factory that all implementing factories must extend from.
 * The factory is the main access to obtaining stream processors that can
 * process the XMPP protocol protocol. It can also be extended to support
 * additional new stream features and others.
 */
public abstract class XMPPStreamFactory {
    private static XMPPStreamFactory factory;

    public static XMPPStreamFactory getFactory() throws XMPPException {
        if (factory != null)
            return factory;
        try {
            Class factoryClass = FeridianConfiguration.getConfig().getXMPPStreamFactory();
            factory = (XMPPStreamFactory) ClassUtil.newInstance(factoryClass, XMPPStreamFactory.class);
            return factory;
        } catch (Exception ex) {
            throw new XMPPException("Unable to instantitate factory", ex);
        }
    }

    /**
     * creates the stream for the specified namespace. If no stream is available
     * to process the namespace, then null is returned.
     * 
     * @param namespace the namespace associated with the stream
     * @return the xmpp stream, or null if one cannot be found
     */
    public abstract IXMPPStream createStream(String namespace);
}
