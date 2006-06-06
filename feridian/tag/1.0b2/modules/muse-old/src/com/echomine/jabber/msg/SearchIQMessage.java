package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JID;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberIQMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This message works with the jabber:iq:search namespace.
 * The jabber:iq:search namespace is used to search for users in a directory. Such a directory
 * is usually called a Jabber User Directory or JUD; the largest such directory on the Jabber network
 * is that hosted at users.jabber.org on the jabber.org public Jabber server. Users may add, update, or
 * delete their information to this directory using the jabber:iq:register namespace; they may also search
 * for other users in the directory using the jabber:iq:search namespace. The specific fields available for
 * searching are determined by the implementation; however, the possible search fields are limited to those
 * documented herein (i.e., the namespace is not extensible).
 * The basic functionality is for a user to query a directory regarding the possible search fields, to send
 * a search query, and to receive search results. There is currently no mechanism for paging through results
 * or limiting the number of "hits".
 * TODO: addFields, addField, getFields are similar to RegisterIQMessage.  Should refactor when get a chance.
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0055.html">JEP-0055 Version 1.0</a></b></p>
 *
 * @since 0.8a4
 */
public class SearchIQMessage extends JabberIQMessage implements JabberCode {
    private static Log log = LogFactory.getLog(SearchIQMessage.class);

    /**
     * this constructor is for creating outgoing messages.  It is here to be used by
     * subclasses.  The constructor simply creates a default element tree with the
     * <iq> as the top top level tag and <query> as its child, and then sets the message to use that tree.
     */
    public SearchIQMessage(String type) {
        super(type);
        getDOM().addContent(new Element("query", XMLNS_IQ_SEARCH));
    }

    /**
     * sets the default to be of iq type "get"
     */
    public SearchIQMessage() {
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
        Element query = getDOM().getChild("query", XMLNS_IQ_SEARCH);
        //query should definitely exist
        Element field = new Element(name, XMLNS_IQ_SEARCH);
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
        Element query = getDOM().getChild("query", XMLNS_IQ_SEARCH);
        String name, value;
        Element field;
        do {
            name = (String) iter.next();
            value = (String) fields.get(name);
            field = new Element(name, XMLNS_IQ_SEARCH);
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
        Element query = getDOM().getChild("query", XMLNS_IQ_SEARCH);
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

    /**
     * retrieves the list of search result items if there are any.  If none exists, then an empty list
     * will be returned.
     *
     * @return the list of SearchItem objects, empty if none exists.
     * @throws java.lang.IllegalStateException
     *          if the message type is not a result type
     */
    public List getResultItems() {
        if (!TYPE_RESULT.equals(getType()))
            throw new IllegalStateException("This method can only be called when the message type is a result type");
        ArrayList list = new ArrayList();
        Namespace ns = XMLNS_IQ_SEARCH;
        Element query = getDOM().getChild("query", ns);
        Iterator iter = query.getChildren("item", ns).iterator();
        Element elem;
        SearchItem item;
        while (iter.hasNext()) {
            elem = (Element) iter.next();
            try {
                item = new SearchItem(new JID(elem.getAttributeValue("jid")), elem.getChildText("first", ns), elem.getChildText("last", ns), elem.getChildText("nick", ns), elem.getChildText("email", ns));
                list.add(item);
            } catch (ParseException ex) {
                if (log.isWarnEnabled())
                    log.warn("Error parsing JID for the search item.  Ignoring this particular search item.", ex);
            }
        }
        return list;
    }

    public int getMessageType() {
        return MSG_IQ_SEARCH;
    }
}
