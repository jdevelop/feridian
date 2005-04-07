package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JID;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>represents an agent object.  This is here to support the AgentIQMessage.  It is very similar
 * to JIDType because it will soon be superseded by the new browsing framework (which uses JIDType).
 * The fields for this object are readonly because this function is usually used to retrieve data sent from the server.</p>
 * <p>This object is not reusable once instantiated.  Consider it immutable.</p>
 * @deprecated Replaced by new JEP-0030 Service Discovery
 * @see ServiceInfoIQMessage
 */
public class Agent {
    private JID jid;
    private HashMap features = new HashMap();
    private ArrayList ns = new ArrayList();

    public Agent(JID jid) {
        this.jid = jid;
    }

    public JID getJID() {
        return jid;
    }

    /** @return a short phrase describing the service, or null if empty */
    public String getDescription() {
        return (String) features.get("description");
    }

    /**
     * inclusion of this empty element signals that the service is a multi-user chat service
     * @return true if the agent supports multi-user chat
     */
    public boolean supportsMultiUserChat() {
        return features.containsKey("groupchat");
    }

    /**
     * inclusion of this empty element signals that the service supports registration
     * @return true if the agent support registration
     */
    public boolean supportsRegistration() {
        return features.containsKey("register");
    }

    /**
     * inclusion of this empty element signals that the service supports searching
     * @return true if the agent supports searching
     */
    public boolean supportsSearching() {
        return features.containsKey("search");
    }

    /** @return the name of the agent as sent by the server, null if empty */
    public String getName() {
        return (String) features.get("name");
    }

    /** @return the transport name of the agent as sent by the server, null if empty */
    public String getTransport() {
        return (String) features.get("transport");
    }

    /** @return the service name of the agent as returned by the server, null if empty */
    public String getService() {
        return (String) features.get("service");
    }

    /**
     * retrieves a list of features supported by the agent.  This basically contains
     * all the names (the agent name, the service name, the transport name) plus all
     * the features supported by the agent (ie. registration, search, groupchat).
     * By querying for the existence of a specific feature, you then know what is supported
     * by the agent and act accordingly.  For instance, if you know that the User Directory
     * support the Search feature, you can then add an option to allow the user to do a search through this specific agent.
     * @return a hashmap of features that are String objects
     */
    public HashMap getFeatureList() {
        return features;
    }

    /**
     * retrieves the list of namespaces that the agent supports.  This is basically
     * the newer set of functionality that will supersede the older feature list.  It
     * contains the namespaces supported by the agent (ie. jabber:iq:conference, etc).
     * Once you know the namespace supported, you can support that by doing namespace-specific
     * actions for that particular agent.
     * @return a list of the namespaces (String objects)
     */
    public List getNSList() {
        return ns;
    }

    /**
     * parses the incoming message for the data.  The element passed in
     * should be the beginning of the agent element (ie. the <agent> element).
     */
    public void parse(Element agentElem) throws ParseException {
        //clear the internal data first first
        features.clear();
        ns.clear();
        //now parse the rest of the info
        List list = agentElem.getChildren();
        int size = list.size();
        Element elem;
        String name;
        for (int i = 0; i < size; i++) {
            elem = (Element) list.get(i);
            //add the data to the hashtable
            //if it's a ns, then add it to the ns space instead
            name = elem.getName();
            if (name.equals("ns"))
                ns.add(elem.getText());
            else
                features.put(name, elem.getText());
        }
    }
}
