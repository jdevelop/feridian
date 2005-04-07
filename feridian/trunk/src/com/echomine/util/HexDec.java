package com.echomine.util;

/** Does conversions for Hex-Dec and vice versa. */
public class HexDec {
    private static char[] hexChar;

    static {
        hexChar = new char[16];
        hexChar[0] = '0';
        hexChar[1] = '1';
        hexChar[2] = '2';
        hexChar[3] = '3';
        hexChar[4] = '4';
        hexChar[5] = '5';
        hexChar[6] = '6';
        hexChar[7] = '7';
        hexChar[8] = '8';
        hexChar[9] = '9';
        hexChar[10] = 'A';
        hexChar[11] = 'B';
        hexChar[12] = 'C';
        hexChar[13] = 'D';
        hexChar[14] = 'E';
        hexChar[15] = 'F';
    }

    public static String convertBytesToHexString(byte[] data) {
        return convertBytesToHexString(data, 0, data.length);
    }

    public static String convertBytesToHexString(byte[] data, int offset, int len) {
        StringBuffer buf = new StringBuffer(len * 2);
        convertBytesToHexString(data, offset, len, buf);
        return buf.toString();
    }

    public static void convertBytesToHexString(byte[] data, int offset, int len, StringBuffer outbuf) {
        int end = offset + len;
        for (int i = offset; i < end; i++) {
            outbuf.append(hexChar[(data[i] >> 4) & 0xF]);
            outbuf.append(hexChar[data[i] & 0xF]);
        }
    }

    public static byte[] convertHexStringToBytes(String hexStr) {
        int len = hexStr.length();
        byte[] bytes = new byte[len / 2];
        convertHexStringToBytes(hexStr, bytes, 0);
        return bytes;
    }

    public static int convertHexStringToBytes(String hexStr, byte[] bytebuf, int offset) {
        int len = hexStr.length();
        for (int i = 0; i < len; i += 2) {
            char highChar = hexStr.charAt(i);
            char lowChar = hexStr.charAt(i + 1);
            byte highNibble = 0;
            byte lowNibble = 0;
            if (highChar >= '0' && highChar <= '9') {
                highNibble = (byte)(highChar - '0');
            }
            else if (highChar >= 'A' && highChar <= 'F') {
                highNibble = (byte)(10 + highChar - 'A');
            }
            else if (highChar >= 'a' && highChar <= 'f') {
                highNibble = (byte)(10 + highChar - 'a');
            }
            else {
                throw new ArithmeticException("Invalid hexadecimal string " + highChar + " of '" + hexStr + "'");
            }
            if (lowChar >= '0' && lowChar <= '9') {
                lowNibble = (byte)(lowChar - '0');
            }
            else if (lowChar >= 'A' && lowChar <= 'F') {
                lowNibble = (byte)(10 + lowChar - 'A');
            }
            else if (lowChar >= 'a' && lowChar <= 'f') {
                lowNibble = (byte)(10 + lowChar - 'a');
            }
            else {
                throw new ArithmeticException("Invalid hexadecimal string " + lowChar + " of '" + hexStr + "'");
            }
            bytebuf[offset++] = (byte)(highNibble << 4 | lowNibble);
        }
        return offset;
    }
}
