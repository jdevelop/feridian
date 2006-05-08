package com.echomine.util;

import com.echomine.common.ParseException;

/** Some convenient parsing utilities */
public class ParseUtil {
    public static int serializeIntBE(int value, byte[] outbuf, int offset) {
        outbuf[offset++] = (byte) (value >> 24);
        outbuf[offset++] = (byte) (value >> 16);
        outbuf[offset++] = (byte) (value >> 8);
        outbuf[offset++] = (byte) (value);
        // Return next offset.
        return offset;
    }

    public static int serializeIntLE(int value, byte[] outbuf, int offset) {
        outbuf[offset++] = (byte) (value);
        outbuf[offset++] = (byte) (value >> 8);
        outbuf[offset++] = (byte) (value >> 16);
        outbuf[offset++] = (byte) (value >> 24);
        // Return next offset.
        return offset;
    }

    public static int deserializeIntBE(byte[] inbuf, int offset) {
        return (inbuf[offset]) << 24 | (inbuf[offset + 1] & 0xff) << 16 | (inbuf[offset + 2] & 0xff) << 8 |
                (inbuf[offset + 3] & 0xff);
    }

    public static int deserializeIntLE(byte[] inbuf, int offset) {
        return (inbuf[offset + 3]) << 24 | (inbuf[offset + 2] & 0xff) << 16 | (inbuf[offset + 1] & 0xff) << 8 |
                (inbuf[offset] & 0xff);
    }

    public static int serializeShortBE(short value, byte[] outbuf, int offset) {
        outbuf[offset++] = (byte) (value >> 8);
        outbuf[offset++] = (byte) (value);
        // Return next offset.
        return offset;
    }

    public static int serializeShortLE(short value, byte[] outbuf, int offset) {
        outbuf[offset++] = (byte) (value);
        outbuf[offset++] = (byte) (value >> 8);
        // Return next offset.
        return offset;
    }

    public static short deserializeShortBE(byte[] inbuf, int offset) {
        return (short) ((inbuf[offset] & 0xff) << 8 | (inbuf[offset + 1] & 0xff));
    }

    public static short deserializeShortLE(byte[] inbuf, int offset) {
        return (short) ((inbuf[offset + 1] & 0xff) << 8 | (inbuf[offset] & 0xff));
    }

    public static int serializeString(String str, byte[] outbuf, int offset) {
        // Strip off the hi-byte of the char.  No good.
        for (int i = 0; i < str.length(); i++) {
            outbuf[offset] = (byte) str.charAt(i);
            offset++;
        }
        return offset;
    }

    public static int deserializeString(byte[] inbuf, int offset, StringBuffer outbuf) {
        int begin = offset;
        int maxLen = inbuf.length;
        while (offset < maxLen) {
            if (inbuf[offset] == 0) {
                // Note that the terminating 0 is not added in the returning offset.
                break;
            }
            offset++;
        }
        if (offset - begin > 0)
            outbuf.append(new String(inbuf, begin, offset - begin));
        return offset;
    }

    public static int deserializeString(byte[] inbuf, int offset, int len, StringBuffer outbuf) throws ParseException {
        if (len < 0) throw new ParseException("Length must be >= 0");
        if (len > inbuf.length - offset)
            len = inbuf.length - offset;
        outbuf.append(new String(inbuf, offset, len));
        return offset + len;
    }
}
