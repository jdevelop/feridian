package com.echomine.jibx;

import java.io.ByteArrayInputStream;

/**
 * The mock reader will simply not log.
 */
public class MockXMPPLoggableReader extends XMPPLoggableReader {

    public MockXMPPLoggableReader() {
        super(new ByteArrayInputStream("".getBytes()));
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
