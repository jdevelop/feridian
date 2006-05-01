package com.echomine.jabber.packet;

import com.echomine.xmpp.packet.IQPacket;

/**
 * Submits and parses a Client Version message. The message will return the software (and its version) of the recipient
 * that you sent the message to. It will give you information such as the software client being used, the version, and
 * the OS the client is running on. This message seems to only work with the server and not when you send it to a user
 * (somehow not supported). Thus, current implementation will not allow you to create a message that contains your own
 * time information to send to the server. When such feature is supported, the message will implement it.
 * <p/>
 * <b>See <a href="http://www.jabber.org/jeps/jep-0092.html">JEP-0092</a></b> </p>
 */
public class VersionIQPacket extends IQPacket {
    private String name;
    private String version;
    private String os;

    /**
     * @return Returns the client software name, ie. Feridian.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the OS.
     */
    public String getOS() {
        return os;
    }

    /**
     * @param os The os to set.
     */
    public void setOS(String os) {
        this.os = os;
    }

    /**
     * @return Returns the version of the software.
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version The version to set.
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
