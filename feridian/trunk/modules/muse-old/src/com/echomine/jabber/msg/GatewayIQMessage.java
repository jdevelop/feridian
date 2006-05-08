package com.echomine.jabber.msg;

import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberIQMessage;
import org.jdom.Element;

import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Add support to query gateways and request certain information from it. This is normally
 * used to translate a service-specific username into a JID-compliant name.  For instance,
 * the AIM username may be "Some User".  The translation service might transform it into a JID
 * that looks like "SomeUser@aim.jabber.org".</p>
 */
public class GatewayIQMessage extends JabberIQMessage implements JabberCode {
    /**
     * this constructor is for messages with type.
     */
    public GatewayIQMessage(String type) {
        super(type);
        //add in the query element
        getDOM().addContent(new Element("query", XMLNS_IQ_GATEWAY));
    }

    /**
     * defaults to iq type get
     */
    public GatewayIQMessage() {
        this(TYPE_GET);
    }

    /**
     * normally used to add fields that should be sent to the server when querying for gateway
     * info or setting the data. You could update the fields that you want.  If this is a new account
     * message, you should definitely include the username and password in here.
     *
     * @param name  the name of the field
     * @param value the value that is associated with the name
     */
    public void addField(String name, String value) {
        Element query = getDOM().getChild("query", XMLNS_IQ_GATEWAY);
        //query should definitely exist
        Element field = new Element(name, XMLNS_IQ_GATEWAY);
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
        Element query = getDOM().getChild("query", XMLNS_IQ_GATEWAY);
        String name, value;
        Element field;
        do {
            name = (String) iter.next();
            value = (String) fields.get(name);
            field = new Element(name, XMLNS_IQ_GATEWAY);
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
        Element query = getDOM().getChild("query", XMLNS_IQ_GATEWAY);
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
        return MSG_IQ_GATEWAY;
    }
}
