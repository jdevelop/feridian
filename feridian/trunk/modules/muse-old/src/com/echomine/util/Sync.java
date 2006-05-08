package com.echomine.util;

/** Base class for the thread lock mechanisms. */
public interface Sync {
    void acquire() throws InterruptedException;

    void release();

    boolean attempt(long msec) throws InterruptedException;
}
