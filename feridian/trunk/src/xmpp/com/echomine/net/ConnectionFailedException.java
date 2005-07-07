package com.echomine.net;

/** thrown when connection fails */
public class ConnectionFailedException extends ConnectionException {
    private static final long serialVersionUID = 6685147220171608494L;

    public ConnectionFailedException() {
        super();
    }

    public ConnectionFailedException(String par1) {
        super(par1);
    }
}
