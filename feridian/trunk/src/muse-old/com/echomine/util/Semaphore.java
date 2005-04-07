package com.echomine.util;

/**
 * A semaphore is a old locking technique where only a specified number of threads can obtain the lock at one time.  The other
 * threads that wants to acquire the lock will wait in line until the lock is released.  Everyone takes turns obtaining the
 * lock.  The number of locks that can be acquired at one time is determined by the constructor.  This class does not
 * guarantee that a thread waiting to obtain a lock will get it, even if it is waiting for it before other threads.  There is
 * no Priority assigned to the waiting.
 */
public class Semaphore implements Sync {
    protected long permits;

    public Semaphore(long initial) {
        permits = initial;
    }

    public void acquire() throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        synchronized(this) {
            try {
                while (permits <= 0) wait();
                --permits;
            } catch (InterruptedException ex) {
                notify();
                throw ex;
            }
        }
    }

    public synchronized void release() {
        ++permits;
        notify();
    }

    public boolean attempt(long msecs) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        synchronized(this) {
            if (permits > 0) {
                --permits;
                return true;
            } else if (msecs <= 0)
                return false;
            else {
                try {
                    long startTime = System.currentTimeMillis();
                    long waitTime = msecs;
                    for ( ; ; ) {
                        wait(waitTime);
                        if (permits > 0) {
                            --permits;
                            return true;
                        } else {
                            long now = System.currentTimeMillis();
                            waitTime = msecs - (now - startTime);
                            if (waitTime <= 0)
                                return false;
                        }
                    }
                } catch (InterruptedException ex) {
                    notify();
                    throw ex;
                }
            }
        }
    }
}
