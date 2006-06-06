package com.echomine.xmpp;

import com.echomine.feridian.FeridianConfiguration;
import com.echomine.util.ClassUtil;

/**
 * This is the main gateway into the XMPP API. This factory allows the user to
 * create multiple instances of XMPP connection objects that are ready for use.
 * The factory will look up the main implementation factory in the
 * configuration. Thus, the factory implementation is pluggable. All factory
 * implementation MUST extend from this class. This factory is intentionally
 * marked abstract because it does not implement all factory-required methods.
 */
public abstract class XMPPConnectionFactory {
    private static XMPPConnectionFactory factory;

    /**
     * Obtains the factory specified in the configuration
     * 
     * @return the factory
     * @throws XMPPException if error occurs while trying to get factory
     */
    public static final XMPPConnectionFactory getFactory() throws XMPPException {
        if (factory != null)
            return factory;
        try {
            Class factoryClass = FeridianConfiguration.getConfig().getXMPPConnectionFactory();
            factory = (XMPPConnectionFactory) ClassUtil.newInstance(factoryClass, XMPPConnectionFactory.class);
            return factory;
        } catch (Exception ex) {
            throw new XMPPException("Unable to instantitate factory", ex);
        }
    }

    /**
     * creates an XMPP connection by using the factory specified in the
     * configuration
     * 
     * @return an instance of the xmpp connection for use
     */
    public abstract IXMPPConnection createXMPPConnection();
}
