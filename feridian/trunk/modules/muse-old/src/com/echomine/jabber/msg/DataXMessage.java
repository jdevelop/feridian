package com.echomine.jabber.msg;

import com.echomine.common.ParseException;
import com.echomine.jabber.JabberCode;
import com.echomine.jabber.JabberJDOMMessage;
import com.echomine.jabber.JabberMessage;
import com.echomine.jabber.JabberMessageParser;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>This extension adds the new form-based data submission and retrieval mechanism using the jabber:x:data namespace.
 * The Data Gathering and Reporting interface may not be supported by all message types.  As of the current implementation,
 * the X message may be present in a chat message, a presence message, and an iq message.  Certain rules are present
 * for each type of message and requires that it conforms to the JEP standards (take a look at the JEP to know the
 * limitations and restrictions). It is suggested that you read up on the JEP and know the details of working
 * with this namespace before using it.  Alot of the support requires developer support.  The API is unable to
 * make things easier for you in that way because of the data used in this message is for displaying results to
 * the user.</p>
 * <p>Current implementation has a known issue.  Under JEP specification, the search results/items can be sent
 * back to the requester in multiple message chunks with the same message ID.  However, in Muse's current implementation,
 * if you set this message to listen for a reply and you send this message in a synchronized manner, the call will return
 * upon receiving the first message result and ignore the rest of the result messages.  In order to workaround this
 * issue, you should not send this message synchronously if you are submitting a search request that will return
 * results.  Rather, you should specifically create a listener that will listen for the incoming messages and combine
 * all the incoming result messages of the same Message ID to form your total results.</p>
 * <p><b>Current Implementation: <a href="http://www.jabber.org/jeps/jep-0004.html">JEP-0004 Version 2.1</a></b></p>
 * @since 0.8a4
 * @see DataXField
 * @see DataXOption
 */
public class DataXMessage extends JabberJDOMMessage {
    public static final String TYPE_SUBMIT = "submit";
    public static final String TYPE_CANCEL = "cancel";
    public static final String TYPE_RESULT = "result";
    public static final String TYPE_FORM = "form";
    public static final String EMPTY_STRING = "";
    private String formType;
    private String instructions;
    private String title;
    private ArrayList fields = new ArrayList();
    private ArrayList reportedFields = new ArrayList();
    private ArrayList itemFields = new ArrayList();

    /** constructs a default message of type submit */
    public DataXMessage() {
        this(TYPE_SUBMIT);
    }

    /** constructs a default message of the type specified */
    public DataXMessage(String formType) {
        super(new Element("x", JabberCode.XMLNS_X_DATA));
        setFormType(formType);
    }

    /**
     * Retrieves the form type for the data. This can either be submit (indicating that the data is to be submitted),
     * cancel (cancellation of request to fill out the form), form (a form containing data to be filled out), and
     * result (the result after form submission, such as search results).
     * @return the form type
     */
    public String getFormType() {
        return formType;
    }

    /**
     * sets the form type to one of the types as presented by the constants in this class.
     * @param formType the form type
     */
    public void setFormType(String formType) {
        this.formType = formType;
    }

    /**
     * @return the instructions that comes with the form, empty string if there is none
     */
    public String getInstructions() {
        if (instructions == null) return EMPTY_STRING;
        return instructions;
    }

    /**
     * sets the instructions to fill out the form, null to set it as non-existent.
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * retrieves the optional title that goes along with the form
     * @return the form title, or empty string if there is none
     */
    public String getTitle() {
        if (title == null) return EMPTY_STRING;
        return title;
    }

    /**
     * sets the form title to the title specified.  Set to null for empty/non-existent
     * @param title the form title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * retrieves the list of fields from the message.  The list is not modifiable.
     * @return an unmodifiable list of DataXField objects
     */
    public List getFields() {
        return Collections.unmodifiableList(fields);
    }

    /**
     * retrieves the list of reported fields from the message.  The list is not modifiable.
     * This list is normally used to indicate the column headers for incoming item results.
     * @return an unmodifiable list of DataXField objects
     */
    public List getReportedFields() {
        return Collections.unmodifiableList(reportedFields);
    }

    /**
     * retrieves the list of result items from the message.  The list is not modifiable.
     * This list is normally used to represent item results for searches
     * @return an unmodifiable list of DataXField objects
     */
    public List getItemFields() {
        return Collections.unmodifiableList(itemFields);
    }

    /** adds a field to the field list */
    public void addField(DataXField field) {
        if (field == null) throw new IllegalArgumentException("Field to be added cannot be null");
        fields.add(field);
    }

    /** adds a field to the reported field list */
    public void addReportedField(DataXField field) {
        if (field == null) throw new IllegalArgumentException("Field to be added cannot be null");
        reportedFields.add(field);
    }

    /** adds a field to the item field list */
    public void addItemField(DataXField field) {
        if (field == null) throw new IllegalArgumentException("Field to be added cannot be null");
        itemFields.add(field);
    }

    /** @return the message type for this class */
    public int getMessageType() {
        return JabberCode.MSG_X_DATA;
    }

    /** parses the incoming data */
    public JabberMessage parse(JabberMessageParser parser, Element msgTree) throws ParseException {
        super.parse(parser, msgTree);
        //parse the form type attribute
        Namespace ns = JabberCode.XMLNS_X_DATA;
        formType = msgTree.getAttributeValue("type");
        instructions = msgTree.getChildTextNormalize("instructions", ns);
        title = msgTree.getChildTextNormalize("title", ns);
        //parse fields
        List list = msgTree.getChildren("field", ns);
        int size;
        DataXField field;
        if (!list.isEmpty()) {
            size = list.size();
            for (int i = 0; i < size; i++) {
                field = new DataXField();
                field.parse((Element) list.get(i));
                fields.add(field);
            }
        }
        //parse reported fields if any
        if (msgTree.getChild("reported", ns) != null) {
            list = msgTree.getChild("reported", ns).getChildren("field", ns);
            if (!list.isEmpty()) {
                size = list.size();
                for (int i = 0; i < size; i++) {
                    field = new DataXField();
                    field.parse((Element) list.get(i));
                    reportedFields.add(field);
                }
            }
        }
        //parse item fields if any
        if (msgTree.getChild("item", ns) != null) {
            list = msgTree.getChild("item", ns).getChildren("field", ns);
            if (!list.isEmpty()) {
                size = list.size();
                for (int i = 0; i < size; i++) {
                    field = new DataXField();
                    field.parse((Element) list.get(i));
                    itemFields.add(field);
                }
            }
        }
        return this;
    }

    public String encode() throws ParseException {
        //add all the attributes into the tree
        Element x = getDOM();
        Namespace ns = JabberCode.XMLNS_X_DATA;
        //remove any children first
        if (!x.getChildren().isEmpty()) x.getChildren().clear();
        if (formType != null)
            x.setAttribute("type", formType);
        if (instructions != null)
            x.addContent(new Element("instructions", ns).setText(instructions));
        if (title != null)
            x.addContent(new Element("title", ns).setText(title));
        //add fields
        if (!fields.isEmpty()) {
            int size = fields.size();
            for (int i = 0; i < size; i++)
                x.addContent(((DataXField) fields.get(i)).encode());
        }
        //add reported fields if any
        if (!reportedFields.isEmpty()) {
            int size = reportedFields.size();
            Element elem = new Element("reported", ns);
            for (int i = 0; i < size; i++)
                elem.addContent(((DataXField) reportedFields.get(i)).encode());
            x.addContent(elem);
        }
        //add item fields if any
        if (!itemFields.isEmpty()) {
            int size = itemFields.size();
            Element elem = new Element("item", ns);
            for (int i = 0; i < size; i++)
                elem.addContent(((DataXField) itemFields.get(i)).encode());
            x.addContent(elem);
        }
        return super.encode();
    }
}
