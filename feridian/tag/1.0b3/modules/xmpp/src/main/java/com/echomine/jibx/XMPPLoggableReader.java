package com.echomine.jibx;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.echomine.xmpp.XMPPLogger;

/**
 * This reader is the main reader wrapper for socket input streams.  It enables logging
 * for incoming data.
 */
public class XMPPLoggableReader extends InputStreamReader {
    private static final int STOP = 0;
    private static final int START = 1;
    private int status = STOP;
    private CharArrayWriter writer = new CharArrayWriter(1024);

    public XMPPLoggableReader(InputStream in, Charset cs) {
        super(in, cs);
    }

    public XMPPLoggableReader(InputStream in, CharsetDecoder dec) {
        super(in, dec);
    }

    public XMPPLoggableReader(InputStream in, String charsetName) throws UnsupportedEncodingException {
        super(in, charsetName);
    }

    public XMPPLoggableReader(InputStream in) {
        super(in);
    }

    /**
     * This will begin the process of logging any incoming data.
     */
    public void startLogging() {
        synchronized (writer) {
            if (status == START)
                return;
            status = START;
            writer.reset();
        }
    }

    /**
     * Stops logging and immediately output the current data
     * 
     */
    public void stopLogging() {
        synchronized (writer) {
            if (status == STOP)
                return;
            status = STOP;
            if (XMPPLogger.canLogIncoming())
                XMPPLogger.logIncoming(writer.toString());
        }
    }

    /**
     * This will flush the log to a separate log where ignored data are
     * outputted.
     */
    public void flushIgnoredDataToLog() {
        if (XMPPLogger.canLogIgnored() && status == START) {
            XMPPLogger.logIgnored(writer.toString());
            writer.reset();
        }
    }

    /**
     * Flush the current data in the log if logging is enabled and status is set
     * to START. The status will not change after flushing.
     */
    public void flushLog() {
        if (XMPPLogger.canLogIncoming() && status == START) {
            XMPPLogger.logIncoming(writer.toString());
            writer.reset();
        }
    }

    /**
     * Overridden to enable logging
     */
    public int read() throws IOException {
        int ch = super.read();
        if (XMPPLogger.canLogIncoming() && status == START && ch != -1)
            writer.write(ch);
        return ch;
    }

    /**
     * Overridden to enable logging
     */
    public int read(char[] cbuf, int offset, int length) throws IOException {
        int read = super.read(cbuf, offset, length);
        if (XMPPLogger.canLogIncoming() && status == START && read > 0)
            writer.write(cbuf, offset, read);
        return read;
    }

    /**
     * Overridden to enable logging
     */
    public void close() throws IOException {
        stopLogging();
        super.close();
    }

    /**
     * This gets the current text data that is in the log buffer.
     * 
     * @return the log text, can possibly be empty
     */
    public String getLogText() {
        return writer.toString();
    }

}
