package com.echomine.feridian;

/**
 * The configuration exception is a runtime exceptino that indicates a problem
 * when reading configuration files.  It is purposely a runtime exception since
 * these configuration should not have to be caught but should be thrown to indicate
 * to the developer problems so that errors can be fixed.
 */
public class ConfigurationException extends RuntimeException {
    private static final long serialVersionUID = -1981084294902261613L;

    public ConfigurationException() {
        super();

    }

    public ConfigurationException(String message) {
        super(message);

    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);

    }

    public ConfigurationException(Throwable cause) {
        super(cause);

    }

}
