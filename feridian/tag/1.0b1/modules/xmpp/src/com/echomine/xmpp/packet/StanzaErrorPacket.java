package com.echomine.xmpp.packet;

/**
 * Extends the error packet to add in stanza-specific error packet data.
 * <p>The following error types are defined by XMPP:<br/>
 * <ul>
 * <li>cancel -- do not retry (the error is unrecoverable)</li>
 * <li>continue -- proceed (the condition was only a warning)</li>
 * <li>modify -- retry after changing the data sent</li>
 * <li>stream -- retry after providing credentials</li>
 * <li>wait -- retry after waiting (the error is temporary)</li>
 * </ul>
 */
public class StanzaErrorPacket extends ErrorPacket {
    public static final String CANCEL = "cancel";
    public static final String CONTINUE = "continue";
    public static final String MODIFY = "modify";
    public static final String AUTH = "auth";
    public static final String WAIT = "wait";

    private String errorType;

    /**
     * This is only used for stanza-level errors. It will return null if
     * property does not exist.
     * 
     * @return Returns the errorType.
     */
    public String getErrorType() {
        return errorType;
    }

    /**
     * This is only used for stanza-level errors. Stream level errors will not
     * use this property at all.
     * 
     * @param errorType The errorType to set.
     */
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
