package com.echomine.net;

import java.util.EventObject;

/** contains connection event codes */
public class ConnectionEvent extends EventObject {
    private static final long serialVersionUID = -767358705381792702L;
    public final static int CONNECTION_OPENED = 1;
    public final static int CONNECTION_CLOSED = 2;
    public final static int CONNECTION_ERRORED = 3;
    public final static int CONNECTION_STARTING = 4;
    public final static int CONNECTION_VETOED = 5;
    public final static int CONNECTION_REJECTED = 6;
    private String errormsg;
    private int status;

    public ConnectionEvent(ConnectionContext source, int status) {
        super(source);
        this.status = status;
    }

    public ConnectionEvent(ConnectionContext source, int status, String errormsg) {
        this(source, status);
        this.errormsg = errormsg;
    }

    /** @return true if event is an error (or vetoed) event, false otherwise */
    public boolean isError() {
        return (status == CONNECTION_ERRORED);
    }

    /** @return true if connection was vetoed and closed, false otherwise */
    public boolean isVetoed() {
        return (status == CONNECTION_VETOED);
    }

    /** @return true if connection was rejected and closed, false otherwise */
    public boolean isRejected() {
        return (status == CONNECTION_REJECTED);
    }

    /** @return the error message if event is an error, empty string if no error */
    public String getErrorMessage() {
        if (errormsg == null)
            return "";
        return errormsg;
    }

    /** @return the connection context associated with this event */
    public ConnectionContext getConnectionContext() {
        return (ConnectionContext) getSource();
    }

    /** @return the status of the event */
    public int getStatus() {
        return status;
    }

    /** @return the port as reported by the connection context */
    public int getPort() {
        return ((ConnectionContext) getSource()).getPort();
    }

    /** @return the hostname as reported by the connection context */
    public String getHostName() {
        return ((ConnectionContext) getSource()).getHostName();
    }
}
