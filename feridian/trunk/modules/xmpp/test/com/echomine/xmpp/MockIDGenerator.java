package com.echomine.xmpp;

/**
 * This is an useful id generator when running unit tests. It will always output
 * the same ID "test_001" always so that testing can be consistent.
 */
public class MockIDGenerator extends IDGenerator {
    private static final String ID = "test_001";

    protected String generateID() {
        return ID;
    }
}
