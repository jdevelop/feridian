package com.echomine.jabber.msg;

import com.echomine.jabber.JabberJDOMMessage;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.regex.Pattern;

/**
 * This abstract class provides shared functions for the PGP encrypted and signed messages.
 * It contains methods to attach and strip PGP headers.
 */
abstract class AbstractPGPXMessage extends JabberJDOMMessage {
    private String pgpStartHeader;
    private String pgpEndHeader;
    private Pattern pattern;

    /**
     * constructs a default message
     *
     * @throws java.util.regex.PatternSyntaxException
     *          if regex is invalid
     */
    public AbstractPGPXMessage(Namespace ns, String pgpStartHeader, String pgpEndHeader) {
        super(new Element("x", ns));
        this.pgpStartHeader = pgpStartHeader;
        this.pgpEndHeader = pgpEndHeader;
        // This pattern matches the OpenPGP header as defined in RFC2240 - in (6)Radix-64 Conversions.
        String p = "^.*" + pgpStartHeader + ".*?\n\\s*\n(.*)" + pgpEndHeader + ".*$";
        pattern = Pattern.compile(p, Pattern.DOTALL);
    }

    /**
     * sets the PGP specified data
     */
    public void setPGPMessage(String data) {
        getDOM().setText(stripPGPHeaders(data));
    }

    /**
     * retrieves the PGP data from the  message
     *
     * @return the PGP data or null if there is none.
     */
    public String getPGPMessage() {
        return addPGPHeaders(getDOM().getText());
    }

    /**
     * Strips the pgp headers off the data passed in
     */
    private String stripPGPHeaders(String data) {
        return pattern.matcher(data).replaceFirst("$1");
    }

    /**
     * Adds the pgp header back into the data
     */
    private String addPGPHeaders(String data) {
        return pgpStartHeader + "\n\n" + data + pgpEndHeader + "\n";
    }
}