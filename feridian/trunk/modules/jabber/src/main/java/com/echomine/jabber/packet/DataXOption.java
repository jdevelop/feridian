package com.echomine.jabber.packet;

/**
 * Supporting class for jabber:x:data support. This represents one option that
 * is used for list-single and list-multi fields.
 * 
 * @see DataXPacket
 * @see DataXField
 */
public class DataXOption {
    private String label;
    private String value;

    /** default constructor for use to parse incoming options */
    public DataXOption() {
    }

    /** accepts a value for the option */
    public DataXOption(String value) {
        this(value, null);
    }

    /**
     * accepts a value and optionally a label. The label can be null. If the
     * label is null, then by default, the label will be the same as the value
     * as per the JEP specification.
     * 
     * @param value the value of the option
     * @param label optionally the label, or null if not used (which will
     *            default to value when retrieved)
     */
    public DataXOption(String value, String label) {
        this.label = label;
        this.value = value;
    }

    /**
     * retrieves the label. If the label does not exist (ie. null), then the
     * value will be returned instead
     * 
     * @return the label or the value string if label is null
     */
    public String getLabel() {
        if (label == null)
            return getValue();
        return label;
    }

    /** sets the label. Set to null to default to using the value for the label */
    public void setLabel(String label) {
        this.label = label;
    }

    /** @return the value for the option */
    public String getValue() {
        return value;
    }

    /** sets the value */
    public void setValue(String value) {
        this.value = value;
    }
}
