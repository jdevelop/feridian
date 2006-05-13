package com.echomine.jabber.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.echomine.xmpp.JID;
import com.echomine.xmpp.JIDFormatException;

/**
 * Represents the field in the jabber:x:data schema. This field will contain the
 * data to work properly with all the different types of fields as defined by
 * JEP-0004. Note that this is the base class. Every field type extends from
 * this class to provide the proper convenience methods to work with each
 * individual type.
 * 
 * @see DataXPacket
 * @see DataXOption
 */
public class DataXField {
    public static final String TYPE_BOOLEAN = "boolean";
    public static final String TYPE_FIXED = "fixed";
    public static final String TYPE_HIDDEN = "hidden";
    public static final String TYPE_JID_MULTI = "jid-multi";
    public static final String TYPE_JID_SINGLE = "jid-single";
    public static final String TYPE_LIST_MULTI = "list-multi";
    public static final String TYPE_LIST_SINGLE = "list-single";
    public static final String TYPE_TEXT_MULTI = "text-multi";
    public static final String TYPE_TEXT_PRIVATE = "text-private";
    public static final String TYPE_TEXT_SINGLE = "text-single";
    private static final String EMPTY_STRING = "";
    private boolean required = false;
    private String description;
    private ArrayList<String> values = new ArrayList<String>();
    private ArrayList<DataXOption> options = new ArrayList<DataXOption>();
    private String fieldType = TYPE_TEXT_SINGLE;
    private String label;
    private String variableName;

    /** default constructor to use for parsing incoming messages */
    public DataXField() {
        this(TYPE_TEXT_SINGLE);
    }

    /** constructor that requires only a field type. */
    public DataXField(String fieldType) {
        this(fieldType, null, null);
    }

    /**
     * constructs a field with the parameters. Only field type is required. The
     * rest are optional and can be null if not used
     * 
     * @param fieldType the field type
     * @param variableName the variable name, or null if not used
     * @param label the optional label name, or null if not used
     */
    public DataXField(String fieldType, String variableName, String label) {
        setFieldType(fieldType);
        this.variableName = variableName;
        this.label = label;
    }

    /** whether this field is a required field for input. Defaults to false */
    public boolean isRequired() {
        return required;
    }

    /** sets the required attribute for this field */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /** @return the description of the field, or empty string if none exists */
    public String getDescription() {
        if (description == null)
            return EMPTY_STRING;
        return description;
    }

    /** sets the description */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * retrieves the field type (ie. list-multi, list-single, boolean, text,
     * hidden, etc). The return value can be null if the field type did not
     * exist. The client should assume that the type is text-single if the
     * return type is null.
     */
    public String getFieldType() {
        if (fieldType == null) return TYPE_TEXT_SINGLE;
        return fieldType;
    }

    /**
     * sets the field type for this field. If the field type specified is not
     * supported or if the field type is null, then text-single will be used as
     * the fallback field type.
     * 
     * @throws IllegalArgumentException if the field type is not supported
     */
    public void setFieldType(String fieldType) {
        if (!isSupportedFieldType(fieldType))
            this.fieldType = TYPE_TEXT_SINGLE;
        else
            this.fieldType = fieldType;
    }

    /** @return the label associated with this field, or null if none exists */
    public String getLabel() {
        return label;
    }

    /** sets the label */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the variable name associated with this field, or null if none
     *         exists
     */
    public String getVariableName() {
        return variableName;
    }

    /** sets the variable name */
    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    /**
     * Adds a boolean value to the field. This is used only when you are using
     * the boolean type field. If the field type is set to anything other than
     * boolean type, then this method will throw an IllegalArgumentException. If
     * there are already other values set in this field, then all the values are
     * removed before setting the boolean value.
     * 
     * @param value the boolean value to add
     * @throws IllegalArgumentException when the field type is not a boolean
     *             type.
     */
    public void setBooleanValue(boolean value) {
        if (!TYPE_BOOLEAN.equals(fieldType))
            throw new IllegalArgumentException("This method can only be called when the field type is boolean");
        // clear values
        if (!values.isEmpty())
            values.clear();
        if (value)
            addValue("1");
        else
            addValue("0");
    }

    /**
     * Adds a jid to the field. This method can only be used when the field type
     * is jid-single. If the field type is set to anything other than the
     * specified types, IllegalArgumentException will be thrown. If there are
     * already other values set in this field, then all the values are removed
     * and will effectively be replaced by the JID that is passed in the
     * parameter.
     * 
     * @param jid the JID to add
     * @throws IllegalArgumentException when the field type is not a jid-multi
     *             type
     */
    public void setJIDValue(JID jid) {
        if (!TYPE_JID_SINGLE.equals(fieldType))
            throw new IllegalArgumentException("This method can only be called when the field type is jid-single");
        // clear values
        if (!values.isEmpty())
            values.clear();
        addValue(jid.toString());
    }

    /**
     * Adds a jid to the list of field values. This method can only be used when
     * the field type is jid-multi. If the field type is set to anything other
     * than the specified types, IllegalArgumentException will be thrown. If you
     * have a jid-single type, then you should use the setJIDValue() method
     * instead.
     * 
     * @param jid the JID to add
     * @throws IllegalArgumentException when the field type is not a jid-multi
     *             type
     */
    public void addJIDValue(JID jid) {
        if (!TYPE_JID_MULTI.equals(fieldType))
            throw new IllegalArgumentException("This method can only be called when the field type is jid-multi");
        addValue(jid.toString());
    }

    /**
     * adds the text to the list of field values. This method can only be used
     * when the field type is text-multi or fixed. If the field type is set to
     * anything other than the specified types, IllegalArgumentException will be
     * thrown. If you have a text-single type, then you should use the
     * setTextValue() method instead.
     * 
     * @param text the text to add
     * @throws IllegalArgumentException when the field type is not a text-multi
     *             type
     */
    public void addTextValue(String text) {
        if (!TYPE_TEXT_MULTI.equals(fieldType) && !TYPE_FIXED.equals(fieldType))
            throw new IllegalArgumentException("This method can only be called when the field type is text-multi or fixed");
        addValue(text);
    }

    /**
     * sets the field value to the text specified. This is used only when you
     * are using text-single, text-private, or hidden type field. If the field
     * type is set to anything other than the specified types,
     * IllegalArgumentException will be thrown. If there are already other
     * values set in this field, all the values will be removed and replaced by
     * the new value.
     * 
     * @param text the text to store, cannot be null
     * @throws IllegalArgumentException when the field type is not a text-single
     *             type
     */
    public void setTextValue(String text) {
        if (!TYPE_TEXT_SINGLE.equals(fieldType)
                && !TYPE_HIDDEN.equals(fieldType)
                && !TYPE_TEXT_PRIVATE.equals(fieldType))
            throw new IllegalArgumentException("This method can only be called when the field type is text-single or hidden or text-private");
        if (!values.isEmpty())
            values.clear();
        addValue(text);
    }

    /**
     * retrieves the first value as a String type. This method can be called for
     * any field type. The returned type will be string. If there are multiple
     * values associated with this field, the first value will be returned.
     * 
     * @return the first value as String, or null if there are no values to
     *         retrieves
     */
    public String getStringValue() {
        if (values.isEmpty())
            return null;
        return values.get(0);
    }

    /**
     * Retrieves the first value as a boolean value. The accepted value is "1"
     * for true and any other string for false ("0", "false", "true" will all
     * return false). This method will work for any field types, but it is
     * normally used to retrieve boolean types.
     * 
     * @return the first value of boolean, or false if there are no values to
     *         retrieve
     */
    public boolean getBooleanValue() {
        String val = values.get(0);
        if ("1".equals(val))
            return true;
        return false;
    }

    /**
     * retrieves the first value as a JID object. This method will work for any
     * field types, but it is normally used to retrieve JID types. If there is a
     * problem parsing the JID, an exception will be thrown.
     * 
     * @return the JID object or null if there is no value to retrieve
     * @throws JIDFormatException if there is any problem parsing JID out of a
     *             string
     */
    public JID getJIDValue() throws JIDFormatException {
        String val = (String) values.get(0);
        return JID.parseJID(val);
    }

    /**
     * retrieves all the values as a list of JID objects. This method will work
     * for any field types, but it is normally used to retrieve JID types. If
     * there is a problem parsing ANY JIDs, an exception will be thrown.
     * 
     * @return a list of JID objects, in the order that was received, or an
     *         empty list if no values exist
     * @throws JIDFormatException if there is any problem parsing ANY JID out of the
     *             list of values
     */
    public List<JID> getJIDValues() throws JIDFormatException {
        int size = values.size();
        String val;
        ArrayList<JID> list = new ArrayList<JID>(size);
        for (int i = 0; i < size; i++) {
            val = values.get(i);
            list.add(JID.parseJID(val));
        }
        return list;
    }

    /**
     * An internal method that simply adds the string value without doing any
     * validation checks. It is a convenience method called by all the other
     * methods to add values to the list.
     * 
     * @param value the text value to add
     */
    protected void addValue(String value) {
        if (value == null)
            throw new IllegalArgumentException("The value cannot be null");
        values.add(value);
    }

    /**
     * An internal method that adds an option into the options list without
     * doing any validation checks.
     * 
     * @param option the option to add
     */
    protected void addOption(DataXOption option) {
        if (option == null)
            throw new IllegalArgumentException("The option cannot be null");
        options.add(option);
    }

    /**
     * retrieves the values sets in the field. If there are no data, then empty
     * list is returned. The return list of values are not modifiable (ie. it's
     * read-only).
     */
    public List getStringValues() {
        return Collections.unmodifiableList(values);
    }

    /**
     * sets the values to a list of values you want added. This method bypasses
     * validation checks and thus will not check the internal values. This
     * method will clone the data from the list you passed in and store it
     * inside its own list. Passing in null will clear the values list. It is
     * very unlikely you will be using this method unless you are dealing with
     * unknown field types. Otherwise, you will use one of the setXXX() and
     * addXXX() methods to manipulate the list. Currently, this method is not
     * public. If you think you need it, then request that it be made public.
     */
    protected void setValues(List<String> values) {
        this.values.clear();
        if (values != null)
            this.values.addAll(values);
    }

    /**
     * gets the options list. If the values are null, then an empty list will be
     * returned. The return list of values are not modifiable (ie. it's
     * read-only).
     */
    public List getOptions() {
        return Collections.unmodifiableList(options);
    }

    /**
     * sets the list of options to display. This method is only usable when
     * field type is list-single or list-multi. If the type is not one of those
     * specified, IllegalArgumentException will be thrown. If there are options
     * available before calling this method, the old values will be replaced by
     * the new ones. This method will clone the data from the list you pass in
     * and store it inside its own list.
     * 
     * @param ops a list of DataXOption objects
     * @throws IllegalArgumentException if the field type is not the designated
     *             type
     */
    public void setOptions(List<DataXOption> ops) {
        if (!TYPE_LIST_SINGLE.equals(fieldType)
                && !TYPE_LIST_MULTI.equals(fieldType))
            throw new IllegalArgumentException("This method can only be called when the field type is list-single or list-multi");
        this.options.clear();
        if (ops != null)
            this.options.addAll(ops);
    }

    /** checks whether the field type is a supported field type */
    private boolean isSupportedFieldType(String fieldType) {
        if (TYPE_BOOLEAN.equals(fieldType) || TYPE_FIXED.equals(fieldType)
                || TYPE_HIDDEN.equals(fieldType)
                || TYPE_JID_MULTI.equals(fieldType)
                || TYPE_JID_SINGLE.equals(fieldType)
                || TYPE_LIST_MULTI.equals(fieldType)
                || TYPE_LIST_SINGLE.equals(fieldType)
                || TYPE_TEXT_MULTI.equals(fieldType)
                || TYPE_TEXT_SINGLE.equals(fieldType)
                || TYPE_TEXT_PRIVATE.equals(fieldType))
            return true;
        return false;
    }
}
