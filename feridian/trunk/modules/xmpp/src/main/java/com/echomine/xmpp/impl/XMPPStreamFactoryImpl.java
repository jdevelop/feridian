package com.echomine.xmpp.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.echomine.feridian.FeridianConfiguration;
import com.echomine.xmpp.IXMPPStream;
import com.echomine.xmpp.XMPPStreamFactory;

/**
 * Default implements for the XMPPStreamFactory. It will search namespaces
 * through the ones registered with the configuration file.
 */
public class XMPPStreamFactoryImpl extends XMPPStreamFactory {
    private final static Log log = LogFactory.getLog(XMPPStreamFactoryImpl.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.echomine.xmpp.XMPPStreamFactory#createStream(java.lang.String)
     */
    public IXMPPStream createStream(String namespace) {
        try {
            Class streamCls = FeridianConfiguration.getConfig().getStreamForFeature(namespace);
            if (streamCls != null)
                return (IXMPPStream) streamCls.newInstance();
        } catch (Exception ex) {
            // intentionally ignored
            if (log.isWarnEnabled())
                log.warn("Error when retrieve stream feature class", ex);
        }
        return null;
    }

}
