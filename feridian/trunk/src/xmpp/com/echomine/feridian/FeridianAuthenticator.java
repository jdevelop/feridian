package com.echomine.feridian;

/**
 * Represents an authenticator extension. This is used for configuration purposes.
 */
public class FeridianAuthenticator {
    private Class cls;

    /**
     * The class associated with the packet extension
     * 
     * @return Returns the cls.
     */
    public Class getAuthenticatorClass() {
        return cls;
    }
}
