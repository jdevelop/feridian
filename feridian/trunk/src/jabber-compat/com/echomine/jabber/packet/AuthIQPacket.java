package com.echomine.jabber.packet;

/**
 * The authentication packet is created to support the non-SASL authentication
 * (or in xmpp terms, the new iq-stream feature).  When sending authentication
 * packet, only the username and resource is required.
 */
public class AuthIQPacket {
    String username;
    String password;
    String digest;
    String resource;

    /**
     * @return Returns the digest.
     */
    public String getDigest() {
        return digest;
    }

    /**
     * @param digest The digest to set.
     */
    public void setDigest(String digest) {
        this.digest = digest;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Returns the resource.
     */
    public String getResource() {
        return resource;
    }

    /**
     * @param resource The resource to set.
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * @return Returns the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

}
