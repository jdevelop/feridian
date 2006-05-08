package com.echomine.xmpp;

/**
 * This is used to store the authentication information for use by SASL and
 * possibly other authentication mechanisms. It holds a temporary set of
 * authentication information. After security is finished, this object should
 * clear out the security information.
 */
public class XMPPAuthCallback {
    String username;
    char[] password;
    String resource;

    /**
     * Clears the stored data
     */
    public void clear() {
        username = null;
        resource = null;
        for (int i = 0; i < password.length; i++)
            password[i] = '\0';
    }

    /**
     * @return Returns the password.
     */
    public char[] getPassword() {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(char[] password) {
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
