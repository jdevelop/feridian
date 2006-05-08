package com.echomine.jabber;

/**
 * Contains the codes for all errors.
 */
public interface ErrorCode {
    //Jabber error codes
    public static final int REDIRECT = 302;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int PAYMENT_REQUIRED = 402;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int NOT_ALLOWED = 405;
    public static final int NOT_ACCEPTABLE = 406;
    public static final int REGISTRATION_REQUIRED = 407;
    public static final int REQUEST_TIMEOUT = 408;
    public static final int USERNAME_NOT_AVAILABLE = 409;
    public static final int CONFLICT = 409;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int NOT_IMPLEMENTED = 501;
    public static final int REMOTE_SERVER_ERROR = 502;
    public static final int SERVICE_UNAVAILABLE = 503;
    public static final int REMOTE_SERVER_TIMEOUT = 504;
    public static final int DISCONNECTED = 510;

    //xmpp error conditions (S_ for server jabber:stream namespace)
    //these only happen at the server level
    //if you get one of these errors, likely the server will close the connection.
    public static final String S_BAD_FORMAT = "bad-format";
    public static final String S_BAD_NAMESPACE_PREFIX = "bad-namespace-prefix";
    public static final String S_CONFLICT = "conflict";
    public static final String S_CONNECTION_TIMEOUT = "connection-timeout";
    public static final String S_HOST_GONE = "host-gone";
    public static final String S_HOST_UNKNOWN = "host-unknown";
    public static final String S_IMPROPER_ADDRESSING = "improper-addressing";
    public static final String S_INTERNAL_SERVER_ERROR = "internal-server-error";
    public static final String S_INVALID_FROM = "invalid-from";
    public static final String S_INVALID_ID = "invalid-id";
    public static final String S_INVALID_NAMESPACE = "invalid-namespace";
    public static final String S_INVALID_XML = "invalid-xml";
    public static final String S_NOT_AUTHORIZED = "not-authorized";
    public static final String S_POLICY_VIOLATION = "policy-violation";
    public static final String S_REMOTE_CONNECTION_FAILED = "remote-connection-failed";
    public static final String S_RESOURCE_CONSTRAINT = "resource-constraint";
    public static final String S_RESTRICTED_XML = "restricted-xml";
    public static final String S_SEE_OTHER_HOST = "see-other-host";
    public static final String S_SYSTEM_SHUTDOWN = "system-shutdown";
    public static final String S_UNDEFINED = "undefined-condition";
    public static final String S_UNSUPPORTED_ENCODING = "unsupported-encoding";
    public static final String S_UNSUPPORTED_STANZA_TYPE = "unsupported-stanza-type";
    public static final String S_UNSUPPORTED_VERSION = "unsupported-version";
    public static final String S_XML_NOT_WELL_FORMED = "xml-not-well-formed";

    //xmpp stanza error conditions (C_ for jabber:client namespace)
    //these errors are used for stanza errors (not server errors).
    public static final String C_BAD_REQUEST = "bad-request";
    public static final String C_CONFLICT = "conflict";
    public static final String C_FEATURE_NOT_IMPLEMENTED = "feature-not-implemented";
    public static final String C_FORBIDDEN = "forbidden";
    public static final String C_GONE = "gone";
    public static final String C_INTERNAL_SERVER_ERROR = "internal-server-error";
    public static final String C_ITEM_NOT_FOUND = "item-not-found";
    public static final String C_MALFORMED_JID = "jid-malformed";
    public static final String C_NOT_ACCEPTABLE = "not-acceptable";
    public static final String C_NOT_ALLOWED = "not-allowed";
    public static final String C_NOT_AUTHORIZED = "not-authorized";
    public static final String C_PAYMENT_REQUIRED = "payment-required";
    public static final String C_RECIPIENT_UNAVAILABLE = "recipient-unavailable";
    public static final String C_REDIRECT = "redirect";
    public static final String C_REGISTRATION_REQUIRED = "registration-required";
    public static final String C_REMOTE_SERVER_NOT_FOUND = "remote-server-not-found";
    public static final String C_REMOTE_SERVER_TIMEOUT = "remote-server-timeout";
    public static final String C_RESOURCE_CONSTRAINT = "resource-constraint";
    public static final String C_SERVICE_UNAVAILABLE = "service-unavailable";
    public static final String C_SUBSCRIPTION_REQUIRED = "subscription-required";
    public static final String C_UNDEFINED = "undefined-condition";
    public static final String C_UNEXPECTED_REQUEST = "unexpected-request";
}