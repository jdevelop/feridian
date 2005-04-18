package com.echomine.util;

import java.net.InetAddress;

/** Contains some utility functions that is IP and host related. */
public class IPUtil {
    /**
     * Checks whether an IP is private or not.  This will check by the
     * set standards of IANA who assigns blocks of IPs that are considered
     * private use.
     */
    public static boolean isHostIPPrivate(String ip) {
        if (ip.equals("0.0.0.0")) return true;
        if (ip.equals("127.0.0.1")) return true;
        if (ip.startsWith("10.")) return true;
        if (ip.startsWith("192.168.")) return true;
        if (ip.startsWith("172.16.")) return true;
        if (ip.startsWith("172.17.")) return true;
        if (ip.startsWith("172.18.")) return true;
        if (ip.startsWith("172.19.")) return true;
        if (ip.startsWith("172.20.")) return true;
        if (ip.startsWith("172.21.")) return true;
        if (ip.startsWith("172.22.")) return true;
        if (ip.startsWith("172.23.")) return true;
        if (ip.startsWith("172.24.")) return true;
        if (ip.startsWith("172.25.")) return true;
        if (ip.startsWith("172.26.")) return true;
        if (ip.startsWith("172.27.")) return true;
        if (ip.startsWith("172.28.")) return true;
        if (ip.startsWith("172.29.")) return true;
        if (ip.startsWith("172.30.")) return true;
        if (ip.startsWith("172.31.")) return true;
        return false;
    }

    /** Converts a long representation of an IP into a string format "xx.xx.xx.xx" */
    public static String convertIP(long ip) {
        return ((ip & 0x000000FF) + "." + ((ip & 0x0000FF00) >> 8) + "." + ((ip & 0x00FF0000) >> 16) + "." +
            ((ip & 0xFF000000) >>> 24));
    }

    /**
     * Serializes the IP from a given string.  This is only IPv4 capable.
     * @param ip The IP string in the "xx.xx.xx.xx" format
     * @param offset the starting offset where the bytes will write to
     * @param outbuf the results will be appened to this buffer
     * @return the current offset after serializing
     */
    public static int serializeIP(String ip, byte[] outbuf, int offset) {
        InetAddress inet = null;
        byte[] addrBuf = null;
        try {
            inet = InetAddress.getByName(ip);
            addrBuf = inet.getAddress();
        }
        catch (Exception e) {
            addrBuf = new byte[4];
            addrBuf[0] = (byte) '\0';
            addrBuf[1] = (byte) '\0';
            addrBuf[2] = (byte) '\0';
            addrBuf[3] = (byte) '\0';
        }
        outbuf[offset++] = addrBuf[0];
        outbuf[offset++] = addrBuf[1];
        outbuf[offset++] = addrBuf[2];
        outbuf[offset++] = addrBuf[3];
        return offset;
    }

    /**
     * Deserializes the IP from a given set of bytes.  This is only IPv4 capable.
     * @param inbuf the buffer that contains the IP in bytes
     * @param offset the starting offset where deserialization occurs
     * @param outbuf the results will be appened to this buffer
     * @return the current offset after deserializing
     */
    public static int deserializeIP(byte[] inbuf, int offset, StringBuffer outbuf) {
        int digit1 = inbuf[offset];
        int digit2 = inbuf[offset + 1];
        int digit3 = inbuf[offset + 2];
        int digit4 = inbuf[offset + 3];
        if (digit1 < 0)
            digit1 += 256;
        if (digit2 < 0)
            digit2 += 256;
        if (digit3 < 0)
            digit3 += 256;
        if (digit4 < 0)
            digit4 += 256;
        outbuf.append(digit1).append(".").append(digit2).append(".").append(digit3).append(".").append(digit4);
        return offset + 4;
    }
}
