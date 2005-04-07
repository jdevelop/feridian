package com.echomine.net;

/** thrown when a file transfer is vetoed by a listener to not to continue the file transfer. */
public class TransferVetoException extends Exception {
    public TransferVetoException() {
        super();
    }

    public TransferVetoException(String par1) {
        super(par1);
    }
}
