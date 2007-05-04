package com.echomine.jabber.compat.packet;

import com.echomine.xmpp.packet.IQPacket;

/**
 * The authentication packet is created to support the non-SASL authentication
 * (or in xmpp terms, the new iq-stream feature). When sending authentication
 * packet, only the username and resource is required.
 */
public class AuthIQPacket extends IQPacket {
    String username;
    String password;
    String digest;
    String resource;

    /**
     * default auth iq packet with type "get" 
     */
    public AuthIQPacket() {
        super();
    }

    /**
     * constructs an auth IQ packet with the specified type
     * 
     * @param type
     */
    public AuthIQPacket(String type) {
        super(type);
    }

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

    /**
     * This convenience method simply checks that the digest string is empty. If
     * it is empty (not null), then we can safely assume that the remote entity
     * supports this authentication method. Note that this method is only useful
     * for working with an incoming AuthIQPacket.
     * 
     * @return true if digest is supported, false otherwise
     */
    public boolean isDigestSupported() {
        return "".equals(digest);
    }
    
    /**
     * This convenience method simply checks that the passsword string is empty. If
     * it is empty (not null), then we can safely assume that the remote entity
     * supports this authentication method. Note that this method is only useful
     * for working with an incoming AuthIQPacket.
     * 
     * @return true if plain is supported, false otherwise
     */
    public boolean isPlainSupported() {
        return "".equals(password);
    }
}
