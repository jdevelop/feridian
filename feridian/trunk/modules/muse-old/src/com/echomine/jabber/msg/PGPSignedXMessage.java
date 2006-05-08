package com.echomine.jabber.msg;

import com.echomine.jabber.JabberCode;

/**
 * <p>Support PGP signed messages.  This class will work with PGP-signed message.  It's really simple
 * because it simply contains only the encrypted data.  It will not do any encryption for you.
 * That is up to you to implement on the client level since developers use
 * different Encryption packages to encrypt data.
 * <p>Signed messages are normally used for <presence/> messages.
 * The signing should use Status string of the Presence message, and signed
 * using the private key of the sender.</p>
 * <p/>
 * <p>There are actually three types of PGP-signed messages and this only handles two types:
 * <b>detached</b> and <b>clearsign</b>. If you have a copy of <a href="http://www.gnupg.org">Gnupg</a>
 * you can create a detached PGP message with the command <code>gpg -ab filename</code>.
 * A detched PGP message begins with a '-----BEGIN PGP SIGNATURE-----' header and ends with
 * '-----END PGP SIGNATURE-----'. Using Gnupg you can create a clearsign PGP message with the
 * command <code>gpg -clearsign filename</code>. Clearsign messages contain the same headers
 * as a detached message but the also contain a leading '-----BEGIN PGP SIGNED MESSAGE-----'
 * and a plaintext copy of the data that has been signed.</p>
 * <p/>
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0027.html">JEP-0027 Version 1.2</a></b></p>
 */
public class PGPSignedXMessage extends AbstractPGPXMessage {
    // clearsign messages are handled properly because all information leading up to PGP_HEADER is
    // also stripped off in AbstractPGPXMessage. This has the effect of leaving you with a detached message.
    private static final String PGP_HEADER = "-----BEGIN PGP SIGNATURE-----";
    private static final String PGP_FOOTER = "-----END PGP SIGNATURE-----";

    /**
     * constructs a default message
     */
    public PGPSignedXMessage() {
        super(JabberCode.XMLNS_X_PGP_SIGNED, PGP_HEADER, PGP_FOOTER);
    }

    public int getMessageType() {
        return JabberCode.MSG_X_PGP_SIGNED;
    }
}
