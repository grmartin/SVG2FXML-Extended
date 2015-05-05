package com.arg1arg2.data.xml.stream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.xtext.xbase.lib.Pair;

/**
 * Created by grmartin on 5/5/15.
 */
@SuppressWarnings("unused")
public class XMLOutputFactory {
    private final javax.xml.stream.XMLOutputFactory streamFactory;

    public static XMLOutputFactory newInstance() {
        return new XMLOutputFactory();
    }

    private XMLOutputFactory() {
        this(javax.xml.stream.XMLOutputFactory.newFactory());
    }

    private XMLOutputFactory(javax.xml.stream.XMLOutputFactory f) {
        this.streamFactory = f;
    }

    public Pair<XMLEventWriter, StringWriter> createXMLEventStringWriterPair() throws XMLStreamException {
        return createXMLEventStringWriterPair(new StringWriter());
    }

    public Pair<XMLEventWriter, StringWriter> createXMLEventStringWriterPair(StringWriter writer) throws XMLStreamException {
        return new Pair<>(createXMLEventWriter(writer), writer);
    }

    /* Delegation to wrapped javax class */
    public static XMLOutputFactory newFactory(String factoryId, ClassLoader classLoader) throws FactoryConfigurationError {
        return new XMLOutputFactory(javax.xml.stream.XMLOutputFactory.newFactory(factoryId, classLoader));
    }

    public static XMLOutputFactory newFactory() throws FactoryConfigurationError {
        return new XMLOutputFactory(javax.xml.stream.XMLOutputFactory.newFactory());
    }

    public XMLStreamWriter createXMLStreamWriter(Writer stream) throws XMLStreamException {
        return streamFactory.createXMLStreamWriter(stream);
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream stream) throws XMLStreamException {
        return streamFactory.createXMLStreamWriter(stream);
    }

    public XMLStreamWriter createXMLStreamWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return streamFactory.createXMLStreamWriter(stream, encoding);
    }

    public XMLStreamWriter createXMLStreamWriter(Result result) throws XMLStreamException {
        return streamFactory.createXMLStreamWriter(result);
    }

    public XMLEventWriter createXMLEventWriter(Result result) throws XMLStreamException {
        return streamFactory.createXMLEventWriter(result);
    }

    public XMLEventWriter createXMLEventWriter(OutputStream stream) throws XMLStreamException {
        return streamFactory.createXMLEventWriter(stream);
    }

    public XMLEventWriter createXMLEventWriter(OutputStream stream, String encoding) throws XMLStreamException {
        return streamFactory.createXMLEventWriter(stream, encoding);
    }

    public XMLEventWriter createXMLEventWriter(Writer stream) throws XMLStreamException {
        return streamFactory.createXMLEventWriter(stream);
    }

    public void setProperty(String name, Object value) throws IllegalArgumentException {
        streamFactory.setProperty(name, value);
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return streamFactory.getProperty(name);
    }

    public boolean isPropertySupported(String name) {
        return streamFactory.isPropertySupported(name);
    }
}
