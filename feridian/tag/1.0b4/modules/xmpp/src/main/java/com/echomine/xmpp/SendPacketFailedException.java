package com.echomine.xmpp;

/**
 * Exception to indicate that the packet to be sent failed. The user should try
 * to resend the packet again.
 */
public class SendPacketFailedException extends XMPPException {
    private static final long serialVersionUID = -8553039202005027771L;

    public SendPacketFailedException() {
        super();

    }

    public SendPacketFailedException(String message) {
        super(message);

    }

    public SendPacketFailedException(String message, Throwable cause) {
        super(message, cause);

    }

    public SendPacketFailedException(Throwable cause) {
        super(cause);

    }
}
