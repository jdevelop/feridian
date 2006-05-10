package org.jibx.binding.util;


/**
 * Wrapper for arrays of ordered strings. This verifies the arrays and supports
 * efficient lookups.
 */

public class StringArray
{
    /** Ordered array of strings. */
    private final String[] m_list;
    
    /**
     * Constructor from array of values. This checks the array values to make
     * sure they're ordered and unique, and if they're not throws an exception.
     * Once the array has been passed to this constructor it must not be
     * modified by outside code.
     * 
     * @param list array of values
     */
    public StringArray(String[] list) {
        validateArray(list);
        m_list = list;
    }

    /**
     * Constructor from array of values to be added to base instance. This
     * checks the array values to make sure they're ordered and unique, and if
     * they're not throws an exception. Once the array has been passed to this
     * constructor it must not be modified by outside code.
     * 
     * @param list array of values
     */
    public StringArray(String[] list, StringArray base) {
        validateArray(list);
        m_list = mergeArrays(list, base.m_list);
    }

    /**
     * Constructor from pair of base instances. This checks
     * the array values to make sure they're ordered and unique, and if they're
     * not throws an exception. Once the array has been passed to this
     * constructor it must not be modified by outside code.
     * 
     * @param array1 first base array
     * @param array2 second base array
     */
    public StringArray(StringArray array1, StringArray array2) {
        m_list = mergeArrays(array1.m_list, array2.m_list);
    }
    
    /**
     * Merge a pair of ordered arrays into a single array. The two source arrays
     * must not contain any values in common.
     * 
     * @param list1 first ordered array
     * @param list2 second ordered array
     * @return merged array
     */
    private String[] mergeArrays(String[] list1, String[] list2) {
        String[] merge = new String[list1.length + list2.length];
        int fill = 0;
        int i = 0;
        int j = 0;
        while (i < list1.length && j < list2.length) {
            int diff = list2[j].compareTo(list1[i]);
            if (diff > 0) {
                merge[fill++] = list1[i++];
            } else if (diff < 0) {
                merge[fill++] = list2[j++];
            } else {
                throw new IllegalArgumentException
                    ("Duplicate values in arrays");
            }
        }
        if (i < list1.length) {
            System.arraycopy(list1, i, merge, fill, list1.length-i);
        }
        if (j < list2.length) {
            System.arraycopy(list2, j, merge, fill, list2.length-j);
        }
        return merge;
    }

    /**
     * Make sure passed-in array contains values that are in order and without
     * duplicate values.
     * 
     * @param list
     */
    private void validateArray(String[] list) {
        if (list.length > 0) {
            String last = list[0];
            int index = 0;
            while (++index < list.length) {
                String comp = list[index];
                int diff = last.compareTo(comp);
                if (diff > 0) {
                    throw new IllegalArgumentException
                        ("Array values are not ordered");
                } else if (diff < 0) {
                    last = comp;
                } else {
                    throw new IllegalArgumentException
                        ("Duplicate values in array");
                }
            }
        }
    }
    
    /**
     * Get string at a particular index in the list.
     *
     * @param index list index to be returned
     * @return string at that index position
     */
    public String get(int index) {
        return m_list[index];
    }
    
    /**
     * Find index of a particular string in the array. This does
     * a binary search through the array values, using a pair of
     * index bounds to track the subarray of possible matches at
     * each iteration.
     *
     * @param value string to be found in list
     * @return index of string in array, or <code>-1</code> if
     * not present
     */
    public int indexOf(String value) {
        int base = 0;
        int limit = m_list.length - 1;
        while (base <= limit) {
            int cur = (base + limit) >> 1;
            int diff = value.compareTo(m_list[cur]);
            if (diff < 0) {
                limit = cur - 1;
            } else if (diff > 0) {
                base = cur + 1;
            } else {
                return cur;
            }
        }
        return -1;
    }
    
    /**
     * Get number of values in array
     *
     * @return number of values in array
     */
    public int size() {
        return m_list.length;
    }
}