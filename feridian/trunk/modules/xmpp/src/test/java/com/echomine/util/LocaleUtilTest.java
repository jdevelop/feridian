package com.echomine.util;

import java.util.Locale;

import junit.framework.TestCase;

/**
 * tests the locale formatting class 
 */
public class LocaleUtilTest extends TestCase {

    public void testParseLocale() throws Exception {
        Locale locale = LocaleUtil.parseLocale("en-us");
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
        locale = LocaleUtil.parseLocale("en");
        assertEquals("en", locale.getLanguage());
        locale = LocaleUtil.parseLocale("en-us-west");
        assertEquals("en", locale.getLanguage());
        assertEquals("US", locale.getCountry());
        assertNull(LocaleUtil.parseLocale("2blah3-3323-blah"));
    }
    
    public void testFormatLocale() throws Exception {
        Locale locale = new Locale("en", "us");
        assertEquals("en-us", LocaleUtil.format(locale));
        locale = new Locale("en");
        assertEquals("en", LocaleUtil.format(locale));
        locale = new Locale("en", "us", "West");
        assertEquals("en-us", LocaleUtil.format(locale));
    }
}
