/*
Copyright (c) 2004, Dennis M. Sosnoski.
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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Base handler for marshalling text document to an output stream. This is
 * designed for use with character encodings that use standard one-byte values
 * for Unicode characters in the 0x20-0x7F range, which includes the most
 * widely used encodings for XML documents. It needs to be subclassed with
 * implementation methods specific to the encoding used.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */
public abstract class StreamWriterBase extends XMLWriterBase
{
    //
    // Defined entities and special sequences as bytes
    
    protected static final byte[] QUOT_ENTITY =
    {
        (byte)'&', (byte)'q', (byte)'u', (byte)'o', (byte)'t', (byte)';'
    };
    protected static final byte[] AMP_ENTITY =
    {
        (byte)'&', (byte)'a', (byte)'m', (byte)'p', (byte)';'
    };
    protected static final byte[] GT_ENTITY =
    {
        (byte)'&', (byte)'g', (byte)'t', (byte)';'
    };
    protected static final byte[] LT_ENTITY =
    {
        (byte)'&', (byte)'l', (byte)'t', (byte)';'
    };
    protected static final byte[] LT_CDATASTART =
    {
        (byte)'<', (byte)'!', (byte)'[', (byte)'C', (byte)'D', (byte)'A',
        (byte)'T', (byte)'A', (byte)'['
    };
    protected static final byte[] LT_CDATAEND =
    {
        (byte)']', (byte)']', (byte)'>'
    };
    
    /** Default output buffer size. */
    private static final int INITIAL_BUFFER_SIZE = 2048;
    
    /** Name of encoding used for stream. */
    private final String m_encodingName;
    
    /** Stream for text output. */
    private OutputStream m_stream;
    
    /** Buffer for accumulating output bytes. */
    protected byte[] m_buffer;
    
    /** Current offset in filling buffer. */
    protected int m_fillOffset;
    
    /** Byte sequences for prefixes of namespaces in scope. */
    protected byte[][] m_prefixBytes;
    
    /** Byte sequences for prefixes of extension namespaces in scope. */
    protected byte[][][] m_extensionBytes;
    
    /** Indent tags for pretty-printed text. */
    private boolean m_indent;
    
    /** Base number of characters in indent sequence (end of line only). */
    private int m_indentBase;
    
    /** Number of extra characters in indent sequence per level of nesting. */
    private int m_indentPerLevel;
    
    /** Raw text for indentation sequences. */
    private byte[] m_indentSequence;
    
    /**
     * Constructor.
     *
     * @param enc character encoding used for output to streams
     * @param uris ordered array of URIs for namespaces used in document (must
     * be constant; the value in position 0 must always be the empty string "",
     * and the value in position 1 must always be the XML namespace
     * "http://www.w3.org/XML/1998/namespace")
     */
    public StreamWriterBase(String enc, String[] uris) {
        super(uris);
        m_encodingName = enc;
        m_prefixBytes = new byte[uris.length][];
        m_buffer = new byte[INITIAL_BUFFER_SIZE];
    }
    
    /**
     * Copy constructor. This takes the stream and encoding information from a
     * supplied instance, while setting a new array of namespace URIs. It's
     * intended for use when invoking one binding from within another binding.
     *
     * @param base instance to be used as base for writer
     * @param uris ordered array of URIs for namespaces used in document
     * (see {@link #StreamWriterBase(String, String[])})
     */
    public StreamWriterBase(StreamWriterBase base, String[] uris) {
        this(base.m_encodingName, uris);
        m_stream = base.m_stream;
        m_indent = base.m_indent;
        m_indentBase = base.m_indentBase;
        m_indentPerLevel = base.m_indentPerLevel;
        m_indentSequence = base.m_indentSequence;
    }
    
    /**
     * Set output stream. If an output stream is currently open when this is
     * called the existing stream is flushed and closed, with any errors
     * ignored.
     *
     * @param outs stream for document data output
     */
    public void setOutput(OutputStream outs) {
        try {
            close();
        } catch (IOException e) { /* deliberately empty */ }
        m_stream = outs;
        reset();
    }
    
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
    public void setIndentSpaces(int count, String newline, char indent) {
        if (count >= 0) {
            try {
                if (newline == null) {
                    newline = "\n";
                }
                m_indent = true;
                byte[] base = newline.getBytes(m_encodingName);
                m_indentBase = base.length;
                byte[] per = new String(new char[]
                    { indent }).getBytes(m_encodingName);
                m_indentPerLevel = count * per.length;
                int length = m_indentBase + m_indentPerLevel * 10;
                m_indentSequence = new byte[length];
                for (int i = 0; i < length; i++) {
                    if (i < newline.length()) {
                        m_indentSequence[i] = base[i];
                    } else {
                        int index = (i - m_indentBase) % per.length;
                        m_indentSequence[i] = per[index];
                    }
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException
                    ("Encoding " + m_encodingName + " not recognized by JVM");
            }
        } else {
            m_indent = false;
        }
    }
    
    /**
     * Make at least the requested number of bytes available in the output
     * buffer. If necessary, the output buffer will be replaced by a larger
     * buffer.
     *
     * @param length number of bytes space to be made available
     * @throws IOException if error writing to document
     */
    protected void makeSpace(int length) throws IOException {
        if (m_fillOffset + length > m_buffer.length) {
            m_stream.write(m_buffer, 0, m_fillOffset);
            m_fillOffset = 0;
            if (length > m_buffer.length) {
                m_buffer = new byte[Math.max(length, m_buffer.length*2)];
            }
        }
    }
    
    /**
     * Report that namespace has been undefined.
     *
     * @param index namespace URI index number
     */
    protected void undefineNamespace(int index) {
        if (index < m_prefixBytes.length) {
            m_prefixBytes[index] = null;
        } else if (m_extensionBytes != null) {
            index -= m_prefixes.length;
            for (int i = 0; i < m_extensionBytes.length; i++) {
                int length = m_extensionBytes[i].length;
                if (index < length) {
                    m_extensionBytes[i][index] = null;
                    break;
                } else {
                    index -= length;
                }
            }
        } else {
            throw new IllegalArgumentException("Index out of range");
        }
    }
    
    /**
     * Write namespace prefix to output. This internal method is used to throw
     * an exception when an undeclared prefix is used.
     *
     * @param index namespace URI index number
     * @throws IOException if error writing to document
     */
    protected void writePrefix(int index) throws IOException {
        try {
            byte[] bytes = null;
            if (index < m_prefixBytes.length) {
                bytes = m_prefixBytes[index];
            } else if (m_extensionBytes != null) {
                index -= m_prefixes.length;
                for (int i = 0; i < m_extensionBytes.length; i++) {
                    int length = m_extensionBytes[i].length;
                    if (index < length) {
                        bytes = m_extensionBytes[i][index];
                        break;
                    } else {
                        index -= length;
                    }
                }
            }
            makeSpace(bytes.length);
            System.arraycopy(bytes, 0, m_buffer, m_fillOffset, bytes.length);
            m_fillOffset += bytes.length;
        } catch (NullPointerException ex) {
            throw new IOException("Namespace URI has not been declared.");
        }
    }
    
    /**
     * Write entity bytes to output. 
     *
     * @param bytes actual bytes to be written
     * @param offset starting offset in buffer
     * @return offset for next data byte in buffer
     */
    protected int writeEntity(byte[] bytes, int offset) {
        System.arraycopy(bytes, 0, m_buffer, offset, bytes.length);
        return offset + bytes.length;
    }
    
    /**
     * Append extension namespace URIs to those in mapping.
     *
     * @param uris namespace URIs to extend those in mapping
     */
    public void pushExtensionNamespaces(String[] uris) {
        super.pushExtensionNamespaces(uris);
        byte[][] items = new byte[uris.length][];
        if (m_extensionBytes == null) {
            m_extensionBytes = new byte[][][] { items };
        } else {
            int length = m_extensionBytes.length;
            byte[][][] grow = new byte[length+1][][];
            System.arraycopy(m_extensionBytes, 0, grow, 0, length);
            grow[length] = items;
            m_extensionBytes = grow;
        }
    }
    
    /**
     * Remove extension namespace URIs. This removes the last set of
     * extension namespaces pushed using {@link #pushExtensionNamespaces}.
     */
    public void popExtensionNamespaces() {
        super.popExtensionNamespaces();
        int length = m_extensionBytes.length;
        if (length == 1) {
            m_extensionBytes = null;
        } else {
            byte[][][] shrink = new byte[length-1][][];
            System.arraycopy(m_extensionBytes, 0, shrink, 0, length-1);
            m_extensionBytes = shrink;
        }
    }
    
    /**
     * Request output indent. Output the line end sequence followed by the
     * appropriate number of indent characters.
     * 
     * @param bias indent depth difference (positive or negative) from current
     * element nesting depth
     * @throws IOException on error writing to document
     */
    public void indent(int bias) throws IOException {
        if (m_indent) {
            int length = m_indentBase +
                (getNestingDepth() + bias) * m_indentPerLevel;
            if (length > m_indentSequence.length) {
                int use = Math.max(length,
                    m_indentSequence.length*2 - m_indentBase);
                byte[] grow = new byte[use];
                System.arraycopy(m_indentSequence, 0, grow, 0,
                    m_indentSequence.length);
                for (int i = m_indentSequence.length; i < use; i++) {
                    grow[i] = grow[m_indentBase];
                }
                m_indentSequence = grow;
            }
            makeSpace(length);
            System.arraycopy(m_indentSequence, 0, m_buffer, m_fillOffset,
                length);
            m_fillOffset += length;
        }
    }
    
    /**
     * Request output indent. Output the line end sequence followed by the
     * appropriate number of indent characters for the current nesting level.
     * 
     * @throws IOException on error writing to document
     */
    public void indent() throws IOException {
        indent(0);
    }
    
    /**
     * Flush document output. Forces out all output generated to this point.
     *
     * @throws IOException on error writing to document
     */
    public void flush() throws IOException {
        if (m_stream != null) {
            indent();
            m_stream.write(m_buffer, 0, m_fillOffset);
            m_fillOffset = 0;
            m_stream.flush();
        }
    }
    
    /**
     * Close document output. Completes writing of document output, including
     * closing the output medium.
     *
     * @throws IOException on error writing to document
     */
    public void close() throws IOException {
        flush();
        if (m_stream != null) {
            m_stream.close();
            m_stream = null;
        }
    }
    
    /**
     * Reset to initial state for reuse. This override of the base class
     * method handles clearing the internal buffer when starting a new
     * document.
     */
    public void reset() {
        super.reset();
        m_fillOffset = 0;
    }
}
