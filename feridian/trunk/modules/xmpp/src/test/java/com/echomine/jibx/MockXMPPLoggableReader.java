package com.echomine.jibx;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * The mock reader will simply not log.
 */
public class MockXMPPLoggableReader extends XMPPLoggableReader {

    public MockXMPPLoggableReader() {
        super(new ByteArrayInputStream("".getBytes()));
    }

    public MockXMPPLoggableReader(InputStream in, String charsetName) throws UnsupportedEncodingException {
        super(in, charsetName);
    }

    public MockXMPPLoggableReader(InputStream in) {
        super(in);
    }

    public void flushIgnoredDataToLog() {
    }

    public void flushLog() {
    }

    public void startLogging() {
    }

    public void stopLogging() {
    }
}
