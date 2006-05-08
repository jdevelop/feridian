package com.echomine.jabber.msg;

import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberIQMessage;
import org.jdom.Element;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Deals with messages for registering, password changes, user profile changes, etc.  This message represents the
 * jabber:iq:register namespace.
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0077.html">JEP-0077 Version 1.1</a></b></p>
 */
public class RegisterIQMessage extends JabberIQMessage implements JabberCode {
    /**
     * this constructor is for creating outgoing messages.  It is here to be used by
     * subclasses.  The constructor simply creates a default element tree with the
     * <iq> as the top top level tag and <query> as its child, and then sets the message to use that tree.
     */
    public RegisterIQMessage(String type) {
        super(type);
        getDOM().addContent(new Element("query", XMLNS_IQ_REGISTER));
    }

    /**
     * sets the default to be of iq type "get"
     */
    public RegisterIQMessage() {
        this(TYPE_GET);
    }

    /**
     * normally used to add fields that should be sent to the server when registering a new account
     * or updating a current one.  You could update the fields that you want.  If this is a new account
     * message, you should definitely include the username and password in here.
     *
     * @param name  the name of the field
     * @param value the value that is associated with the name
     */
    public void addField(String name, String value) {
        Element query = getDOM().getChild("query", XMLNS_IQ_REGISTER);
        //query should definitely exist
        Element field = new Element(name, XMLNS_IQ_REGISTER);
        if (value != null)
            field.setText(value);
        query.addContent(field);
    }

    /**
     * this method allows you to add multiple fields at once.  The hashtable contains strings for names and values.
     */
    public void addFields(HashMap fields) {
        Iterator iter = fields.keySet().iterator();
        if (!iter.hasNext()) return;
        Element query = getDOM().getChild("query", XMLNS_IQ_REGISTER);
        String name, value;
        Element field;
        do {
            name = (String) iter.next();
            value = (String) fields.get(name);
            field = new Element(name, XMLNS_IQ_REGISTER);
            if (value != null)
                field.setText(value);
            query.addContent(field);
        } while (iter.hasNext());
    }

    /**
     * this is used normally for incoming messages to retrieve the fields that are returned.
     *
     * @return hash map of name/value string pairs that contain the information inside the message.
     */
    public HashMap getFields() {
        HashMap fields = new HashMap();
        //obtain the dom
        Element query = getDOM().getChild("query", XMLNS_IQ_REGISTER);
        Iterator iter = query.getChildren().iterator();
        String name, value;
        Element field;
        while (iter.hasNext()) {
            field = (Element) iter.next();
            name = field.getName();
            value = field.getText();
            fields.put(name, value);
        }
        return fields;
    }

    public int getMessageType() {
        return MSG_IQ_REGISTER;
    }
}
