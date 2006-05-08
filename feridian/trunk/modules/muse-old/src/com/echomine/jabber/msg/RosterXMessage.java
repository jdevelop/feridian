package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberJDOMMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The roster message actually doesn't contain any attributes to the <query> tag that it uses.  However, it
 * contains a list of roster items.
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0093.html">JEP-0093 1.0</a></b></p>
 */
public class RosterXMessage extends JabberJDOMMessage {
    private static Log log = LogFactory.getLog(RosterXMessage.class);
    private ArrayList rosterList;

    /** constructs a default Roster X Message. */
    public RosterXMessage() {
        super(new Element("x", JabberCode.XMLNS_X_ROSTER));
    }

    /** adds a roster item to the current roster message. This will add the item straight into the DOM. */
    public void addRosterItem(RosterItem item) {
        //add it to our current DOM
        getDOM().addContent(item.getDOM());
    }

    /** adds a list of roster items */
    public void addRosterItems(List items) {
        Element root = getDOM();
        Iterator iter = items.iterator();
        RosterItem item;
        while (iter.hasNext()) {
            item = (RosterItem) iter.next();
            root.addContent(item.getDOM());
        }
    }

    /**
     * retrieves a list of roster items.  The first time it's called, it will parse the data out and cache it.
     * Subsequent calls will use the cached result.
     */
    public List getRosterItems() {
        if (rosterList != null) return rosterList;
        rosterList = new ArrayList();
        Iterator iter = getDOM().getChildren().iterator();
        Element temp;
        RosterItem item;
        while (iter.hasNext()) {
            temp = (Element) iter.next();
            //parse it into the roster item
            try {
                item = RosterItem.createRosterItem(temp);
                rosterList.add(item);
            } catch (ParseException ex) {
                if (log.isWarnEnabled())
                    log.warn("Unable to parse JID for roster item", ex);
            }
        }
        return rosterList;
    }

    public int getMessageType() {
        return JabberCode.MSG_X_ROSTER;
    }
}
