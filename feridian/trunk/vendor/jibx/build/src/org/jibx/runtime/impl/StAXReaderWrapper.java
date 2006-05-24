package org.jibx.runtime.impl;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jibx.runtime.IXMLReader;
import org.jibx.runtime.IntStack;
import org.jibx.runtime.JiBXException;

/**
 * Wrapper for a StAX parser implementation. This delegates most calls
 * more or less directly, only adding the required namespace functionality
 * on top of the StAX API.
 */
public class StAXReaderWrapper implements IXMLReader
{
    /** Event type code translation array. Indexed by the StAX event code, it
    returns the corresponding XML reader event code. */
    static final byte[] s_eventTranslations = new byte[256];
    static {
        s_eventTranslations[XMLStreamConstants.CDATA] = IXMLReader.CDSECT;
        s_eventTranslations[XMLStreamConstants.CHARACTERS] = IXMLReader.TEXT;
        s_eventTranslations[XMLStreamConstants.COMMENT] = IXMLReader.COMMENT;
        s_eventTranslations[XMLStreamConstants.DTD] = IXMLReader.DOCDECL;
        s_eventTranslations[XMLStreamConstants.END_DOCUMENT] =
            IXMLReader.END_DOCUMENT;
        s_eventTranslations[XMLStreamConstants.END_ELEMENT] =
            IXMLReader.END_TAG;
        s_eventTranslations[XMLStreamConstants.ENTITY_REFERENCE] =
            IXMLReader.ENTITY_REF;
        s_eventTranslations[XMLStreamConstants.PROCESSING_INSTRUCTION] =
            IXMLReader.PROCESSING_INSTRUCTION;
        s_eventTranslations[XMLStreamConstants.SPACE] =
            IXMLReader.IGNORABLE_WHITESPACE;
        s_eventTranslations[XMLStreamConstants.START_DOCUMENT] =
            IXMLReader.START_DOCUMENT;
        s_eventTranslations[XMLStreamConstants.START_ELEMENT] =
            IXMLReader.START_TAG;
    }
   
    /** Actual parser. */
    private final XMLStreamReader m_parser;
    
    /** Parser processing namespaces flag. */
    final boolean m_isNamespaceAware;
    
    /** Document name. */
    private final String m_docName;
    
    /** Current element nesting depth. */
    int m_nestingDepth;
    
    /** Namespace definitions in scope at each nesting depth. */
    private IntStack m_inScopeCounts;
    
    /** Namespace URIs in scope. */
    private StringArray m_inScopeUris;
    
    /** Namespace prefixes in scope. */
    private StringArray m_inScopePrefixes;
    
    /** Accumulated text for return. */
    private String m_accumulatedText;
    
    /** Accumulated text is processing instruction flag (otherwise content) */
    private boolean m_isProcessingInstruction;
    
    /** Document encoding (apparently cannot be read after parse done). */
    private String m_encoding;
    
    /**
     * Constructor used by factory.
     * 
     * @param rdr event reader
     * @param nsa namespace aware flag
     */
    public StAXReaderWrapper(XMLStreamReader rdr, String name, boolean nsa) {
        m_parser = rdr;
        m_docName = name;
        m_isNamespaceAware = nsa;
        m_inScopeCounts = new IntStack();
        m_inScopeCounts.push(0);
        m_inScopeUris = new StringArray();
        m_inScopePrefixes = new StringArray();
    }
    
    /**
     * Build current parse input position description.
     *
     * @return text description of current parse position
     */
    public String buildPositionString() {
        Location location = m_parser.getLocation();
        String base = "(line " + location.getLineNumber() + ", col " +
            location.getColumnNumber();
        if (m_docName != null) {
            base += ", in " + m_docName;
        }
        return base + ')';
    }
    
    /**
     * Handle start tag. This increments the nesting count, and records all
     * namespaces associated with the start tag.
     */
    private void startTag() {
        if (m_nestingDepth == 0) {
            m_encoding = m_parser.getEncoding();
            if (m_encoding == null) {
                m_encoding = m_parser.getCharacterEncodingScheme();
                if (m_encoding == null) {
                    m_encoding = "UTF-8";
                }
            }
        }
        m_nestingDepth++;
        int count = m_parser.getNamespaceCount();
        for (int i = 0; i < count; i++) {
            m_inScopeUris.add(m_parser.getNamespaceURI(i));
            m_inScopePrefixes.add(m_parser.getNamespacePrefix(i));
        }
        m_inScopeCounts.push(m_inScopeUris.size());
    }
    
    /**
     * Handle end tag. This decrements the nesting count, and deletes all
     * namespaces associated with the start tag.
     */
    private void endTag() {
        m_nestingDepth--;
        int count = m_inScopeCounts.pop() - m_inScopeCounts.peek();
        if (count > 0) {
            m_inScopeUris.remove(count);
            m_inScopePrefixes.remove(count);
        }
    }
    
    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#nextToken()
     */
    public int nextToken() throws JiBXException {
        if (m_accumulatedText == null) {
            try {
                int code;
                loop: while (true) {
                    code = s_eventTranslations[m_parser.next()];
                    switch (code) {
                        
                        case START_TAG:
                            startTag();
                            break loop;
                            
                        case END_TAG:
                            endTag();
                            break loop;
                            
                        case PROCESSING_INSTRUCTION:
                            m_accumulatedText = m_parser.getPITarget() + ' ' +
                                m_parser.getPIData();
                            m_isProcessingInstruction = true;
                            while (s_eventTranslations[m_parser.next()] == 0);
                            break loop;
                            
                        case 0:
                            break;
                            
                        default:
                            break loop;
                    }
                }
                return code;
                
            } catch (XMLStreamException e) {
                throw new JiBXException
                    ("Error parsing document " + buildPositionString(), e);
            }
        } else {
            m_accumulatedText = null;
            m_isProcessingInstruction = false;
            int code = s_eventTranslations[m_parser.getEventType()];
            if (code == START_TAG) {
                startTag();
            } else if (code == END_TAG) {
                endTag();
            }
            return code;
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#next()
     */
    public int next() throws JiBXException {
        String text = null;
        StringBuffer buff = null;
        try {
            int type;
            if (m_accumulatedText == null) {
                m_parser.next();
            } else {
                m_accumulatedText = null;
                m_isProcessingInstruction = false;
            }
            loop: while (true) {
                type = s_eventTranslations[m_parser.getEventType()];
                switch (type) {

                    case ENTITY_REF:
                        if (m_parser.getText() == null) {
                            throw new JiBXException
                                ("Unexpanded entity reference in text at " +
                                buildPositionString());
                        }
                        // fall through into text accumulation

                    case CDSECT:
                    case TEXT:
                        if (text == null) {
                            text = m_parser.getText();
                        } else {
                            if (buff == null) {
                                buff = new StringBuffer(text);
                            }
                            buff.append(m_parser.getTextCharacters());
                        }
                        break;

                    case END_TAG:
                        if (text == null) {
                            endTag();
                            return type;
                        }
                        break loop;
                        
                    case START_TAG:
                        if (text == null) {
                            startTag();
                            return type;
                        }
                        break loop;
                        
                    case END_DOCUMENT:
                        if (text == null) {
                            return type;
                        }
                        break loop;

                    default:
                        break;

                }
                m_parser.next();
            }
            if (buff == null) {
                m_accumulatedText = text;
            } else {
                m_accumulatedText = buff.toString();
            }
            return TEXT;
        } catch (XMLStreamException e) {
            throw new JiBXException
                ("Error parsing document " + buildPositionString(), e);
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getEventType()
     */
    public int getEventType() throws JiBXException {
        if (m_accumulatedText == null) {
            return s_eventTranslations[m_parser.getEventType()];
        } else if (m_isProcessingInstruction) {
            return PROCESSING_INSTRUCTION;
        } else {
            return TEXT;
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getName()
     */
    public String getName() {
        return m_parser.getLocalName();
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getNamespace()
     */
    public String getNamespace() {
        String uri = m_parser.getNamespaceURI();
        if (uri == null) {
            return "";
        } else {
            return uri;
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getPrefix()
     */
    public String getPrefix() {
        String prefix = m_parser.getPrefix();
        if (prefix != null && prefix.length() == 0) {
            return null;
        } else {
            return prefix;
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getAttributeCount()
     */
    public int getAttributeCount() {
        return m_parser.getAttributeCount();
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getAttributeName(int)
     */
    public String getAttributeName(int index) {
        try {
            return m_parser.getAttributeLocalName(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getAttributeNamespace(int)
     */
    public String getAttributeNamespace(int index) {
        try {
            String uri = m_parser.getAttributeNamespace(index);
            if (uri == null) {
                return "";
            } else {
                return uri;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getAttributePrefix(int)
     */
    public String getAttributePrefix(int index) {
        try {
            String prefix = m_parser.getAttributePrefix(index);
            if (prefix != null && prefix.length() == 0) {
                return null;
            } else {
                return prefix;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getAttributeValue(int)
     */
    public String getAttributeValue(int index) {
        try {
            return m_parser.getAttributeValue(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getAttributeValue(java.lang.String, java.lang.String)
     */
    public String getAttributeValue(String ns, String name) {
        try {
            return m_parser.getAttributeValue(ns, name);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getText()
     */
    public String getText() {
        if (m_accumulatedText == null) {
            return m_parser.getText();
        } else {
            return m_accumulatedText;
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getNestingDepth()
     */
    public int getNestingDepth() {
        return m_nestingDepth;
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getNamespaceCount(int)
     */
    public int getNamespaceCount(int depth) {
        return m_inScopeCounts.peek(m_nestingDepth-depth);
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getNamespaceUri(int)
     */
    public String getNamespaceUri(int index) {
        return m_inScopeUris.get(index);
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getNamespacePrefix(int)
     */
    public String getNamespacePrefix(int index) {
        String prefix = m_inScopePrefixes.get(index);
        if (prefix != null && prefix.length() == 0) {
            return null;
        } else {
            return prefix;
        }
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getDocumentName()
     */
    public String getDocumentName() {
        return m_docName;
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getLineNumber()
     */
    public int getLineNumber() {
        return m_parser.getLocation().getLineNumber();
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getColumnNumber()
     */
    public int getColumnNumber() {
        return m_parser.getLocation().getColumnNumber();
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getNamespace(java.lang.String)
     */
    public String getNamespace(String prefix) {
        int index = m_inScopePrefixes.size();
        while (--index >= 0) {
            if (prefix.equals(m_inScopePrefixes.get(index))) {
                return m_inScopeUris.get(index);
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#getInputEncoding()
     */
    public String getInputEncoding() {
        return m_encoding;
    }

    /* (non-Javadoc)
     * @see org.jibx.runtime.IXMLReader#isNamespaceAware()
     */
    public boolean isNamespaceAware() {
        return m_isNamespaceAware;
    }
}