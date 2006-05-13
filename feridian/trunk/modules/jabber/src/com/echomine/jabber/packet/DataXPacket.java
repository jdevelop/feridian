package com.echomine.jabber.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.echomine.xmpp.IPacket;

/**
 * <p>
 * This extension adds the new form-based data submission and retrieval
 * mechanism using the jabber:x:data namespace. The Data Gathering and Reporting
 * interface may not be supported by all message types. As of the current
 * implementation, the X message may be present in a chat message, a presence
 * message, and an iq message. Certain rules are present for each type of
 * message and requires that it conforms to the JEP standards (take a look at
 * the JEP to know the limitations and restrictions). It is suggested that you
 * read up on the JEP and know the details of working with this namespace before
 * using it. Alot of the support requires developer support. The API is unable
 * to make things easier for you in that way because of the data used in this
 * message is for displaying results to the user.
 * </p>
 * <p>
 * Current implementation has a known issue. Under JEP specification, the search
 * results/items can be sent back to the requester in multiple message chunks
 * with the same message ID. However, in the current implementation, if you set
 * this message to listen for a reply and you send this message in a
 * synchronized manner, the call will return upon receiving the first message
 * result and ignore the rest of the result messages. In order to workaround
 * this issue, you should not send this message synchronously if you are
 * submitting a search request that will return results. Rather, you should
 * specifically create a listener that will listen for the incoming messages and
 * combine all the incoming result messages of the same Message ID to form your
 * total results.
 * </p>
 * <p>
 * <b>Current Implementation: <a
 * href="http://www.jabber.org/jeps/jep-0004.html">JEP-0004 Version 2.1</a></b>
 * </p>
 * 
 * @see DataXField
 * @see DataXOption
 */
public class DataXPacket implements IPacket {
    public static final String TYPE_SUBMIT = "submit";
    public static final String TYPE_CANCEL = "cancel";
    public static final String TYPE_RESULT = "result";
    public static final String TYPE_FORM = "form";
    public static final String EMPTY_STRING = "";
    private String formType;
    private List<String> instructions;
    private String title;
    private List<DataXField> fields;
    private List<DataXField> reportedFields;
    private List<DataXItemList> itemFields;

    /** constructs a default message of type submit */
    public DataXPacket() {
        this(TYPE_SUBMIT);
    }

    /** constructs a default message of the type specified */
    public DataXPacket(String formType) {
        setFormType(formType);
    }

    /**
     * Retrieves the form type for the data. This can either be submit
     * (indicating that the data is to be submitted), cancel (cancellation of
     * request to fill out the form), form (a form containing data to be filled
     * out), and result (the result after form submission, such as search
     * results).
     * 
     * @return the form type
     */
    public String getFormType() {
        return formType;
    }

    /**
     * sets the form type to one of the types as presented by the constants in
     * this class.
     * 
     * @param formType the form type
     */
    public void setFormType(String formType) {
        this.formType = formType;
    }

    /**
     * @return the instructions that comes with the form, null if there are none.
     */
    public List<String> getInstructions() {
        return instructions;
    }

    /**
     * sets the instructions to fill out the form, null to set it as
     * non-existent.
     */
    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    /**
     * This adds only one instruction, which is typically the case. This will
     * 
     * @param instructions
     */
    public void addInstruction(String instruction) {
        if (instruction == null) return;
        if (instructions == null) 
            instructions = new ArrayList<String>(3);
        instructions.add(instruction);
    }

    /**
     * retrieves the optional title that goes along with the form
     * 
     * @return the form title, or empty string if there is none
     */
    public String getTitle() {
        if (title == null)
            return EMPTY_STRING;
        return title;
    }

    /**
     * sets the form title to the title specified. Set to null for
     * empty/non-existent
     * 
     * @param title the form title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * retrieves the list of fields from the message. The list is not
     * modifiable.
     * 
     * @return an unmodifiable list of DataXField objects, or null if no fields exist
     */
    public List getFields() {
        if (fields == null) return null;
        return Collections.unmodifiableList(fields);
    }

    /**
     * retrieves the list of reported fields from the message. The list is not
     * modifiable. This list is normally used to indicate the column headers for
     * incoming item results.
     * 
     * @return an unmodifiable list of DataXField objects, or null if no reported fields exist
     */
    public List getReportedFields() {
        if (reportedFields == null) return null;
        return Collections.unmodifiableList(reportedFields);
    }

    /**
     * retrieves the specified list of result items from the message. The list
     * is not modifiable. This list is normally used to represent item results
     * for searches
     * 
     * @return an list of DataXField objects, or null if no list exists
     * @throws ArrayIndexOutOfBoundsException if the index specified is not
     *             within bounds
     */
    public DataXItemList getItemFieldList(int idx) {
        if (itemFields == null) return null;
        return itemFields.get(idx);
    }

    /** adds a field to the field list */
    public void addField(DataXField field) {
        if (field == null)
            throw new IllegalArgumentException("Field to be added cannot be null");
        if (fields == null)
            fields = new ArrayList<DataXField>();
        fields.add(field);
    }

    /** adds a field to the reported field list */
    public void addReportedField(DataXField field) {
        if (field == null)
            throw new IllegalArgumentException("Field to be added cannot be null");
        if (reportedFields == null)
            reportedFields = new ArrayList<DataXField>();
        reportedFields.add(field);
    }

    /** adds a field to the item field list */
    public void addItemFieldList(DataXItemList list) {
        if (list == null)
            throw new IllegalArgumentException("Item field list to be added cannot be null");
        if (itemFields == null)
            itemFields = new ArrayList<DataXItemList>();
        itemFields.add(list);
    }
}
