/*
Copyright (c) 2003-2004, Dennis M. Sosnoski
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.
 * Neither the name of JiBX nor the names of its contributors may be used
   to endorse or promote products derived from this software without specific
   prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.jibx.extras;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * XML document comparator. This uses XMLPull parsers to read a pair of
 * documents in parallel, comparing the streams of components seen from the two
 * documents. The comparison ignores differences in whitespace separating
 * elements, but treats whitespace as significant within elements with only
 * character data content. 
 * 
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public class DocumentComparator
{
    /** Parser for first document. */
    protected XmlPullParser m_parserA;

    /** Parser for second document. */
    protected XmlPullParser m_parserB;
    
    /** Print stream for reporting differences. */
    protected PrintStream m_differencePrint;

    /**
     * Constructor. Builds the actual parser.
     *
     * @param print print stream for reporting differences
     * @throws XmlPullParserException on error creating parsers
     */

    public DocumentComparator(PrintStream print) throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        m_parserA = factory.newPullParser();
        m_parserB = factory.newPullParser();
        m_differencePrint = print;
    }

    /**
     * Build parse input position description.
     *
     * @param parser for which to build description
     * @return text description of current parse position
     */

    protected String buildPositionString(XmlPullParser parser) {
        return " line " + parser.getLineNumber() + ", col " +
            parser.getColumnNumber();
    }

    /**
     * Prints error description text. The generated text include position
     * information from both documents. 
     *
     * @param msg error message text
     */
    
    protected void printError(String msg) {
        if (m_differencePrint != null) {
            m_differencePrint.println(msg + " - from " +
                buildPositionString(m_parserA) + " to " +
                buildPositionString(m_parserB));
        }
    }

    /**
     * Verifies that the attributes on the current start tags match.
     *
     * @return <code>true</code> if the attributes match, <code>false</code> if
     * not
     */
    
    protected boolean matchAttributes() {
        int count = m_parserA.getAttributeCount();
        if (m_parserB.getAttributeCount() != count) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            String name = m_parserA.getAttributeName(i);
            String ns = m_parserA.getAttributeNamespace(i);
            String value = m_parserA.getAttributeValue(i);
            if (!value.equals(m_parserB.getAttributeValue(ns, name))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifies that the current start or end tag names match.
     *
     * @return <code>true</code> if the names match, <code>false</code> if not
     */
    
    protected boolean matchNames() {
        return m_parserA.getName().equals(m_parserB.getName()) &&
            m_parserA.getNamespace().equals(m_parserB.getNamespace());
    }

    /**
     * Compares a pair of documents by reading them in parallel from a pair of
     * parsers. The comparison ignores differences in whitespace separating
     * elements, but treats whitespace as significant within elements with only
     * character data content. 
     *
     * @param rdra reader for first document to be compared
     * @param rdrb reader for second document to be compared
     * @return <code>true</code> if the documents are the same,
     * <code>false</code> if they're different
     */
    
    public boolean compare(Reader rdra, Reader rdrb) {
        try {
        
            // set the documents and initialize
            m_parserA.setInput(rdra);
            m_parserB.setInput(rdrb);
            boolean content = false;
            String texta = "";
            String textb = "";
            while (true) {
                
                // start by collecting and moving past text content
                if (m_parserA.getEventType() == XmlPullParser.TEXT) {
                    texta = m_parserA.getText();
                    m_parserA.next();
                }
                if (m_parserB.getEventType() == XmlPullParser.TEXT) {
                    textb = m_parserB.getText();
                    m_parserB.next();
                }
                
                // now check actual tag state
                int typea = m_parserA.getEventType();
                int typeb = m_parserB.getEventType();
                if (typea != typeb) {
                    printError("Different document structure");
                    return false;
                } else if (typea == XmlPullParser.START_TAG) {
                    
                    // compare start tags, attributes, and prior text
                    content = true;
                    if (!matchNames()) {
                        printError("Different start tags");
                        return false;
                    } else if (!matchAttributes()) {
                        printError("Different attributes");
                        return false;
                    } else if (!texta.trim().equals(textb.trim())) {
                        printError("Different text content between elements");
                        return false;
                    }
                    texta = textb = "";
                    
                } else if (typea == XmlPullParser.END_TAG) {
                    
                    // compare end tags and prior text
                    if (!matchNames()) {
                        printError("Different end tags");
                        return false;
                    }
                    if (content) {
                        if (!texta.equals(textb)) {
                            printError("Different text content");
                            if (m_differencePrint != null) {
                                m_differencePrint.println(" \"" + texta +
                                "\" (length " + texta.length() + " vs. \"" +
                                textb + "\" (length " + textb.length() + ')');

                            }
                            return false;
                        }
                        content = false;
                    } else if (!texta.trim().equals(textb.trim())) {
                        printError("Different text content between elements");
                        return false;
                    }
                    texta = textb = "";
                    
                } else if (typea == XmlPullParser.END_DOCUMENT) {
                    return true;
                }
                
                // advance both parsers to next component
                m_parserA.next();
                m_parserB.next();
                
            }
        } catch (IOException ex) {
            if (m_differencePrint != null) {
                ex.printStackTrace(m_differencePrint);
            }
            return false;
        } catch (XmlPullParserException ex) {
            if (m_differencePrint != null) {
                ex.printStackTrace(m_differencePrint);
            }
            return false;
        }
    }
}