package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.*;
import org.jdom.Element;

import java.util.Calendar;

/**
 * Works with vacation messages.  This allow the user to specify and even schedule a time when
 * the vacation message will be displayed.
 * Normally, you must first check to see if the server supports vacation messages (it is a disco
 * service, thus it cannot be retrieved through jabber:iq:agent or jabber:iq:browse).
 * For now, vacation message have a defined disco namespace.  Thus, it will be used for now
 * until otherwise specified in the JEP.
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0109.html">JEP-0109 Version 0.2</a></b></p>
 *
 * @since 0.8a4
 */
public class VacationIQMessage extends JabberIQMessage implements JabberCode {
    private String vacationMessage = "";
    private Calendar startDate;
    private Calendar endDate;

    /**
     * this constructor is for creating outgoing messages.  It is here to be used by
     * subclasses.  The constructor simply creates a default element tree with the
     * <iq> as the top top level tag and <query> as its child, and then sets the message to use that tree.
     */
    public VacationIQMessage(String type) {
        super(type);
        getDOM().addContent(new Element("query", XMLNS_IQ_VACATION));
    }

    /**
     * sets the default to be of iq type "get"
     */
    public VacationIQMessage() {
        this(TYPE_GET);
    }

    /**
     * Convenience method to instantiate a vacation message that allows you to request for your own vacation
     * settings.
     */
    public static VacationIQMessage createRequestVacationMessage() {
        VacationIQMessage msg = new VacationIQMessage();
        return msg;
    }

    /**
     * Convenience method to create a vacation message that allows you to set your vacation message.
     *
     * @param startDate       the starting date, can be null
     * @param endDate         the ending date, can be null
     * @param vacationMessage the vacation message, or null or empty if none
     */
    public static VacationIQMessage createSetVacationMessage(Calendar startDate, Calendar endDate, String vacationMessage) {
        VacationIQMessage msg = new VacationIQMessage(JabberIQMessage.TYPE_SET);
        msg.setStartDate(startDate);
        msg.setEndDate(endDate);
        msg.setVacationMessage(vacationMessage);
        return msg;
    }

    /**
     * Convenience method to create a vacation message that allows you to remove your vacation message.
     */
    public static VacationIQMessage createRemoveVacationMessage() {
        VacationIQMessage msg = new VacationIQMessage(JabberIQMessage.TYPE_SET);
        return msg;
    }

    /**
     * the ending date for the vacation message.  The returned datetime is in the timezone that is
     * contained inside the calendar.  If you need to show it in your own current timezone, you must
     * convert the date returned by this method.
     *
     * @return the starting date of the vacation, or null if it has not been set
     */
    public Calendar getStartDate() {
        return startDate;
    }

    /**
     * the ending date for the vacation message.  The returned datetime is in the timezone that is
     * contained inside the calendar.  If you need to show it in your own current timezone, you must
     * convert the date returned by this method.
     *
     * @return the ending date of the vacation, or null if it has not been set
     */
    public Calendar getEndDate() {
        return endDate;
    }

    /**
     * @return the vacation message, or empty if there is no vacation message
     */
    public String getVacationMessage() {
        return vacationMessage;
    }

    /**
     * sets the vacation message.  If null is passed in, the vacation message will be set to an empty string.
     */
    public void setVacationMessage(String vacationMessage) {
        if (vacationMessage == null)
            this.vacationMessage = "";
        else
            this.vacationMessage = vacationMessage;
    }

    /**
     * sets the start date for the vacation message
     */
    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    /**
     * sets the end date for the vacation message
     */
    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    /**
     * parses the incoming XML DOM object for the data that we need
     */
    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        super.parse(parser, msgTree);
        //parse out the iq message data
        Element query = msgTree.getChild("query", XMLNS_IQ_VACATION);
        //check to make sure that there is a query tag
        if (query != null) {
            String temp = query.getChildText("start", XMLNS_IQ_VACATION);
            if (temp != null) setStartDate(JabberUtil.parseDateTime(temp));
            temp = query.getChildText("end", XMLNS_IQ_VACATION);
            if (temp != null) setEndDate(JabberUtil.parseDateTime(temp));
            temp = query.getChildText("message", XMLNS_IQ_VACATION);
            setVacationMessage(temp);
        }
        return this;
    }

    /**
     * encodes the data in this object into XML string
     *
     * @return the xml string
     */
    public String encode() throws ParseException {
        //add all the attributes into the tree
        Element x = getDOM();
        //remove any children
        x.getChildren().clear();
        Element query = new Element("query", XMLNS_IQ_VACATION);
        if (startDate != null)
            query.addContent(new Element("start", XMLNS_IQ_VACATION).addContent(JabberUtil.formatDateTime(startDate)));
        if (endDate != null)
            query.addContent(new Element("end", XMLNS_IQ_VACATION).addContent(JabberUtil.formatDateTime(endDate)));
        if (vacationMessage != null)
            query.addContent(new Element("message", XMLNS_IQ_VACATION).addContent(vacationMessage));
        x.addContent(query);
        return super.encode();
    }

    /**
     * @return the message type
     */
    public int getMessageType() {
        return MSG_IQ_VACATION;
    }
}
