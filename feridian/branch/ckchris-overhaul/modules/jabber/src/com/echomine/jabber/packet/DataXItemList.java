package com.echomine.jabber.packet;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is basically a wrapper to work with JiBX's mapping structure
 * properly without having to write a custom marshaller. It simply contains a
 * list of fields stored in each item list.
 * 
 * @autho Chris Chen
 */
public class DataXItemList {
    List<DataXField> fields = new ArrayList<DataXField>();

    /**
     * Constructs an instance with an empty list of fields
     */
    public DataXItemList() {
        fields = new ArrayList<DataXField>();
    }

    /**
     * Constructs an instance with the passed in fields
     * 
     * @param fields
     */
    public DataXItemList(List<DataXField> fields) {
        setFields(fields);
    }

    /**
     * Retrieves the fields stored inside the item list
     * 
     * @return the item fields within this list
     */
    public List<DataXField> getFields() {
        return fields;
    }

    /**
     * Add a new field to the current set of fields
     * 
     * @param field the field to add
     */
    public void addField(DataXField field) {
        fields.add(field);
    }

    /**
     * This set the fields by replacing whatever is currently being held with
     * the new set of fields.
     * 
     * @param fields the new set of fields to replace the current set
     */
    public void setFields(List<DataXField> fields) {
        this.fields = fields;
    }

    /**
     * clear all the fields held by this item list
     */
    public void clear() {
        fields.clear();
    }
}
