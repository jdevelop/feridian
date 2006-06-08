package com.echomine.jibx;

/**
 * The formatter contains a set of methods to serialize and deserialize data
 * elements.
 * 
 * @author ckchris
 * @since 1.0
 */
public class JiBXFormatter {
    private final static String FALSE_BOOLEAN = "0";
    private final static String TRUE_BOOLEAN = "1";
    
    /**
     * This will deserialize a number into a boolean. If the string is 0 or a
     * non-integer, then false will be returned. If the string is anything other
     * than 0, then a true is returned. If the string is null, then false is
     * returned.
     * 
     * @return true if number is anything other than 0, otherwise false.
     */
    public final static boolean deserializeBooleanNumber(String number) {
        if (number == null)
            return false;
        try {
            int num = Integer.parseInt(number);
            if (num != 0)
                return true;
        } catch (NumberFormatException ex) {
            // unable to parse string into an integer, so return false
        }
        return false;
    }

    /**
     * this method serializes a boolean into a number -- 0 for false, 1 for
     * true.
     * 
     * @param booleanValue the boolean to serialize
     */
    public final static String serializeBooleanNumber(boolean booleanValue) {
        if (booleanValue)
            return TRUE_BOOLEAN;
        else
            return FALSE_BOOLEAN;
    }
    
}
