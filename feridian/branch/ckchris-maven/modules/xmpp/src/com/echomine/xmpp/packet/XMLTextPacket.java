package com.echomine.xmpp.packet;


/**
 * This packet is a generic text packet that will send out whatever text data is
 * stored directly out to the writer, without escaping any characters. It is
 * specifically used to send out unsupported stanza packets. Care must be taken
 * that whatever text that gets sent out may likely have a reply packet that is
 * unrecognized by the API and will subsequently be ignored. This is only useful
 * for purposes of debugging or sending arbitrary data. This packet is not
 * registered with the extensions configuration and cannot be used to unmarshall
 * any incoming data.
 */
public class XMLTextPacket extends StanzaPacketBase {
    String text;

    /**
     * sets the text (xml) to send to remote
     * 
     * @param text the xml text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the xml text data
     */
    public String getText() {
        return text;
    }
}
