package com.echomine.xmpp;

/**
 * Contains the codes for all errors.
 */
public interface ErrorCode {
    // xmpp error conditions (S_ for server jabber:stream namespace)
    // these only happen at the server level
    // if you get one of these errors, likely the server will close the
    // connection.
    
    static final String S_BAD_FORMAT = "bad-format";
    static final String S_BAD_NAMESPACE_PREFIX = "bad-namespace-prefix";
    static final String S_CONFLICT = "conflict";
    static final String S_CONNECTION_TIMEOUT = "connection-timeout";
    static final String S_HOST_GONE = "host-gone";
    static final String S_HOST_UNKNOWN = "host-unknown";
    static final String S_IMPROPER_ADDRESSING = "improper-addressing";
    static final String S_INTERNAL_SERVER_ERROR = "internal-server-error";
    static final String S_INVALID_FROM = "invalid-from";
    static final String S_INVALID_ID = "invalid-id";
    static final String S_INVALID_NAMESPACE = "invalid-namespace";
    static final String S_INVALID_XML = "invalid-xml";
    static final String S_NOT_AUTHORIZED = "not-authorized";
    static final String S_POLICY_VIOLATION = "policy-violation";
    static final String S_REMOTE_CONNECTION_FAILED = "remote-connection-failed";
    static final String S_RESOURCE_CONSTRAINT = "resource-constraint";
    static final String S_RESTRICTED_XML = "restricted-xml";
    static final String S_SEE_OTHER_HOST = "see-other-host";
    static final String S_SYSTEM_SHUTDOWN = "system-shutdown";
    static final String S_UNDEFINED = "undefined-condition";
    static final String S_UNSUPPORTED_ENCODING = "unsupported-encoding";
    static final String S_UNSUPPORTED_STANZA_TYPE = "unsupported-stanza-type";
    static final String S_UNSUPPORTED_VERSION = "unsupported-version";
    static final String S_XML_NOT_WELL_FORMED = "xml-not-well-formed";

    // xmpp stanza error conditions (C_ for jabber:client namespace)
    // these errors are used for stanza errors (not server errors).
    static final String C_BAD_REQUEST = "bad-request";
    static final String C_CONFLICT = "conflict";
    static final String C_FEATURE_NOT_IMPLEMENTED = "feature-not-implemented";
    static final String C_FORBIDDEN = "forbidden";
    static final String C_GONE = "gone";
    static final String C_INTERNAL_SERVER_ERROR = "internal-server-error";
    static final String C_ITEM_NOT_FOUND = "item-not-found";
    static final String C_MALFORMED_JID = "jid-malformed";
    static final String C_NOT_ACCEPTABLE = "not-acceptable";
    static final String C_NOT_ALLOWED = "not-allowed";
    static final String C_NOT_AUTHORIZED = "not-authorized";
    static final String C_PAYMENT_REQUIRED = "payment-required";
    static final String C_RECIPIENT_UNAVAILABLE = "recipient-unavailable";
    static final String C_REDIRECT = "redirect";
    static final String C_REGISTRATION_REQUIRED = "registration-required";
    static final String C_REMOTE_SERVER_NOT_FOUND = "remote-server-not-found";
    static final String C_REMOTE_SERVER_TIMEOUT = "remote-server-timeout";
    static final String C_RESOURCE_CONSTRAINT = "resource-constraint";
    static final String C_SERVICE_UNAVAILABLE = "service-unavailable";
    static final String C_SUBSCRIPTION_REQUIRED = "subscription-required";
    static final String C_UNDEFINED = "undefined-condition";
    static final String C_UNEXPECTED_REQUEST = "unexpected-request";
    
    //SASL errors
    static final String SASL_ABORTED = "aborted";
    static final String SASL_INCORRECT_ENCODING = "incorrect-encoding";
    static final String SASL_INVALID_AUTHZID = "invalid-authzid";
    static final String SASL_INVALID_MECHANISM = "invalid-mechanism";
    static final String SASL_NOT_AUTHORIZED = "not-authorized";
    static final String SASL_TEMP_AUTH_FAILURE = "temporary-stream-failure";
}