package com.echomine.net;

/** thrown when connection fails */
public class ConnectionFailedException extends ConnectionException {
    public ConnectionFailedException() {
        super();
    }

    public ConnectionFailedException(String par1) {
        super(par1);
    }
}
