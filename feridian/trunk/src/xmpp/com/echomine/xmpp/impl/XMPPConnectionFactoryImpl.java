package com.echomine.xmpp.impl;

import com.echomine.xmpp.IXMPPConnection;
import com.echomine.xmpp.XMPPConnectionFactory;

/**
 * The main connection factory implementation for the API. It will create a
 * instances ofof XMPPConnectionImpl objects.
 * 
 * @see com.echomine.xmpp.impl.XMPPConnectionImpl
 * @see com.echomine.xmpp.impl.XMPPConnectionHandler
 */
public class XMPPConnectionFactoryImpl extends XMPPConnectionFactory {
    public IXMPPConnection createXMPPConnection() {
        XMPPConnectionHandler handler = new XMPPConnectionHandler();
        return new XMPPConnectionImpl(handler);
    }
}
