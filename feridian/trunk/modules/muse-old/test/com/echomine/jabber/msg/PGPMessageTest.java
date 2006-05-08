package com.echomine.jabber.msg;

import com.echomine.jabber.DefaultMessageParser;
import com.echomine.jabber.JabberCode;
import junit.framework.TestCase;

/**
 * this test case will test the pgp-related message classes.
 */
public class PGPMessageTest extends TestCase {
    DefaultMessageParser parser = new DefaultMessageParser();
    private final String CONTENT = "XIMAGINARYX\nXPGPXXXXXXX\nXCONTENTXXX\n";
    private final String VERSION_HEADER = "Version: GnuPG v1.2.1 (GNU/Linux)\n";
    private String encMessage;
    private String expectedEncResult;
    private String sigMessage;
    private String expectedSigResult;

    /**
     * Tests whether incoming PGPSignedXMessages have a registered parser.
     */
    public void testParserSupportsPGPSignedXMessages() {
        assertTrue(parser.supportsParsingFor("x", JabberCode.XMLNS_X_DELAY));
    }

    /**
     * Tests whether incoming PGPEncryptedXMessages have a registered parser.
     */
    public void testParserSupportsPGPEncryptedXMessages() {
        assertTrue(parser.supportsParsingFor("x", JabberCode.XMLNS_X_DELAY));
    }

    public void setUp() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("-----BEGIN PGP MESSAGE-----\n");
        sbuf.append(VERSION_HEADER);
        sbuf.append("\n");
        sbuf.append(CONTENT);
        sbuf.append("-----END PGP MESSAGE-----\n");
        encMessage = sbuf.toString();
        int x = encMessage.indexOf(VERSION_HEADER);
        sbuf.delete(x, x + VERSION_HEADER.length());
        expectedEncResult = sbuf.toString();

        sbuf = new StringBuffer();
        sbuf.append("-----BEGIN PGP SIGNATURE-----\n");
        sbuf.append(VERSION_HEADER);
        sbuf.append("\n");
        sbuf.append(CONTENT);
        sbuf.append("-----END PGP SIGNATURE-----\n");
        sigMessage = sbuf.toString();
        x = sigMessage.indexOf(VERSION_HEADER);
        sbuf.delete(x, x + VERSION_HEADER.length());
        expectedSigResult = sbuf.toString();
    }

    /**
     * Tests if PGP headers are stripped and replaced properly on PGP
     * encrypted messages that contains the correct headers.
     */
    public void testPGPEncryptedMessageWithHeaders() {
        PGPEncryptedXMessage pgpMessage = new PGPEncryptedXMessage();
        pgpMessage.setPGPMessage(encMessage);
        assertEquals(expectedEncResult, pgpMessage.getPGPMessage());
    }

    /**
     * setPGPMessage is written to also handle PGP encrypted messages
     * that do not have PGP headers. This tests that this data is
     * handled properly.
     */
    public void testPGPEncryptedMessageNoHeaders() {
        PGPEncryptedXMessage pgpMessage = new PGPEncryptedXMessage();
        pgpMessage.setPGPMessage(CONTENT);
        assertEquals(expectedEncResult, pgpMessage.getPGPMessage());
    }

    /**
     * This tests that additional data surrounding the PGP message is
     * also stripped out. If you need to handle emails containing a
     * PGP encrypted message this might be useful. Remember though
     * that only the first such message encountered is handled.
     */
    public void testPGPEncryptedMessageEmbedded() {
        PGPEncryptedXMessage pgpMessage = new PGPEncryptedXMessage();
        pgpMessage.setPGPMessage("line1\n randomw text\n" + encMessage + "\nExcess\n\n");
        assertEquals(expectedEncResult, pgpMessage.getPGPMessage());
    }

    /**
     * Tests if PGP headers are stripped and replaced properly on PGP
     * signnatures that contains the correct headers.
     */
    public void testPGPSignedMessageWithHeaders() {
        PGPSignedXMessage pgpMessage = new PGPSignedXMessage();
        pgpMessage.setPGPMessage(sigMessage);
        assertEquals(expectedSigResult, pgpMessage.getPGPMessage());
    }

    /**
     * setPGPMessage is written to also handle PGP signatures that does not
     * have PGP headers. This tests that this data is handled properly.
     */
    public void testPGPSignedMessageNoHeaders() {
        PGPSignedXMessage pgpMessage = new PGPSignedXMessage();
        pgpMessage.setPGPMessage(CONTENT);
        assertEquals(expectedSigResult, pgpMessage.getPGPMessage());
    }

    /**
     * This tests that additional data surrounding the PGP message is
     * also stripped out. This actually tests if clearsigned PGP
     * messages are handled properly since clearsign messages are the
     * same as detached messages with the addition of a lead-in section.
     * <p/>
     * If you need to handle emails containing a PGP signature this might
     * be useful. Remember though that only the first such message
     * encountered is handled.
     */
    public void testPGPSignedMessageEmbedded() {
        PGPSignedXMessage pgpMessage = new PGPSignedXMessage();
        pgpMessage.setPGPMessage("line1\n randomw text\n" + sigMessage + "\nExcess\n\n");
        assertEquals(expectedSigResult, pgpMessage.getPGPMessage());
    }
}
