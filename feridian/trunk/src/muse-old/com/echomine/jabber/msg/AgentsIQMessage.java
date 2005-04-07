package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.*;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This message encapsulates the retrieval of agents supported by the server.  As the implementation
 * provides readonly information to the agents hosted by the server, this class provides readonly methods
 * for retrieving the list of agents.</p> <p>The jabber:iq:agents is the old style of retrieving information and will soon be
 * superseded by the new browsing framework.  In fact, some jabber servers already support the new browsing framework
 * for agent listing (ie. jabber.org).  However, some others do not (specifically jabber.com).  Thus,
 * for compatibility, the jabber:iq:agents namespace is supported.</p>
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0094.html">JEP-0094 Version 0.1</a></b></p>
 *
 * @see ServiceInfoIQMessage
 * @see ServiceItemsIQMessage
 * @deprecated Replaced by new JEP-0030 Service Discovery
 */
public class AgentsIQMessage extends JabberIQMessage {
    private ArrayList agents = new ArrayList();

    /**
     * defaults to iq type get for retrieval or for parsing incoming messages
     */
    public AgentsIQMessage() {
        super(TYPE_GET);
        //add in the query element
        getDOM().addContent(new Element("query", JabberCode.XMLNS_IQ_AGENTS));
    }

    /**
     * retrieve the list of agents.
     *
     * @return a List of Agent objects
     */
    public List getAgentList() {
        return agents;
    }

    /**
     * parses the incoming message for the data
     */
    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        //reset the message
        agents.clear();
        //let the parent class parse out the normal core attributes
        super.parse(parser, msgTree);
        //parse out the attributes for this type first
        //the query element is one level under the iq element
        //and SHOULD be the only element inside the <iq>
        Element query = msgTree.getChild("query", JabberCode.XMLNS_IQ_AGENTS);
        if (query == null)
            throw new ParseException("No agent message exists");
        //now retrieve all the agent element blocks
        List agentElems = query.getChildren("agent", JabberCode.XMLNS_IQ_AGENTS);
        Agent agent;
        Element agentElem;
        int size = agentElems.size();
        JID agentJID;
        for (int i = 0; i < size; i++) {
            agentElem = (Element) agentElems.get(i);
            agentJID = new JID(agentElem.getAttributeValue("jid"));
            //instantiate a new agent object
            agent = new Agent(agentJID);
            //parse the elements
            agent.parse(agentElem);
            //adds the agent to the list
            agents.add(agent);
        }
        return this;
    }

    public int getMessageType() {
        return JabberCode.MSG_IQ_AGENTS;
    }
}
