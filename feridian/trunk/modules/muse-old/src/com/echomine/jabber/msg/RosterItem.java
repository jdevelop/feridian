package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JID;
import com.echomine.jabber.JabberCode;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents one entity as saved in the roster list.  It contains the jid, the groups the entity is in, and other
 * subscription related information.
 */
public class RosterItem {
    public static final String SUBSCRIBE_BOTH = "both";
    public static final String SUBSCRIBE_FROM = "from";
    public static final String SUBSCRIBE_NONE = "none";
    public static final String SUBSCRIBE_REMOVE = "remove";
    public static final String SUBSCRIBE_TO = "to";
    private String name;
    private JID jid;
    private ArrayList groups = new ArrayList();
    private String subscription;
    private String ask;

    /**
     * default constructor usually for creating elements from existing data (ie. incoming info)
     */
    protected RosterItem() {
    }

    /**
     * default constructor for creating a roster item for use.
     *
     * @param jid  the JID of the roster item
     * @param name optional nickname for the roster JID (null if none)
     */
    public RosterItem(JID jid, String name) {
        this.jid = jid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the JID stored in the roster item
     */
    public JID getJID() {
        return jid;
    }

    /**
     * sets the JID to be stored in this roster item
     */
    public void setJID(JID jid) {
        this.jid = jid;
    }

    /**
     * @return the subscription status of the roster item
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     * sets the subscription status of the roster item
     */
    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    /**
     * adds a group to the roster item
     */
    public void addGroup(String name) {
        groups.add(name);
    }

    /**
     * removes a groups from the roster item
     */
    public void removeGroup(String name) {
        groups.remove(name);
    }

    /**
     * retrieves the list of groups that the roster item is in. The array will never be null but may contain zero elements.
     *
     * @return an array of group names the roster item is in
     */
    public String[] getGroups() {
        String[] groupList = new String[groups.size()];
        groups.toArray(groupList);
        return groupList;
    }

    /**
     * is the item/user in the specified group?
     */
    public boolean isInGroup(String name) {
        return groups.contains(name);
    }

    /**
     * indicates that this item should be removed or not
     *
     * @param remove true if item should be deleted from server, false otherwise
     */
    public void setRemove(boolean remove) {
        if (remove)
            subscription = "remove";
        else
            subscription = null;
    }

    /**
     * encodes the roster item into a XML element for outgoing roster message.
     * Since outgoing roster message does not require the ask attribute, it's not added by default.
     */
    public Element getDOM() {
        Element elem = new Element("item", JabberCode.XMLNS_IQ_ROSTER);
        //add the jid and jid only since they are the only ones that are used to send to the server
        elem.setAttribute("jid", jid.toString());
        if (name != null)
            elem.setAttribute("name", name);
        if (subscription != null)
            elem.setAttribute("subscription", subscription);
        //add the groups
        Element group;
        Iterator iter = groups.iterator();
        while (iter.hasNext()) {
            group = new Element("group", JabberCode.XMLNS_IQ_ROSTER);
            group.setText((String) iter.next());
            elem.addContent(group);
        }
        return elem;
    }

    /**
     * used to create a roster item into an object by parsing the elements passed in as the parameter.
     * Normally this is used to create roster items from incoming messages.  If you're using this,
     * make sure that the element is <item xmlns="jabber:iq:roster"/>.  It does not start at <iq> tag.
     *
     * @throws ParseException if the JID of the roster item cannot be parsed properly
     */
    public static RosterItem createRosterItem(Element rosterElem) throws ParseException {
        RosterItem item = new RosterItem();
        item.setName(rosterElem.getAttributeValue("name"));
        item.setJID(new JID(rosterElem.getAttributeValue("jid")));
        item.setSubscription(rosterElem.getAttributeValue("subscription"));
        item.setAsk(rosterElem.getAttributeValue("ask"));
        //go through the elements to retrieve the groups
        Iterator iter = rosterElem.getChildren("group", rosterElem.getNamespace()).iterator();
        Element temp;
        while (iter.hasNext()) {
            temp = (Element) iter.next();
            item.addGroup(temp.getText());
        }
        return item;
    }

    /**
     * returns a debugging output stream
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(200);
        buf.append("<item jid=\"").append(jid.toString()).append("\" ");
        buf.append("name=\"").append(name).append("\" ");
        buf.append("subscription=\"").append(subscription).append("\" ");
        buf.append("ask=\"").append(ask).append("\">");
        String[] groupList = getGroups();
        String group;
        for (int i = 0; i < groupList.length; i++) {
            group = groupList[i];
            buf.append("<group>").append(group).append("</group>");
        }
        buf.append("</item>");
        return buf.toString();
    }
}
