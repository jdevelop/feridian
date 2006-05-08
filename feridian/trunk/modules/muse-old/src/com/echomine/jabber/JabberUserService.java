package com.echomine.jabber;

import com.echomine.common.SendMessageFailedException;
import com.echomine.jabber.msg.AuthIQMessage;
import com.echomine.jabber.msg.GatewayIQMessage;
import com.echomine.jabber.msg.RegisterIQMessage;
import com.echomine.jabber.msg.VacationIQMessage;

import java.util.Calendar;
import java.util.HashMap;

/**
 * <p>The user service contains all the methods needed to interact with user related services such as authentication and
 * registration.</p> <p>For authentication, there are several different ways to secure the authentication.  Cleartext password
 * and digest are the two core features (although it appears there is a new method called zero-knowledge, known as 0k).  The
 * User Service physically queries the server to see what kinds of secure authentication methods are supported, and will
 * automatically choose the most secure way to authenticate.  This means, it will choose by priority, zero-knowledge, digest,
 * and password, in that order.</p>
 */
public class JabberUserService {
    private JabberSession session;

    public JabberUserService(JabberSession session) {
        this.session = session;
    }

    /**
     * logs in to the server using the info provided in JabberContext. This is a synchronous method;
     * the method will not return until authentication succeeds or fails.  If the authentication succeeds,
     * everything will proceed as normal.  If authentication fails, an exception with the error will be thrown.
     * This method contains logic to login using the most secure authentication method provided by the server.
     * You have no need to do that yourself.
     * Login will currently also reset the zero-knowledge sequence when it reaches below 3 (not 0).
     * The new reset sequence will default to 500.
     * @throws JabberMessageException when user authentication fails
     * @throws SendMessageFailedException when message can't be sent
     * @return the authentication type used. The codes are listed in AuthIQMessage
     */
    public int login() throws JabberMessageException, SendMessageFailedException {
        JabberContext context = session.getContext();
        AuthIQMessage msg = AuthIQMessage.createGetAuthMethodsMessage(context);
        //send the message and wait for reply
        session.sendMessage(msg);
        if (msg.isError())
            throw new JabberMessageException(msg.getErrorMessage());
        //the auth message received a reply
        //get the most secure method for authentication
        int authType = msg.getMostSecureAuth();
        //depending on the auth type, hash the password differently
        String zerokToken = msg.getZerokToken();
        int zerokSeq = msg.getZerokSeq();
        msg = AuthIQMessage.createLoginMessage(context, authType, zerokToken, zerokSeq);
        session.sendMessage(msg);
        //reply received, check if authentication results
        if (msg.isError())
            throw new JabberMessageException(msg.getErrorMessage());
        //authentication success, check 0k sequence
        if (zerokToken != null && zerokSeq <= 3)
            changePassword(context.getPassword());
        //return the type of authentication used to authenticate
        return authType;
    }

    /**
     * <p>Registers with a service. It's used to create a new account OR update a current account.  This method is synchronous
     * and will not return until the server sends a reply.  If register is successful, the method will return normally,
     * but an exception will be thrown if error in registration occurs.  This method is set to
     * accept default information.  It simply obtains a hash table of name/value pairs to add to the message.
     * If you need more advanced control on what kind of data to pass in, you should create the register
     * message and send the message yourself without going through this method. An exception is thrown
     * if there is an error.  Otherwise, everything went successfully.</p>
     * <p>Normally when updating data information, you will need to first request the current info first (by
     * retrieving the register fields).  In that request, there will be a "key" field that you need in order
     * to modify any information.  For any modification, you need to include that key in the HashMap that you submit.</p>
     * @param serviceJID the service name to register with (ie. jabber.org, aim.jabber.org, msn.jabber.org)
     * @param fields a set of name/value pairs that will be submitted to the server (ie. username, password, email, etc)
     * @throws JabberMessageException when registration fails. The error message will indicate the reason.
     * @throws SendMessageFailedException when message can't be sent
     */
    public void register(JID serviceJID, HashMap fields) throws JabberMessageException, SendMessageFailedException {
        RegisterIQMessage msg = new RegisterIQMessage(JabberIQMessage.TYPE_SET);
        msg.setTo(serviceJID);
        //synchronize the message
        msg.setSynchronized(true);
        msg.addFields(fields);
        session.sendMessage(msg);
        if (msg.isError())
            throw new JabberMessageException(msg.getErrorMessage());
    }

    /**
     * changes the password.  This method is synchronous and will not return until a reply is received.
     * If there is an error, then the password is not changed, and an exception is thrown.  Otherwise,
     * if everything went ok, the method returns normally.
     * @throws JabberMessageException if password is not changed
     */
    public void changePassword(String newPassword) throws JabberMessageException, SendMessageFailedException {
        RegisterIQMessage msg = new RegisterIQMessage(JabberIQMessage.TYPE_SET);
        JabberContext context = session.getContext();
        //send to the server
        msg.setTo(context.getServerNameJID());
        msg.addField("username", context.getUsername());
        msg.addField("password", newPassword);
        msg.setSynchronized(true);
        session.sendMessage(msg);
        //check if message contains error
        if (msg.isError())
            throw new JabberMessageException(msg.getErrorMessage());
    }

    /**
     * <p>Retrieves a  list of fields that are required to register with a specific gateway (ie. AIM, MSN, etc).
     * You can also use this method retrieve the current information registered with the gateway (except the password).
     * This method works for user registration as well as gateway/transport registration.</p>
     * <p>If you happen to be registered already with the gateway, this method will return
     * the fields (with the data that you registered with) that you can modify.  The
     * information inside will contain a key that must be passed between the client
     * and server for any changes or sets.  The key acts as a security mechanism that makes
     * sure that you're the one who is modifying the information.</p>
     * <p>You need to provide the gateway that this message goes to.  This can actually be
     * retrieved by querying for the list of agents from the server (available in the Server Service).</p>
     * <p>This method is synchronous and will not return until a reply or error is received.</p>
     * @param serviceJID the JID of the gateway (ie. jabber.org, aim.jabber.org, etc)
     */
    public HashMap getRegisterFields(JID serviceJID) throws JabberMessageException, SendMessageFailedException {
        RegisterIQMessage msg = new RegisterIQMessage(JabberIQMessage.TYPE_GET);
        msg.setTo(serviceJID);
        msg.setSynchronized(true);
        session.sendMessage(msg);
        if (msg.isError())
            throw new JabberMessageException(msg.getErrorMessage());
        // retrieve all the fields
        return ((RegisterIQMessage) msg.getReplyMessage()).getFields();
    }

    /**
     * <p>Tries to unsubscribe/remove your account from a specific service.  This is normally used to unregister
     * yourself from a service (ie. MSN, AIM, ICQ, etc) that you were previously registered with.
     * in order to unsubscribe from an account, you obviously need to be registered first.  Thus, you muse
     * first retrieve the list of register fields and obtain the key.  Then, you can use the key
     * to remove.</p><p>This method is synchronous and will not return until a reply or error is received</p>
     * <p>Note that the key has been obsoleted.  This may mean that you do not necessarily need to pass in
     * a key any longer depending on the server.</p>
     * @param serviceJID the service JID that you will be removing yourself from.
     * @param key the key that is sent by the server to you when you retrieved the fields (null if you don't have one).
     */
    public void removeRegisterService(JID serviceJID, String key) throws JabberMessageException,
            SendMessageFailedException {
        RegisterIQMessage msg = new RegisterIQMessage(JabberIQMessage.TYPE_SET);
        msg.setTo(serviceJID);
        if (key != null) msg.addField("key", key);
        msg.addField("remove", null);
        msg.setSynchronized(true);
        session.sendMessage(msg);
        if (msg.isError())
            throw new JabberMessageException(msg.getErrorMessage());
    }

    /**
     * <p>translates a service-specific username into a JID usable for reference.  This is used to translate
     * gateway-specific user naming convention into a Jabber JID naming convention (ie. "123456" for ICQ
     * might be translated into "123456@icq.jabber.org").</p> <p>This message is sent synchronously and will not return until
     * an error or reply is received.
     * @param serviceJID the service/gateway to translate this user into
     * @param user the service/gateway's user name that needs to be translated
     * @throws JabberMessageException if there is an error during translation or timeout occurs.
     * @throws SendMessageFailedException sending the message failed
     * @return the translated name in JID form, or null the name cannot be translated somehow.
     */
    public String translateUserToJID(JID serviceJID, String user) throws JabberMessageException,
            SendMessageFailedException {
        //currently only the <prompt> seems to be used
        GatewayIQMessage msg = new GatewayIQMessage(JabberIQMessage.TYPE_SET);
        msg.setTo(serviceJID);
        msg.setSynchronized(true);
        msg.addField("prompt", user);
        session.sendMessage(msg);
        if (msg.isError())
            throw new JabberMessageException(msg.getErrorMessage());
        GatewayIQMessage reply = (GatewayIQMessage) msg.getReplyMessage();
        return (String) reply.getFields().get("prompt");
    }

    /**
     * requests for the logged in user's vacation message settings.  This returns the vacation reply message
     * that contains the data requested if wait is true
     * @param wait whether to wait for the response or not
     * @return the vacation message reply (not the request)
     */
    public VacationIQMessage requestVacationMessage(boolean wait) throws SendMessageFailedException, JabberMessageException {
        VacationIQMessage msg = VacationIQMessage.createRequestVacationMessage();
        msg.setSynchronized(wait);
        session.sendMessage(msg);
        if (wait) {
            if (msg.isError())
                throw new JabberMessageException(msg.getErrorMessage());
            VacationIQMessage reply = (VacationIQMessage) msg.getReplyMessage();
            return reply;
        }
        return null;
    }

    /**
     * removes the vacation message.  The message will be sent synchronously and will not return until
     * a reply is received.
     */
    public void removeVacationMessage() throws SendMessageFailedException, JabberMessageException {
        VacationIQMessage msg = VacationIQMessage.createRequestVacationMessage();
        msg.setSynchronized(true);
        session.sendMessage(msg);
        if (msg.isError())
            throw new JabberMessageException(msg.getErrorMessage());
    }

    /**
     * sets the vacation message synchronously.
     * @param startDate the starting date for the vacation
     * @param endDate the ending date for the vacation
     * @param vacationMessage the message to display
     */
    public void setVacationMessage(Calendar startDate, Calendar endDate, String vacationMessage) throws SendMessageFailedException, JabberMessageException {
        VacationIQMessage msg = VacationIQMessage.createSetVacationMessage(startDate, endDate, vacationMessage);
        msg.setSynchronized(true);
        session.sendMessage(msg);
        if (msg.isError())
            throw new JabberMessageException(msg.getErrorMessage());
    }
}
