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

package org.jibx.runtime;

import java.io.IOException;

/**
 * XML writer interface used for output of marshalled document. This interface
 * allows easy substitution of different output formats, including parse event
 * stream equivalents. This makes heavy use of state information, so each
 * method call defined is only valid in certain states.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */
public interface IXMLWriter
{
    /**
     * Get the current element nesting depth. Elements are only counted in the
     * depth returned when they're officially open - after the start tag has
     * been output and before the end tag has been output.
     *
     * @return number of nested elements at current point in output
     */
    public int getNestingDepth();
    
    /**
     * Get the number of namespaces currently defined. This is equivalent to the
     * index of the next extension namespace added.
     *
     * @return namespace count
     */
    public int getNamespaceCount();
        
    /**
     * Set nesting indentation. This is advisory only, and implementations of
     * this interface are free to ignore it. The intent is to indicate that the
     * generated output should use indenting to illustrate element nesting.
     *
     * @param count number of character to indent per level, or disable
     * indentation if negative (zero means new line only)
     * @param newline sequence of characters used for a line ending
     * (<code>null</code> means use the single character '\n')
     * @param indent whitespace character used for indentation
     */
    public void setIndentSpaces(int count, String newline, char indent);
        
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
        throws IOException;
    
    /**
     * Generate open start tag. This allows attributes and/or namespace
     * declarations to be added to the start tag, but must be followed by a
     * {@link #closeStartTag} call.
     *
     * @param index namespace URI index number
     * @param name unqualified element name
     * @throws IOException on error writing to document
     */
    public void startTagOpen(int index, String name) throws IOException;
    
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
        int[] nums, String[] prefs) throws IOException;
     
    /**
     * Add attribute to current open start tag. This is only valid after a call
     * to {@link #startTagOpen} and before the corresponding call to {@link
     * #closeStartTag}.
     *
     * @param index namespace URI index number
     * @param name unqualified attribute name
     * @param value text value for attribute
     * @throws IOException on error writing to document
     */
    public void addAttribute(int index, String name, String value)
        throws IOException;
    
    /**
     * Close the current open start tag. This is only valid after a call to
     * {@link #startTagOpen}.
     *
     * @throws IOException on error writing to document
     */
    public void closeStartTag() throws IOException;
    
    /**
     * Close the current open start tag as an empty element. This is only valid
     * after a call to {@link #startTagOpen}.
     *
     * @throws IOException on error writing to document
     */
    public void closeEmptyTag() throws IOException;
    
    /**
     * Generate closed start tag. No attributes or namespaces can be added to a
     * start tag written using this call.
     *
     * @param index namespace URI index number
     * @param name unqualified element name
     * @throws IOException on error writing to document
     */
    public void startTagClosed(int index, String name) throws IOException;
    
    /**
     * Generate end tag.
     *
     * @param index namespace URI index number
     * @param name unqualified element name
     * @throws IOException on error writing to document
     */
    public void endTag(int index, String name) throws IOException;
    
    /**
     * Write ordinary character data text content to document.
     *
     * @param text content value text (must not be <code>null</code>)
     * @throws IOException on error writing to document
     */
    public void writeTextContent(String text) throws IOException;
    
    /**
     * Write CDATA text to document.
     *
     * @param text content value text (must not be <code>null</code>)
     * @throws IOException on error writing to document
     */
    public void writeCData(String text) throws IOException;
    
    /**
     * Write comment to document.
     *
     * @param text comment text (must not be <code>null</code>)
     * @throws IOException on error writing to document
     */
    public void writeComment(String text) throws IOException;
    
    /**
     * Write entity reference to document.
     *
     * @param name entity name (must not be <code>null</code>)
     * @throws IOException on error writing to document
     */
    public void writeEntityRef(String name) throws IOException;
    
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
        throws IOException;
    
    /**
     * Write processing instruction to document.
     *
     * @param target processing instruction target name (must not be
     * <code>null</code>)
     * @param data processing instruction data (must not be <code>null</code>)
     * @throws IOException on error writing to document
     */
    public void writePI(String target, String data) throws IOException;
    
    /**
     * Request output indent. The writer implementation should normally indent
     * output as appropriate. This method can be used to request indenting of
     * output that might otherwise not be indented. The normal effect when used
     * with a text-oriented writer should be to output the appropriate line end
     * sequence followed by the appropriate number of indent characters for the
     * current nesting level.
     *
     * @throws IOException on error writing to document
     */
    public void indent() throws IOException;
    
    /**
     * Flush document output. Forces out all output generated to this point.
     *
     * @throws IOException on error writing to document
     */
    public void flush() throws IOException;
    
    /**
     * Close document output. Completes writing of document output, including
     * closing the output medium.
     *
     * @throws IOException on error writing to document
     */
    public void close() throws IOException;
    
    /**
     * Reset to initial state for reuse. The context is serially reusable,
     * as long as this method is called to clear any retained state information
     * between uses. It is automatically called when output is set.
     */
    public void reset();
    
    /**
     * Get namespace URIs for mapping. This gets the full ordered array of
     * namespaces known in the binding used for this marshalling, where the
     * index number of each namespace URI is the namespace index used to lookup
     * the prefix when marshalling a name in that namespace. The returned array
     * must not be modified.
     *
     * @return array of namespaces
     */
    public String[] getNamespaces();
    
    /**
     * Get URI for namespace.
     *
     * @param index namespace URI index number
     * @return namespace URI text, or <code>null</code> if the namespace index
     * is invalid
     */
    public String getNamespaceUri(int index);
    
    /**
     * Get current prefix defined for namespace.
     *
     * @param index namespace URI index number
     * @return current prefix text, or <code>null</code> if the namespace is not
     * currently mapped
     */
    public String getNamespacePrefix(int index);
    
    /**
     * Get index of namespace mapped to prefix. This can be an expensive
     * operation with time proportional to the number of namespaces defined, so
     * it should be used with care.
     * 
     * @param prefix text to match  (non-<code>null</code>, use "" for default
     * prefix)
     * @return index namespace URI index number mapped to prefix
     */
    public int getPrefixIndex(String prefix);
    
    /**
     * Append extension namespace URIs to those in mapping.
     *
     * @param uris namespace URIs to extend those in mapping
     */
    public void pushExtensionNamespaces(String[] uris);
    
    /**
     * Remove extension namespace URIs. This removes the last set of
     * extension namespaces pushed using {@link #pushExtensionNamespaces}.
     */
    public void popExtensionNamespaces();
    
    /**
     * Get extension namespace URIs added to those in mapping. This gets the
     * current set of extension definitions. The returned arrays must not be
     * modified.
     *
     * @return array of arrays of extension namespaces (<code>null</code> if
     * none)
     */
    public String[][] getExtensionNamespaces();
    
    /**
     * Open the specified namespaces for use. This method is normally only
     * called internally, when namespace declarations are actually written to
     * output. It is exposed as part of this interface to allow for special
     * circumstances where namespaces are being written outside the usual
     * processing. The namespaces will remain open for use until the current
     * element is closed.
     *
     * @param nums array of namespace indexes defined by this element (must
     * be constant, reference is kept until namespaces are closed)
     * @param prefs array of namespace prefixes mapped by this element (no
     * <code>null</code> values, use "" for default namespace declaration)
     * @return array of indexes for namespaces not previously active (the ones
     * actually needing to be declared, in the case of text output)
     * @throws IOException on error writing to document
     */
    public int[] openNamespaces(int[] nums, String[] prefs) throws IOException;
}