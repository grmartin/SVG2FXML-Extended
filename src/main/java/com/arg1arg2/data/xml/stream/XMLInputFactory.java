package com.arg1arg2.data.xml.stream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.transform.Source;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.eclipse.xtext.xbase.lib.Pair;

/**
 * Created by grmartin on 5/5/15.
 */
@SuppressWarnings("unused")
public class XMLInputFactory {
    private final javax.xml.stream.XMLInputFactory streamFactory;

    public static XMLInputFactory newInstance() {
        return new XMLInputFactory();
    }

    private XMLInputFactory() {
        this(javax.xml.stream.XMLInputFactory.newFactory());
    }

    private XMLInputFactory(javax.xml.stream.XMLInputFactory f) {
        this.streamFactory = f;
    }


    public Pair<XMLEventReader, StringReader> createXMLEventStringReaderPair(String string) throws XMLStreamException {
        return createXMLEventStringReaderPair(new StringReader(string));
    }

    public Pair<XMLEventReader, StringReader> createXMLEventStringReaderPair(StringReader reader) throws XMLStreamException {
        return new Pair<>(createXMLEventReader(reader), reader);
    }

    /* Delegation to wrapped javax class */
    public static XMLInputFactory newFactory(String factoryId, ClassLoader classLoader) throws FactoryConfigurationError {
        return new XMLInputFactory(javax.xml.stream.XMLInputFactory.newFactory(factoryId, classLoader));
    }

    public static XMLInputFactory newFactory() throws FactoryConfigurationError {
        return new XMLInputFactory(javax.xml.stream.XMLInputFactory.newFactory());
    }


    public XMLEventReader createXMLEventReader(Source source) throws XMLStreamException {
        return streamFactory.createXMLEventReader(source);
    }

    public void setEventAllocator(XMLEventAllocator allocator) {
        streamFactory.setEventAllocator(allocator);
    }

    public XMLReporter getXMLReporter() {
        return streamFactory.getXMLReporter();
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream) throws XMLStreamException {
        return streamFactory.createXMLStreamReader(stream);
    }

    public boolean isPropertySupported(String name) {
        return streamFactory.isPropertySupported(name);
    }


    public XMLStreamReader createFilteredReader(XMLStreamReader reader, javax.xml.stream.StreamFilter filter) throws XMLStreamException {
        return streamFactory.createFilteredReader(reader, filter);
    }

    public XMLEventReader createXMLEventReader(String systemId, Reader reader) throws XMLStreamException {
        return streamFactory.createXMLEventReader(systemId, reader);
    }

    public XMLEventReader createXMLEventReader(InputStream stream) throws XMLStreamException {
        return streamFactory.createXMLEventReader(stream);
    }

    public XMLStreamReader createXMLStreamReader(Reader reader) throws XMLStreamException {
        return streamFactory.createXMLStreamReader(reader);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, Reader reader) throws XMLStreamException {
        return streamFactory.createXMLStreamReader(systemId, reader);
    }

    public void setXMLResolver(XMLResolver resolver) {
        streamFactory.setXMLResolver(resolver);
    }

    public XMLStreamReader createXMLStreamReader(Source source) throws XMLStreamException {
        return streamFactory.createXMLStreamReader(source);
    }

    public XMLResolver getXMLResolver() {
        return streamFactory.getXMLResolver();
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return streamFactory.getProperty(name);
    }

    public XMLStreamReader createXMLStreamReader(String systemId, InputStream stream) throws XMLStreamException {
        return streamFactory.createXMLStreamReader(systemId, stream);
    }

    public XMLEventReader createXMLEventReader(String systemId, InputStream stream) throws XMLStreamException {
        return streamFactory.createXMLEventReader(systemId, stream);
    }

    public void setXMLReporter(XMLReporter reporter) {
        streamFactory.setXMLReporter(reporter);
    }

    public XMLEventReader createFilteredReader(XMLEventReader reader, javax.xml.stream.EventFilter filter) throws XMLStreamException {
        return streamFactory.createFilteredReader(reader, filter);
    }

    public XMLStreamReader createXMLStreamReader(InputStream stream, String encoding) throws XMLStreamException {
        return streamFactory.createXMLStreamReader(stream, encoding);
    }

    public void setProperty(String name, Object value) throws IllegalArgumentException {
        streamFactory.setProperty(name, value);
    }

    public XMLEventReader createXMLEventReader(XMLStreamReader reader) throws XMLStreamException {
        return streamFactory.createXMLEventReader(reader);
    }

    public XMLEventReader createXMLEventReader(Reader reader) throws XMLStreamException {
        return streamFactory.createXMLEventReader(reader);
    }

    public XMLEventReader createXMLEventReader(InputStream stream, String encoding) throws XMLStreamException {
        return streamFactory.createXMLEventReader(stream, encoding);
    }

    public XMLEventAllocator getEventAllocator() {
        return streamFactory.getEventAllocator();
    }
}
