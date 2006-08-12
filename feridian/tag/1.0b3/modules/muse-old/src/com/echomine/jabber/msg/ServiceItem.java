package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JID;
import com.echomine.jabber.JabberCode;
import org.jdom.Element;

/**
 * Supporting class to work with the Service Discovery protocol.  This class contains one instance of a service item
 * as present in a service items result.  This class also supports the action "view" (on top of "update" and "remove")
 * to support Flexible Offline Message Retrieval.
 * @since 0.8a4
 * @see ServiceItemsIQMessage
 */
public class ServiceItem {
    public static final String ACTION_VIEW = "view";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_REMOVE = "remove";
    JID jid;
    String name;
    String node;
    String action;

    /** construct a service item with the required attributes set */
    public ServiceItem(JID jid) {
        this(jid, null, null);
    }

    /**
     * constructs a service item based on the following attributes
     * @param jid the jid of the item
     * @param name the optional name/description of the item, may be null
     */
    public ServiceItem(JID jid, String name) {
        this(jid, name, null);
    }

    /**
     * constructs a service item based on the following attributes
     * @param jid the jid of the item
     * @param name the optional name/description of the item, may be null
     * @param node the optional node name of the item, may be null
     */
    public ServiceItem(JID jid, String name, String node) {
        if (jid == null) throw new IllegalArgumentException("JID cannot be null");
        this.jid = jid;
        this.name = name;
        this.node = node;
    }

    /** constructor that will parse the incoming element for the element data */
    public ServiceItem(Element serviceElem) throws ParseException {
        parse(serviceElem);
    }

    /** @return the JID of the item */
    public JID getJID() {
        return jid;
    }

    /** sets the JID of the item */
    public void setJID(JID jid) {
        this.jid = jid;
    }

    /** @return the optional name/description of the item */
    public String getName() {
        return name;
    }

    /** sets the name/description of the item */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the optional node name associated with the item, null if none exist */
    public String getNode() {
        return node;
    }

    /**
     * sets the node name associated with the item
     * Node attributes SHOULD be used only when trying to provide or query information which is not directly addressable.
     * Node attributes SHOULD NOT be empty, and implementations MUST treat empty node attributes the same as no node
     * attribute being present.
     */
    public void setNode(String node) {
        if ("".equals(node))
            this.node = null;
        else
            this.node = node;
    }

    /** @return the action type associated with this item, or null if none exists */
    public String getAction() {
        return action;
    }

    /** sets the action type of this item.  Must be either null, ACTION_UPDATE, ACTION_REMOVE */
    public void setAction(String action) {
        if (action != null && !ACTION_VIEW.equals(action) && !ACTION_UPDATE.equals(action) && !ACTION_REMOVE.equals(action))
            throw new IllegalArgumentException("The action can only be of the following values: update, remove (or set to null if none)");
        this.action = action;
    }

    /**
     * parses the element for all the data.  This will also reset all the variables inside this class.
     * Thus, the instance can be reused for multiple parsing without any problems.
     * @param serviceElem the element that contains the service item
     */
    public void parse(Element serviceElem) throws ParseException {
        if (!"item".equals(serviceElem.getName()) && JabberCode.XMLNS_IQ_DISCO_ITEMS != serviceElem.getNamespace())
            throw new ParseException("The incoming element is not a recognizable service item XML element");
        jid = new JID(serviceElem.getAttributeValue("jid"));
        node = serviceElem.getAttributeValue("node");
        action = serviceElem.getAttributeValue("action");
        name = serviceElem.getAttributeValue("name");
    }

    public Element encode() {
        Element serviceElem = new Element("item", JabberCode.XMLNS_IQ_DISCO_ITEMS);
        serviceElem.setAttribute("jid", jid.toString());
        if (node != null)
            serviceElem.setAttribute("node", node);
        if (action != null)
            serviceElem.setAttribute("action", action);
        if (name != null)
            serviceElem.setAttribute("name", name);
        return serviceElem;
    }
}
