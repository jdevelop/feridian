package com.echomine.jabber.msg;

import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberContext;
import com.echomine.jabber.JabberIQMessage;
import com.echomine.util.HexDec;
import org.jdom.Element;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This message object deals with all the workings of sending and receiving authentication messages.
 * The auth message doesn't require to and from field so they're not required by this message.
 * The outgoing auth message is synchronized by default since it requires waiting for a reply to know the status
 * of the outgoing message.  Incoming auth message is not synchronized by default since incoming message
 * normally won't get sent out again.
 */
public class AuthIQMessage extends JabberIQMessage implements JabberCode {
    public final static int AUTH_CLEARTEXT = 1;
    public final static int AUTH_DIGEST = 2;
    public final static int AUTH_ZERO_KNOWLEDGE = 3;
    private int mostSecureAuth = 0;
    private String mostSecurePassword = null;
    private String digest = null;
    private String hash = null;
    private String zerokToken;
    private int zerokSeq;

    /**
     * this constructor is used for creating an initial outgoing message.
     * It does nothing except set the type and add in the default <query> tag.
     */
    public AuthIQMessage(String type) {
        super(type);
        setSynchronized(true);
        getDOM().addContent(new Element("query", XMLNS_IQ_AUTH));
    }

    /**
     * this constructor is used for incoming iq:auth messages. Outgoing messages should use the static methods instead.
     */
    public AuthIQMessage() {
        this(TYPE_GET);
    }

    /**
     * retrieves the most secure authentication method that exists inside the reply message.
     * This message can only be called when the message is a "get" message type AND has already
     * received a reply.  If this did not occur, then calling this method will return the least
     * secure authentication method by default.  Thus, it is wise that you create and send this
     * message first before using this message.
     *
     * @return the most secure authentication method that can be used by this server
     */
    public int getMostSecureAuth() {
        //cached result.. when it's parsed once,
        //it doesn't need to go through it again
        if (mostSecureAuth != 0) return mostSecureAuth;
        //message is not a "get" type and no message reply
        if (getReplyMessage() == null)
            return AUTH_CLEARTEXT;
        //parse the reply message
        AuthIQMessage reply = (AuthIQMessage) getReplyMessage();
        //get the DOM
        Element elem = reply.getDOM();
        //parse the internal elements and put them into a temporary hash table
        HashMap authMethods = new HashMap();
        Iterator iter = elem.getChild("query", XMLNS_IQ_AUTH).getChildren().iterator();
        Element authMethod;
        while (iter.hasNext()) {
            authMethod = (Element) iter.next();
            //add it to the hash
            authMethods.put(authMethod.getName(), authMethod.getText());
        }
        //now determine which method is the most secure
        if (authMethods.containsKey("digest"))
            mostSecureAuth = AUTH_DIGEST;
        else
            mostSecureAuth = AUTH_CLEARTEXT;
        // now check if zero knowledge authentication is possible
        if (authMethods.containsKey("token") && authMethods.containsKey("sequence")) {
            String token = (String) authMethods.get("token");
            String sequence = (String) authMethods.get("sequence");
            //make sure both are not null and sequence is parseable into an integer
            if (token == null || sequence == null) return mostSecureAuth;
            try {
                zerokToken = token;
                zerokSeq = Integer.parseInt(sequence);
            } catch (NumberFormatException ex) {
                return mostSecureAuth;
            }
            //Added workaround for when the sequence reaches 0
            //use the most secure authentication that we know right now
            if (zerokSeq <= 1) return mostSecureAuth;
            mostSecureAuth = AUTH_ZERO_KNOWLEDGE;
        }
        return mostSecureAuth;
    }

    /**
     * This method checks to see which is the most secure authentication method first,
     * and based on that, return the password for that authentication method.
     *
     * @return the password for the most secure authentication method
     */
    public String getPassword(JabberContext context, int authType) {
        //cache the password so that it won't have to calculate twice
        if (mostSecurePassword != null) return mostSecurePassword;
        switch (authType) {
            case AUTH_ZERO_KNOWLEDGE:
                mostSecurePassword = getZeroKnowledgePassword(context, zerokToken, zerokSeq);
                break;
            case AUTH_DIGEST:
                mostSecurePassword = getDigestPassword(context);
                break;
            case AUTH_CLEARTEXT:
            default:
                mostSecurePassword = getCleartextPassword(context);
                break;
        }
        return mostSecurePassword;
    }

    /**
     * @return the cleartext password
     */
    protected String getCleartextPassword(JabberContext context) {
        return context.getPassword();
    }

    /**
     * The way a digest password is computed is as follows. Append the password to the Session ID (sent by the server), and
     * then run it through a SHA1 hash algorithm.  Then return the HEX representation of the hash.
     *
     * @return the digest password
     */
    protected String getDigestPassword(JabberContext context) {
        //cache digest so no need to go through calculation again
        if (digest != null) return digest;
        //instantiate a SHA1 hash
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(context.getSessionID().getBytes());
            md.update(context.getPassword().getBytes());
            byte[] hash = md.digest();
            //convert to hex representation
            digest = HexDec.convertBytesToHexString(hash).toLowerCase();
        } catch (NoSuchAlgorithmException ex) {
            //no algorithm, just return null
            return "";
        }
        return digest;
    }

    /**
     * The way to compute zero-k password is as follows (all using SHA1).
     * Client performs a digest(password) resulting in hashA, and retrieves the token "garbagerandom"
     * and sequence 462 from the server. The client then performs another digest(hashA + token)
     * resulting in hash0, and then performs 461 recursive digest calls resulting in hash461.
     * The client then sends hash461 to the server. The server receives hash461 from the client,
     * and performs an additional hash to result in hash462, and compares it to the stored hash
     * for that sequence#. If they match, store the hash461 from the now authenticated client
     * as the new hash and reduce the sequence#. <b>This feature is not supported</b>
     *
     * @return the zero-knowledge password
     */
    protected String getZeroKnowledgePassword(JabberContext context, String zerokToken, int zerokSeq) {
        //cache hash so no need to go through calculation again
        if (hash != null) return hash;
        //instantiate a SHA1 hash
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            //hash password first
            byte[] hashA = md.digest(context.getPassword().getBytes());
            //now hash hashA + zerokToken
            md.reset();
            md.update(HexDec.convertBytesToHexString(hashA).toLowerCase().getBytes());
            md.update(zerokToken.getBytes());
            byte[] hash0 = md.digest();
            for (int i = 0; i < zerokSeq; i++) {
                md.reset();
                //just start hashing
                hash0 = md.digest(HexDec.convertBytesToHexString(hash0).toLowerCase().getBytes());
            }
            //convert to hex representation
            hash = HexDec.convertBytesToHexString(hash0).toLowerCase();
        } catch (NoSuchAlgorithmException ex) {
            //no algorithm, just return null
            return "";
        }
        return hash;
    }

    /**
     * this returns a default message instance for getting authentication information.
     * It is the "get" auth message type.  uses for this is normally to retrieve the
     * available authentication methods (ie. password, digest, zero-knowledge)
     */
    public static AuthIQMessage createGetAuthMethodsMessage(JabberContext context) {
        AuthIQMessage authMsg = new AuthIQMessage(TYPE_GET);
        //get the DOM tree
        Element elem = authMsg.getDOM();
        //the dom structure for a get auth message is as follows
        //<query xmlns="jabber:iq:auth"><username/></query>
        Element query = elem.getChild("query", XMLNS_IQ_AUTH);
        Element username = new Element("username", XMLNS_IQ_AUTH);
        username.setText(context.getUsername());
        query.addContent(username);
        return authMsg;
    }

    /**
     * creates an auth message that has everything set for sending a login message.
     * Internally, it uses a set message type to create the message.
     * Authentication will also support anonymous resource if you set the username and password both to null.
     * If authentication is zero-knowledge authentication, you must provide the Token and the Sequence.
     * Otherwise, you can pass in null for the values.
     *
     * @param context    the context that will contains the login information
     * @param authType   the authentication type that will be used to login (valid types are listed in this class as constants)
     * @param zerokToken the token sent by the server, required only if you're using Zero-Knowledge Authentication
     * @param zerokSeq   the sequence sent by the server, required only if you're using Zero-Knowledge Authentication
     */
    public static AuthIQMessage createLoginMessage(JabberContext context, int authType, String zerokToken, int zerokSeq) {
        AuthIQMessage msg = new AuthIQMessage(TYPE_SET);
        msg.setZerokToken(zerokToken);
        msg.setZerokSeq(zerokSeq);
        Element elem = msg.getDOM();
        //the xml structure for a "set" is
        //<query xmlns="jabber:iq:auth"><username/><password|digest|hash/><resource/></query>
        Element query = elem.getChild("query", XMLNS_IQ_AUTH);
        //add username
        Element temp;
        if (context.getUsername() != null) {
            temp = new Element("username", XMLNS_IQ_AUTH);
            temp.setText(context.getUsername());
            query.addContent(temp);
        }
        if (context.getPassword() != null) {
            //add authentication type
            switch (authType) {
                case AUTH_DIGEST:
                    temp = new Element("digest", XMLNS_IQ_AUTH);
                    break;
                case AUTH_ZERO_KNOWLEDGE:
                    temp = new Element("hash", XMLNS_IQ_AUTH);
                    break;
                case AUTH_CLEARTEXT:
                default:
                    temp = new Element("password", XMLNS_IQ_AUTH);
                    break;
            }
            String password = msg.getPassword(context, authType);
            if (password != null)
                temp.setText(password);
            query.addContent(temp);
        }
        //add the resource
        temp = new Element("resource", XMLNS_IQ_AUTH);
        temp.setText(context.getResource());
        query.addContent(temp);
        return msg;
    }

    /**
     * protected method since setting these values should only be done by the message object itself
     */
    private void setZerokToken(String zerokToken) {
        this.zerokToken = zerokToken;
    }

    /**
     * protected method since setting these values should only be done by the message object itself
     */
    private void setZerokSeq(int zerokSeq) {
        this.zerokSeq = zerokSeq;
    }

    /**
     * @return the 0k token associated with the message. null if none exists
     */
    public String getZerokToken() {
        return zerokToken;
    }

    /**
     * @return the 0k sequence associated with the message
     */
    public int getZerokSeq() {
        return zerokSeq;
    }

    /**
     * @return the MSG_IQ_AUTH message type
     */
    public int getMessageType() {
        return MSG_IQ_AUTH;
    }
}
