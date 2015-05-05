package com.arg1arg2.data.xml.stream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.Location;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import java.util.Iterator;

/**
 * Created by grmartin on 5/5/15.
 */
@SuppressWarnings("unused")
public class XMLEventFactory {
    private final javax.xml.stream.XMLEventFactory streamFactory;

    public static XMLEventFactory newInstance() {
        return new XMLEventFactory();
    }

    private XMLEventFactory() {
        this(javax.xml.stream.XMLEventFactory.newFactory());
    }

    private XMLEventFactory(javax.xml.stream.XMLEventFactory f) {
        this.streamFactory = f;
    }

    public Attribute createAttribute(Namespace ns, String name, String value) {
        return createAttribute(ns.getPrefix(), ns.getNamespaceURI(), name, value);
    }

    /* Delegation to wrapped javax class */
    public static XMLEventFactory newFactory() throws FactoryConfigurationError {
        return new XMLEventFactory(javax.xml.stream.XMLEventFactory.newFactory());
    }

    public static XMLEventFactory newFactory(String factoryId, ClassLoader classLoader) throws FactoryConfigurationError {
        return new XMLEventFactory(javax.xml.stream.XMLEventFactory.newFactory(factoryId, classLoader));
    }

    public void setLocation(Location location) {
        streamFactory.setLocation(location);
    }

    public Attribute createAttribute(String prefix, String namespaceURI, String localName, String value) {
        return streamFactory.createAttribute(prefix, namespaceURI, localName, value);
    }

    public Attribute createAttribute(String localName, String value) {
        return streamFactory.createAttribute(localName, value);
    }

    public Attribute createAttribute(QName name, String value) {
        return streamFactory.createAttribute(name, value);
    }

    public Namespace createNamespace(String namespaceURI) {
        return streamFactory.createNamespace(namespaceURI);
    }

    public Namespace createNamespace(String prefix, String namespaceUri) {
        return streamFactory.createNamespace(prefix, namespaceUri);
    }

    public StartElement createStartElement(QName name, Iterator attributes, Iterator namespaces) {
        return streamFactory.createStartElement(name, attributes, namespaces);
    }

    public StartElement createStartElement(String prefix, String namespaceUri, String localName) {
        return streamFactory.createStartElement(prefix, namespaceUri, localName);
    }

    public StartElement createStartElement(String prefix, String namespaceUri, String localName, Iterator attributes, Iterator namespaces) {
        return streamFactory.createStartElement(prefix, namespaceUri, localName, attributes, namespaces);
    }

    public StartElement createStartElement(String prefix, String namespaceUri, String localName, Iterator attributes, Iterator namespaces, NamespaceContext context) {
        return streamFactory.createStartElement(prefix, namespaceUri, localName, attributes, namespaces, context);
    }

    public EndElement createEndElement(QName name, Iterator namespaces) {
        return streamFactory.createEndElement(name, namespaces);
    }

    public EndElement createEndElement(String prefix, String namespaceUri, String localName) {
        return streamFactory.createEndElement(prefix, namespaceUri, localName);
    }

    public EndElement createEndElement(String prefix, String namespaceUri, String localName, Iterator namespaces) {
        return streamFactory.createEndElement(prefix, namespaceUri, localName, namespaces);
    }

    public Characters createCharacters(String content) {
        return streamFactory.createCharacters(content);
    }

    public Characters createCData(String content) {
        return streamFactory.createCData(content);
    }

    public Characters createSpace(String content) {
        return streamFactory.createSpace(content);
    }

    public Characters createIgnorableSpace(String content) {
        return streamFactory.createIgnorableSpace(content);
    }

    public StartDocument createStartDocument() {
        return streamFactory.createStartDocument();
    }

    public StartDocument createStartDocument(String encoding, String version, boolean standalone) {
        return streamFactory.createStartDocument(encoding, version, standalone);
    }

    public StartDocument createStartDocument(String encoding, String version) {
        return streamFactory.createStartDocument(encoding, version);
    }

    public StartDocument createStartDocument(String encoding) {
        return streamFactory.createStartDocument(encoding);
    }

    public EndDocument createEndDocument() {
        return streamFactory.createEndDocument();
    }

    public EntityReference createEntityReference(String name, EntityDeclaration declaration) {
        return streamFactory.createEntityReference(name, declaration);
    }

    public Comment createComment(String text) {
        return streamFactory.createComment(text);
    }

    public ProcessingInstruction createProcessingInstruction(String target, String data) {
        return streamFactory.createProcessingInstruction(target, data);
    }

    public DTD createDTD(String dtd) {
        return streamFactory.createDTD(dtd);
    }

}
