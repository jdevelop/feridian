/*
Copyright (c) 2004-2005, Dennis M. Sosnoski.
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

package org.jibx.runtime.impl;

import java.io.IOException;

import org.jibx.runtime.IXMLWriter;

/**
 * Base implementation of XML writer interface. This provides common handling of
 * indentation and formatting that can be used for all forms of text output.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */
public abstract class XMLWriterBase extends XMLWriterNamespaceBase
{
    /** Flag for current element has text content. */
    protected boolean m_textSeen;
    
    /** Flag for current element has content. */
    protected boolean m_contentSeen;
    
    /**
     * Constructor.
     *
     * @param uris ordered array of URIs for namespaces used in document (must
     * be constant; the value in position 0 must always be the empty string "",
     * and the value in position 1 must always be the XML namespace
     * "http://www.w3.org/XML/1998/namespace")
     */
    public XMLWriterBase(String[] uris) {
        super(uris);
    }
    
    /**
     * Write markup text to output. Markup text can be written directly to the
     * output without the need for any escaping, but still needs to be properly
     * encoded.
     *
     * @param text markup text to be written
     * @throws IOException if error writing to document
     */
    protected abstract void writeMarkup(String text) throws IOException;
    
    /**
     * Write markup character to output. Markup text can be written directly to
     * the output without the need for any escaping, but still needs to be
     * properly encoded.
     *
     * @param chr markup character to be written
     * @throws IOException if error writing to document
     */
    protected abstract void writeMarkup(char chr) throws IOException;
    
    /**
     * Write namespace prefix to output. This internal method is used to throw
     * an exception when an undeclared prefix is used.
     *
     * @param index namespace URI index number
     * @throws IOException if error writing to document
     */
    protected abstract void writePrefix(int index) throws IOException;
    
    /**
     * Write attribute text to output. This needs to write the text with any
     * appropriate escaping.
     *
     * @param text attribute value text to be written
     * @throws IOException if error writing to document
     */
    protected abstract void writeAttributeText(String text) throws IOException;
    
    /**
     * Request output indent with bias from current element nesting level. This
     * is used internally for proper indenting in special cases.
     *
     * @throws IOException on error writing to document
     */
    protected abstract void indent(int bias) throws IOException;
        
    /**
     * Write XML declaration to document. This can only be called before any
     * other methods in the interface are called.
     *
     * @param version XML version text
     * @param encoding text for encoding attribute (unspecified if
     * <code>null</code>)
     * @param standalone text for standalone attribute (unspecified if
     * <code>null</code>)
     * @throws IOException on error writing to document
     */
    public void writeXMLDecl(String version, String encoding, String standalone)
        throws IOException {
        writeMarkup("<?xml version=\"");
        writeAttributeText(version);
        if (encoding != null) {
            writeMarkup("\" encoding=\"");
            writeAttributeText(encoding);
        }
        if (standalone != null) {
            writeMarkup("\" standalone=\"");
            writeAttributeText(standalone);
        }
        writeMarkup("\"?>");
    }
    
    /**
     * Generate open start tag. This allows attributes to be added to the start
     * tag, but must be followed by a {@link #closeStartTag} call.
     *
     * @param index namespace URI index number
     * @param name unqualified element name
     * @throws IOException on error writing to document
     */
    public void startTagOpen(int index, String name) throws IOException {
        indent();
        writeMarkup('<');
        writePrefix(index);
        writeMarkup(name);
    }
    
    /**
     * Generate start tag for element with namespaces. This creates the actual
     * start tag, along with any necessary namespace declarations. Previously
     * active namespace declarations are not duplicated. The tag is
     * left incomplete, allowing other attributes to be added.
     *
     * @param index namespace URI index number
     * @param name element name
     * @param nums array of namespace indexes defined by this element (must
     * be constant, reference is kept until end of element)
     * @param prefs array of namespace prefixes mapped by this element (no
     * <code>null</code> values, use "" for default namespace declaration)
     * @throws IOException on error writing to document
     */
    public void startTagNamespaces(int index, String name,
        int[] nums, String[] prefs) throws IOException {
        
        // find the namespaces actually being declared
        int[] deltas = openNamespaces(nums, prefs);
        
        // create the start tag for element
        startTagOpen(index, name);
        
        // add namespace declarations to open element
        for (int i = 0; i < deltas.length; i++) {
            int slot = deltas[i];
            String prefix = getNamespacePrefix(slot);
            if (prefix.length() > 0) {
                writeMarkup(" xmlns:");
                writeMarkup(prefix);
                writeMarkup("=\"");
            } else {
                writeMarkup(" xmlns=\"");
            }
            writeAttributeText(getNamespaceUri(slot));
            writeMarkup('"');
        }
    }
    
    /**
     * Add attribute to current open start tag. This is only valid after a call
     * to {@link #startTagOpen} or {@link #startTagNamespaces} and before the
     * corresponding call to {@link #closeStartTag}.
     *
     * @param index namespace URI index number
     * @param name unqualified attribute name
     * @param value text value for attribute
     * @throws IOException on error writing to document
     */
    public void addAttribute(int index, String name, String value)
        throws IOException {
        writeMarkup(" ");
        writePrefix(index);
        writeMarkup(name);
        writeMarkup("=\"");
        writeAttributeText(value);
        writeMarkup('"');
    }
    
    /**
     * Close the current open start tag. This is only valid after a call to
     * {@link #startTagOpen}.
     *
     * @throws IOException on error writing to document
     */
    public void closeStartTag() throws IOException {
        writeMarkup('>');
        incrementNesting();
        m_textSeen = m_contentSeen = false;
    }
    
    /**
     * Close the current open start tag as an empty element. This is only valid
     * after a call to {@link #startTagOpen}.
     *
     * @throws IOException on error writing to document
     */
    public void closeEmptyTag() throws IOException {
        writeMarkup("/>");
        incrementNesting();
        decrementNesting();
        m_contentSeen = true;
    }
    
    /**
     * Generate closed start tag. No attributes or namespaces can be added to a
     * start tag written using this call.
     *
     * @param index namespace URI index number
     * @param name unqualified element name
     * @throws IOException on error writing to document
     */
    public void startTagClosed(int index, String name) throws IOException {
        indent();
        writeMarkup('<');
        writePrefix(index);
        writeMarkup(name);
        writeMarkup('>');
        incrementNesting();
        m_textSeen = m_contentSeen = false;
    }
    
    /**
     * Generate end tag.
     *
     * @param index namespace URI index number
     * @param name unqualified element name
     * @throws IOException on error writing to document
     */
    public void endTag(int index, String name) throws IOException {
        if (m_contentSeen && !m_textSeen) {
            indent(-1);
        }
        writeMarkup("</");
        writePrefix(index);
        writeMarkup(name);
        writeMarkup('>');
        decrementNesting();
        m_textSeen = false;
        m_contentSeen = true;
    }
    
    /**
     * Write comment to document.
     *
     * @param text comment text
     * @throws IOException on error writing to document
     */
    public void writeComment(String text) throws IOException {
        writeMarkup("<!--");
        writeMarkup(text);
        writeMarkup("-->");
    }
    
    /**
     * Write entity reference to document.
     *
     * @param name entity name
     * @throws IOException on error writing to document
     */
    public void writeEntityRef(String name) throws IOException {
        writeMarkup('&');
        writeMarkup(name);
        writeMarkup(';');
        m_contentSeen = true;
    }
    
    /**
     * Write DOCTYPE declaration to document.
     *
     * @param name root element name
     * @param sys system ID (<code>null</code> if none, must be
     * non-<code>null</code> for public ID to be used)
     * @param pub public ID (<code>null</code> if none)
     * @param subset internal subset (<code>null</code> if none)
     * @throws IOException on error writing to document
     */
    public void writeDocType(String name, String sys, String pub, String subset)
        throws IOException {
        indent();
        writeMarkup("<!DOCTYPE ");
        writeMarkup(name);
        writeMarkup(' ');
        if (sys != null) {
            if (pub == null) {
                writeMarkup("SYSTEM \"");
                writeMarkup(sys);
            } else {
                writeMarkup("PUBLIC \"");
                writeMarkup(pub);
                writeMarkup("\" \"");
                writeMarkup(sys);
            }
            writeMarkup('"');
        }
        if (subset != null) {
            writeMarkup('[');
            writeMarkup(subset);
            writeMarkup(']');
        }
        writeMarkup('>');
    }
    
    /**
     * Write processing instruction to document.
     *
     * @param target processing instruction target name
     * @param data processing instruction data
     * @throws IOException on error writing to document
     */
    public void writePI(String target, String data) throws IOException {
        indent();
        writeMarkup("<?");
        writeMarkup(target);
        writeMarkup(' ');
        writeMarkup(data);
        writeMarkup("?>");
        m_contentSeen = true;
    }
    
    /**
     * Flush document output. Forces out all output generated to this point.
     *
     * @throws IOException on error writing to document
     */
    public abstract void flush() throws IOException;
    
    /**
     * Close document output. Completes writing of document output, including
     * closing the output medium.
     *
     * @throws IOException on error writing to document
     */
    public abstract void close() throws IOException;
    
    /**
     * Reset to initial state for reuse. The writer is serially reusable,
     * as long as this method is called to clear any retained state information
     * between uses. It is automatically called when output is set.
     */
    public void reset() {
        m_textSeen = m_contentSeen = false;
        super.reset();
    }
    
    /**
     * Create a child writer instance to be used for a separate binding. The
     * child writer inherits the stream and encoding from this writer, while
     * using the supplied namespace URIs.
     * 
     * @param uris ordered array of URIs for namespaces used in document
     * (see {@link #XMLWriterBase(String[])})
     * @return child writer
     */
    public abstract IXMLWriter createChildWriter(String[] uris);
}