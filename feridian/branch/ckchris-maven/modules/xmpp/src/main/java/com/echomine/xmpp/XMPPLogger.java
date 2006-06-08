package com.echomine.xmpp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The logger that currently only logs incoming and outgoing packets under
 * specific logging spaces. These logging spaces can then be enabled in logging
 * configuration files (ie. log4j or java logging).
 */
public class XMPPLogger {
    private static final Log inlog = LogFactory.getLog("com.echomine.feridian.packet.incoming");
    private static final Log ignorelog = LogFactory.getLog("com.echomine.feridian.packet.ignored");
    private static final Log outlog = LogFactory.getLog("com.echomine.feridian.packet.outgoing");

    /**
     * logs data to the ignored data logger
     */
    public static void logIgnored(String data) {
        if (data.trim().length() == 0)
            return;
        ignorelog.debug(data);
    }

    /**
     * Logs data to the incoming data logger
     */
    public static void logIncoming(String data) {
        if (data.trim().length() == 0)
            return;
        inlog.debug(data);
    }

    /**
     * logs data to the outgoing data logger
     */
    public static void logOutgoing(String data) {
        if (data.trim().length() == 0)
            return;
        outlog.debug(data);
    }

    /**
     * Whether logging is enabled for the ignored data
     */
    public static boolean canLogIgnored() {
        return ignorelog.isDebugEnabled();
    }

    /**
     * Whether logging is enabled for the incoming data
     */
    public static boolean canLogIncoming() {
        return inlog.isDebugEnabled();
    }

    /**
     * Whether logging is enabled for the outgoing data
     */
    public static boolean canLogOutgoing() {
        return outlog.isDebugEnabled();
    }
}
