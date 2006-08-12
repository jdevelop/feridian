package com.echomine.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * This will format and parse locale based on the XMPP's definition of xml:lang.
 * The format follows RFC 3066. Although this class will parse a RFC compliant
 * string, it will only maintain the first tags (primary and first subtag). The
 * rest are concatenated as one single variant string.
 * </p>
 * <p>
 * Language-Tag = Primary-subtag *( "-" Subtag ) <br/> Primary-subtag = 1*8ALPHA
 * <br/> Subtag = 1*8(ALPHA / DIGIT)
 * </p>
 */
public class LocaleUtil {
    private static final Pattern localePat = Pattern.compile("([a-zA-Z]{1,8})(?:-(\\w{1,8}))?.*");

    /**
     * formats the locale into a XMPP compliant xml:lang string. The format will
     * only consist of only the language and country (the first two tags).
     * 
     * @param locale the locale
     * @return the string of the locale
     */
    public static final String format(Locale locale) {
        if (locale == null) 
            throw new IllegalArgumentException("Locale must be specified and cannot be null");
        StringBuffer result = new StringBuffer(locale.getLanguage());
        boolean c = locale.getCountry().length() != 0;
        if (c)
            result.append("-").append(locale.getCountry().toLowerCase(locale));
        return result.toString();
    }

    /**
     * this will parse out the locale from the given locale string. It will only
     * parse out the country and language (the first two tags), and ignore the
     * rest.
     * 
     * @param localeStr the locale string
     * @return the locale or null if the locale string doesn't match
     */
    public static final Locale parseLocale(String localeStr) {
        if (localeStr == null) 
            throw new IllegalArgumentException("locale string cannot be null");
        Matcher matcher = localePat.matcher(localeStr);
        if (matcher.matches()) {
            int size = matcher.groupCount();
            String lang = matcher.group(1);
            String country = "";
            if (size >= 2)
                country = matcher.group(2);
            if (country == null)
                country = "";
            return new Locale(lang, country);
        }
        return null;
    }
}
