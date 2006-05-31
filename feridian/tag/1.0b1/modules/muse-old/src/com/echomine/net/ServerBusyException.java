package com.echomine.net;

/** exception thrown when the connection is through but the remote server gives us an error that the server is busy. */
public class ServerBusyException extends ConnectionException {
    public ServerBusyException() {
        super();
    }

    public ServerBusyException(String par1) {
        super(par1);
    }
}
