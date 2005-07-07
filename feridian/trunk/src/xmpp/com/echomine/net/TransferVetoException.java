package com.echomine.net;

/** thrown when a file transfer is vetoed by a listener to not to continue the file transfer. */
public class TransferVetoException extends Exception {
    private static final long serialVersionUID = 8359883043662819665L;

    public TransferVetoException() {
        super();
    }

    public TransferVetoException(String par1) {
        super(par1);
    }
}
