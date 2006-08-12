package com.echomine.jabber;

import com.echomine.common.ParseException;
import java.util.List;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Since IQ Messages are unique based on the internal <query> tag, special parser classes is created just to create the
 * appropriate IQ message.  This parser will check to see which iq message it is.  It will simply pass the delegation
 * of creating the message back to the JabberMessageParser.  In essence, it's sorta doing a double roundabout loop
 * just to create the message.  This way, all message parsers need only be registered with one object.
 */
public class JabberIQMessageParser implements JabberMessageParsable {
    /** @return the JabberMessage or null if the <query> type is not recognized */
    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        JabberMessage msg = null;
        //check the query tag
        //there should only be one <query> tag inside the <iq> OR
        //there should be no <tags> inside OR
        //there should be some sort of <query> with <error> tag
        List elems = msgTree.getChildren();
        //0 children = default IQ Message
        if (elems.isEmpty()) {
            msg = new JabberIQMessage();
            ((JabberIQMessage)msg).parse(parser, msgTree);
            return msg;
        } else {
            Element query = (Element)elems.get(0);
            String localName = query.getName();
            Namespace qns = query.getNamespace();
            try {
                msg = parser.createMessage(localName, qns, msgTree);
            } catch (MessageNotSupportedException ex) {
                //return a default IQ Message message
                msg = new JabberIQMessage();
                ((JabberIQMessage)msg).parse(parser, msgTree);
            }
            return msg;
        }
    }
}
