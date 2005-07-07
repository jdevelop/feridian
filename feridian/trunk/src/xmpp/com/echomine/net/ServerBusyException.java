package com.echomine.net;

/** exception thrown when the connection is through but the remote server gives us an error that the server is busy. */
public class ServerBusyException extends ConnectionException {
    private static final long serialVersionUID = 1192797248795991296L;

    public ServerBusyException() {
        super();
    }

    public ServerBusyException(String par1) {
        super(par1);
    }
}
