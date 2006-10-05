package com.echomine.jabber;

import com.echomine.common.ParseException;
import org.jdom.Element;

import java.util.List;

/**
 * Contains the error messages and error codes associated with a specific error.  The naming convention may be misleading, but
 * this class does not extend JabberMessage.  It is usually used by a JabberMessage when parsing error messages.  Pretty much
 * this class is more of a helper class than a message class.
 * Furthermore, because the error message is now rather specific, including the management of the parsing and
 * encoding of xml data, the message is now tied to jdom and is not generic for use for other purposes.
 * If you have an issue with this, then you will need to create your own error message to handle the messages.
 * This message supports the transitional JEP-0086, helping bridge the gap between the old style Jabber error
 * messages to the new XMPP style Jabber Error messages.
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0086.html">JEP-0086 Version 1.0</a></b></p>
 *
 * @since 0.8b1
 */
public class ErrorMessage implements ErrorCode, JabberCode {
    public static final String TYPE_AUTH = "auth";
    public static final String TYPE_CANCEL = "cancel";
    public static final String TYPE_CONTINUE = "continue";
    public static final String TYPE_MODIFY = "modify";
    public static final String TYPE_WAIT = "wait";
    private int code;
    private String msg;
    private String condition;
    private Element applicationCondition;
    private String type;

    /**
     * This constructor is mainly used for incoming error messages where the error
     * codes and conditions will be parsed from the DOM tree
     */
    private ErrorMessage() {
    }

    /**
     * Constructs the legacy error message using the specified code and message.
     * The code will be translated as specified in the JEP.
     */
    public ErrorMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
        translateCodeToCondition(code);
    }

    public ErrorMessage(String condition, String type) {
        this(condition, type, null);
    }

    public ErrorMessage(String condition, String type, String msg) {
        this.condition = condition;
        this.type = type;
        this.msg = msg;
        translateConditionToCode(condition, type);
    }

    public static ErrorMessage createErrorMessage(Element errorElem) throws ParseException {
        ErrorMessage msg = new ErrorMessage();
        msg.parse(errorElem);
        return msg;
    }

    /**
     * @return the user-readable error message, or null if there isn't one
     */
    public String getMessage() {
        return msg;
    }

    /**
     * @return the error code
     */
    public int getCode() {
        return code;
    }

    /**
     * @return the condition for the message, or null if there isn't one.
     */
    public String getCondition() {
        return condition;
    }

    /**
     * The error type, as specified in the constants in this class.  This value
     * is only set for stanza errors, not for stream errors.
     *
     * @return the error condition type, or null if there isn't one.
     */
    public String getType() {
        return type;
    }

    /**
     * This retrieves the application-specified condition if there is one.
     * This returns the element for the application condition.  Because
     * the application condition is XML data, JDOM is explicitly tied to
     * the use of application conditions.
     */
    public Element getApplicationCondition() {
        return applicationCondition;
    }

    /**
     * Sets the application condition to be sent along with the error message.
     * The application condition will have a namespace attached.
     */
    public void setApplicationCondition(Element applicationCondition) {
        this.applicationCondition = applicationCondition;
    }

    /**
     * Set the condition to be used.  The condition should be from the list of
     * stanza error constants listed in ErrorCode
     *
     * @see ErrorCode
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * Sets the type to be associated with the condition.  The type should be
     * from the list of constants that are in this class.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the error code for this error message.
     */
    public void setCode(int code) {
        this.code = code;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(100);
        if (code != 0)
            buf.append(code).append(" ");
        buf.append(msg);
        return buf.toString();
    }

    /**
     * Parses the error element for the required elements. It is compatible with both the
     * old and the new legacy style of error messages.
     */
    private void parse(Element errorElem) throws ParseException {
        try {
            String attr = errorElem.getAttributeValue("code");
            if (attr != null) code = Integer.parseInt(attr);
            attr = errorElem.getAttributeValue("type");
            if (code > 0) msg = errorElem.getText();
            //check if this is an XMPP style message
            if (attr == null) {
                translateCodeToCondition(code);
                return;
            }
            type = attr;
            //obtain the condition, text, and other info
            List children = errorElem.getChildren();
            int size = children.size();
            Element elem;
            for (int i = 0; i < size; i++) {
                elem = (Element) children.get(i);
                if ((XMLNS_ERROR_STANZA.equals(elem.getNamespace()) ||
                        XMLNS_ERROR_STREAM.equals(elem.getNamespace())) && !"text".equals(elem.getName()))
                    condition = elem.getName();
                else if ((XMLNS_ERROR_STANZA.equals(elem.getNamespace()) ||
                        XMLNS_ERROR_STREAM.equals(elem.getNamespace())) && "text".equals(elem.getName()))
                    msg = elem.getText();
                else
                    applicationCondition = elem;
            }
            if (code == 0) translateConditionToCode(condition, type);
        } catch (NumberFormatException ex) {
            throw new ParseException("The error code is not a valid code integer");
        }
    }

    /**
     * Takes the code and sets the condition/type as specified in the JEP.  This is useful
     * for compatibility with legacy jabber clients/servers or XMPP clients/servers.
     * The conditions are set to work with stanza errors.  Since Muse is a client API,
     * stream errors should not be created by Muse.
     * If a code does not have a condition to match, then condition will be set to UNDEFINED
     * and type will be set to CANCEL.
     */
    private void translateCodeToCondition(int code) {
        switch (code) {
            case REDIRECT:
                condition = C_REDIRECT;
                type = TYPE_MODIFY;
                break;
            case BAD_REQUEST:
                condition = C_BAD_REQUEST;
                type = TYPE_MODIFY;
                break;
            case UNAUTHORIZED:
                condition = C_NOT_AUTHORIZED;
                type = TYPE_AUTH;
                break;
            case PAYMENT_REQUIRED:
                condition = C_PAYMENT_REQUIRED;
                type = TYPE_AUTH;
                break;
            case FORBIDDEN:
                condition = C_FORBIDDEN;
                type = TYPE_AUTH;
                break;
            case NOT_FOUND:
                condition = C_ITEM_NOT_FOUND;
                type = TYPE_CANCEL;
                break;
            case NOT_ALLOWED:
                condition = C_NOT_ALLOWED;
                type = TYPE_CANCEL;
                break;
            case NOT_ACCEPTABLE:
                condition = C_NOT_ACCEPTABLE;
                type = TYPE_CANCEL;
                break;
            case REGISTRATION_REQUIRED:
                condition = C_REGISTRATION_REQUIRED;
                type = TYPE_AUTH;
                break;
            case REQUEST_TIMEOUT:
            case REMOTE_SERVER_TIMEOUT:
                condition = C_REMOTE_SERVER_TIMEOUT;
                type = TYPE_WAIT;
                break;
            case CONFLICT:
                condition = C_CONFLICT;
                type = TYPE_CANCEL;
                break;
            case INTERNAL_SERVER_ERROR:
                condition = C_INTERNAL_SERVER_ERROR;
                type = TYPE_WAIT;
                break;
            case NOT_IMPLEMENTED:
                condition = C_FEATURE_NOT_IMPLEMENTED;
                type = TYPE_CANCEL;
                break;
            case REMOTE_SERVER_ERROR:
                condition = C_SERVICE_UNAVAILABLE;
                type = TYPE_WAIT;
                break;
            case SERVICE_UNAVAILABLE:
            case DISCONNECTED:
                condition = C_SERVICE_UNAVAILABLE;
                type = TYPE_CANCEL;
                break;
            default:
                condition = C_UNDEFINED;
                type = TYPE_CANCEL;
        }
    }

    /**
     * Takes the condition/type and sets the code as specified in the JEP.  This is useful
     * for compatibility with legacy jabber clients/servers or XMPP clients/servers.
     * If the condition does not have a code to match, then code will stay as 0.
     */
    private void translateConditionToCode(String condition, String type) {
        if (C_BAD_REQUEST.equals(condition) && TYPE_MODIFY.equals(type))
            code = BAD_REQUEST;
        else if (C_CONFLICT.equals(condition) && TYPE_CANCEL.equals(type))
            code = CONFLICT;
        else if (C_FEATURE_NOT_IMPLEMENTED.equals(condition) && TYPE_CANCEL.equals(type))
            code = NOT_IMPLEMENTED;
        else if (C_FORBIDDEN.equals(condition) && TYPE_AUTH.equals(type))
            code = FORBIDDEN;
        else if (C_GONE.equals(condition) && TYPE_MODIFY.equals(type))
            code = REDIRECT;
        else if (C_INTERNAL_SERVER_ERROR.equals(condition) && TYPE_WAIT.equals(type))
            code = INTERNAL_SERVER_ERROR;
        else if (C_ITEM_NOT_FOUND.equals(condition) && TYPE_CANCEL.equals(type))
            code = NOT_FOUND;
        else if (C_MALFORMED_JID.equals(condition) && TYPE_MODIFY.equals(type))
            code = BAD_REQUEST;
        else if (C_NOT_ACCEPTABLE.equals(condition) && TYPE_MODIFY.equals(type))
            code = NOT_ACCEPTABLE;
        else if (C_NOT_ALLOWED.equals(condition) && TYPE_CANCEL.equals(type))
            code = NOT_ALLOWED;
        else if (C_NOT_AUTHORIZED.equals(condition) && TYPE_AUTH.equals(type))
            code = UNAUTHORIZED;
        else if (C_PAYMENT_REQUIRED.equals(condition) && TYPE_AUTH.equals(type))
            code = PAYMENT_REQUIRED;
        else if (C_RECIPIENT_UNAVAILABLE.equals(condition) && TYPE_WAIT.equals(type))
            code = NOT_FOUND;
        else if (C_REDIRECT.equals(condition) && TYPE_MODIFY.equals(type))
            code = REDIRECT;
        else if (C_REGISTRATION_REQUIRED.equals(condition) && TYPE_AUTH.equals(type))
            code = REGISTRATION_REQUIRED;
        else if (C_REMOTE_SERVER_NOT_FOUND.equals(condition) && TYPE_CANCEL.equals(type))
            code = NOT_FOUND;
        else if (C_REMOTE_SERVER_TIMEOUT.equals(condition) && TYPE_WAIT.equals(type))
            code = REMOTE_SERVER_TIMEOUT;
        else if (C_RESOURCE_CONSTRAINT.equals(condition) && TYPE_WAIT.equals(type))
            code = INTERNAL_SERVER_ERROR;
        else if (C_SERVICE_UNAVAILABLE.equals(condition) && TYPE_CANCEL.equals(type))
            code = SERVICE_UNAVAILABLE;
        else if (C_SUBSCRIPTION_REQUIRED.equals(condition) && TYPE_AUTH.equals(type))
            code = REGISTRATION_REQUIRED;
        else if (C_UNDEFINED.equals(condition))
            code = INTERNAL_SERVER_ERROR;
        else if (C_UNEXPECTED_REQUEST.equals(condition) && TYPE_WAIT.equals(type))
            code = BAD_REQUEST;
    }
}
